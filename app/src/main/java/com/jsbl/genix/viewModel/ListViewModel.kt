package com.jsbl.genix.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.model.DogBreed
import com.jsbl.genix.db.dao.DogDao
import com.jsbl.genix.db.AppDatabase
import com.jsbl.genix.di.components.DaggerServiceComponent
import com.jsbl.genix.network.LocalService
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

/**
 * Created by Muhammad Ali on 04-May-20.
 * Email muhammad.ali9385@gmail.com
 */
class ListViewModel(application: Application) : BaseViewModel(application) {


    private var refreshTime = 2 * 60 * 1000 * 1000 * 1000L

    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val error = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {

        checkCacheDuration()
        val updateTime = prefsHelper.getTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    fun checkCacheDuration() {
        val cacheNumer = prefsHelper.getCachePreferences()
        try {
            val cacheNumberInt = cacheNumer?.toInt() ?: 5 * 60
            refreshTime = cacheNumberInt.times(1000 * 1000 * 1000L)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        loading.value = true
       /* registrationService.getOtp().enqueue(object :Callback<RequestResponse>{
            override fun onFailure(call: Call<RequestResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<RequestResponse>,
                response: Response<RequestResponse>
            ) {
                TODO("Not yet implemented")
            }

        })*/
/*
        disposable.add(
               *//* .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(t: List<DogBreed>) {
                        storeLocally(t)
                        Toast.makeText(getApplication(), "Retrived from Server", Toast.LENGTH_SHORT)
                            .show()
                        NotificationHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        error.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })*//*
        )*/
    }

    private fun dogsRetrieve(t: List<DogBreed>) {
        dogs.value = t
        error.value = false
        loading.value = false
    }

    /*private fun storeLocally(t: List<DogBreed>) {
        launch {

            val dao: DogDao = AppDatabase(getApplication()).dogDao()
//            dao.deleteAllDogs()
            val insertResult = dao.insertAll(*t.toTypedArray())
            fetchFromDatabase()
//            dogsRetrieve(fetchFromDatabase())
        }
        prefsHelper.updateTime(System.nanoTime())
    }
*/
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}