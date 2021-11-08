package com.jsbl.genix.network

import android.content.Context
import com.jsbl.genix.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScopeRetrofitClient {
    fun apiScopeServices(context: Context): ScopeApiServices {
        val logger = HttpLoggingInterceptor()
        logger.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .retryOnConnectionFailure(false)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()
                    val request: Request = original.newBuilder()
                        .header("Accept", "application/json")
                        .addHeader(
                            "Authorization",
                            "Bearer " + "fv7UFsaYh_3ee-E-g1_B2JUas-qRWQdEWSFUct6E8CMu7G__lQd-z_8iMRF_neYh3V-k5fZ9htYFCMaUirdOn5TZZmk30AQnZo5CvXTNdiZHCIMBXJXYCjxk0a6XzMndnIU46UFHi0KkbsFstsB7DhW6sm7qmpQDkBYVJSsy_dDhI7hupP1Ai93_esxl9bFZP2AX2ADoiM__LTIy3fSnkgbftCO1HVZ29Nh7rAB3YjivgdMfchMQvHTYvcomApaW3-qXileOgDZZpf48jD4akuJiDwiVXCRc4wVU_Fn96EQmwBoi4DENZqVeMZ-4HLVQwLdlcHPl2TsfM6Hnbr1I-w6Ruj1jq9wckc556Abmg0kwFoMtwuSOK-jsCxqBV24LAsFu_WE1U8Y-V2jS7L0k8jbntyVZYmSV1rfBvcBrVGaqGLkK9ZOqI9ndj0B7pqp23ERK9qHWyYrqLqtBFSVIw-OMS7wwuF36zADttksz4N67ie4mW4kaDyNK4XAVdi-1dLgF22mIdkFMvHSVYAnvl6WCbnTw1VLasUoDzlD-aNiRxsX2VWnMB00c3WXwScySdcc2IKEoX9q-CzJpALxXhEYhsMwq6feA5lZ0Kjw_qo8HdihVj6kydjoQADSKpFjU"
                        )
                        .method(original.method, original.body)
                        .build()
                    return chain.proceed(request)
                }
            })
            .build()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL_SCOPE)
            .client(client)
            .build()

        return retrofit.create<ScopeApiServices>(ScopeApiServices::class.java)
    }
}

