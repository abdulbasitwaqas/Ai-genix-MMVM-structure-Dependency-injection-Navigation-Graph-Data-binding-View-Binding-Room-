package com.jsbl.genix.di.modules

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.network.LocalApi
import com.jsbl.genix.network.LocalService
import com.jsbl.genix.utils.APP_TAG
import com.jsbl.genix.utils.SharePreferencesHelper
import com.jsbl.genix.utils.logD
import com.scope.portalapiclient.db.InsuredVehicleConverter.Companion.gson
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


@Module
class APIModule {

    @Provides
    fun provideOkHttpClient(application: Application): OkHttpClient {
        return generateSecureOkHttpClient(application)
    }


    @Provides
    fun provideNetworkApi(client: OkHttpClient): LocalApi {
        val contentType = "application/json".toMediaType()
        val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(LocalApi::class.java)
    }

    @Provides
//    @typeOfService(API_SERVIE_LOCAL)
    fun provideRegistrationService(application: Application): LocalService {
        return LocalService(application)
    }

    /**
     * Generates an OkHttpClient with our trusted CAs
     * to make calls to a service which requires it.
     *
     * @param context the context to access our file.
     * @return OkHttpClient with our trusted CAs added.
     */
    private fun generateSecureOkHttpClient(application: Context): OkHttpClient {
        // Create a simple builder for our http client, this is only por example purposes

        var preferencesHelper: SharePreferencesHelper = SharePreferencesHelper(application)
        val logger = HttpLoggingInterceptor()
        logger.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        var httpClientBuilder = getUnsafeOkHttpClient(application)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logger)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()
                    val request: Request = original.newBuilder()
                        .header("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + preferencesHelper.getAuth())
                        .header("USER",preferencesHelper.encryptUserId(preferencesHelper.getCustomerId().toString()))
                        .method(original.method, original.body)
                        .build()

                    logD(APP_TAG,"Bearer :" +preferencesHelper.getAuth())
                    logD("**encryptedUserID","encrpted user ID :" +preferencesHelper.encryptUserId(preferencesHelper.getCustomerId().toString()))
                    return chain.proceed(request)
                }
            })
            .build()
        return httpClientBuilder
    }
    private fun getUnsafeOkHttpClient(mContext: Context) :
            OkHttpClient.Builder {


        var mCertificateFactory : CertificateFactory =
            CertificateFactory.getInstance("X.509")
        var mInputStream = mContext.resources.openRawResource(R.raw.uat_aigenix_ai)
        var mCertificate : Certificate = mCertificateFactory.generateCertificate(mInputStream)
        mInputStream.close()
        val mKeyStoreType = KeyStore.getDefaultType()
        val mKeyStore = KeyStore.getInstance(mKeyStoreType)
        mKeyStore.load(null, null)
        mKeyStore.setCertificateEntry("ca", mCertificate)

        val mTmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val mTrustManagerFactory = TrustManagerFactory.getInstance(mTmfAlgorithm)
        mTrustManagerFactory.init(mKeyStore)

        val mTrustManagers = mTrustManagerFactory.trustManagers

        val mSslContext = SSLContext.getInstance("SSL")
        mSslContext.init(null, mTrustManagers, null)
        val mSslSocketFactory = mSslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(mSslSocketFactory, mTrustManagers[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        return builder
    }
}

/*

const val API_SERVIE_LOCAL = "local_service"
const val API_SERVICE_SCOPE = "scope_service"
const val API_SERVICE_GOOGLE = "google_service"


@Qualifier
annotation class typeOfService(val type: String)*/
