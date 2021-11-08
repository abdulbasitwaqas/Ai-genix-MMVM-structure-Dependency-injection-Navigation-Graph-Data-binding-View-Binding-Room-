package com.jsbl.genix.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides

/**
 * Created by Muhammad Ali on 03-Aug-20.
 * Email muhammad.ali9385@gmail.com
 */
@Module
class AppModule(val app: Application) {
    @Provides
    fun provideApp(): Application = app
}