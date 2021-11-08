package com.jsbl.genix.di.components

import android.app.Service
import com.jsbl.genix.di.modules.APIModule
import com.jsbl.genix.di.modules.AppModule
import com.jsbl.genix.di.modules.PrefsModule
import com.jsbl.genix.utils.services.GenixService
import com.jsbl.genix.viewModel.*
import com.jsbl.genix.views.fragments.CarList
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Muhammad Ali on 03-Aug-20.
 * Email muhammad.ali9385@gmail.com
 */

@Singleton
@Component(modules = arrayOf(
    APIModule::class,
    PrefsModule::class,
    AppModule::class))
interface ServiceComponent {

    fun injectViewModel(viewModel: ListViewModel)
    fun injectRegistration(viewModel: RegistrationViewModel)
    fun injectCameraCapture(viewModel: CameraCaptureViewModel)
    fun injectLogin(viewModel: LoginViewModel)
    fun injectReset(viewModel: ResetViewModel)
    fun injectVerification(viewModel: VerificationViewModel)
    fun injectDropDown(viewModel: ProfileManagementViewModel)
    fun injectCarDetails(viewModel: CarDetailsViewModel)
    fun injectFeedBack(viewModel: FeedbackViewModel)
    fun injectAreaOfInterest(viewModel: AreaOfInterestViewModel)
    fun injectPersonalDetails(viewModel: PersonalDetailViewModel)
    fun injectIntoBaseViewModel(viewModel:BaseViewModel)
    fun injectCarListViewModel(viewModel:CarListViewModel)
    fun injectIntoService(service:GenixService)

}