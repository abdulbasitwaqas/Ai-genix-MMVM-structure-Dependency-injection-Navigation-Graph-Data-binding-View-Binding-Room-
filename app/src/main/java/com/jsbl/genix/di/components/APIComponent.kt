package com.jsbl.genix.di.components

import com.jsbl.genix.di.modules.APIModule
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.network.LocalService
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Muhammad Ali on 03-Aug-20.
 * Email muhammad.ali9385@gmail.com
 */
@Singleton
@Component(modules = arrayOf(APIModule::class,AppModule::class))
interface APIComponent {

    fun injectAPI(service: LocalService)

}