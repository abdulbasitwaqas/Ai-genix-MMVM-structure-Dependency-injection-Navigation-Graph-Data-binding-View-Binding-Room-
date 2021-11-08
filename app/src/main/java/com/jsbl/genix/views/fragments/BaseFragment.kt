package com.jsbl.genix.views.fragments

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jsbl.genix.R
import com.jsbl.genix.utils.RequestHandler
import com.jsbl.genix.utils.callBacks.NetworkListener
import com.jsbl.genix.utils.callBacks.OnViewClickListener
import com.jsbl.genix.utils.logout
import com.jsbl.genix.utils.showShort
import com.jsbl.genix.viewModel.BaseViewModel
import com.jsbl.genix.views.dialogs.ProgressDialog
import com.scope.portalapiclient.RestClientUtils.isNetworkAvailable

abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding>(private val mViewModelClass: Class<VM>) :
    Fragment(),
    OnViewClickListener, NetworkListener {
    lateinit var viewModel: VM
    open lateinit var binding: DB
    lateinit var mProgressDialog: android.app.ProgressDialog



    public lateinit var dialogP: ProgressDialog
    public lateinit var requestObserver: Observer<RequestHandler>



    fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
    }

    open fun init() {}

    @LayoutRes
    abstract fun getLayoutRes(): Int

    private fun getViewM(): VM = ViewModelProvider(this).get(mViewModelClass)
    open fun onInject() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewM()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(inflater, container)
        init()
//        if ( isNetworkAvailablee(requireContext())){
            setRequestHandler()
       /* } else{
            dismissDialog()
            showShort(requireContext(), ""+resources.getString(R.string.internt_problem))
        }*/
//        setRequestHandler()
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    open fun refresh() {}


    fun setRequestHandler() {
        requestObserver = Observer<RequestHandler> { t ->
            if (t != null) {
                if (t.loading && !t.isSuccess) {
                    onLoading(t)
                } else if (!t.loading && !t.isSuccess) {
                    dismissDialog()
                    onError(t)
                    logout(t.any!!, requireActivity())
                } else if (!t.loading && t.isSuccess) {
                    dismissDialog()
                    onSuccess(t)
                }
            } else {
            }
        }
        viewModel.requestHandlerMLD.observe(viewLifecycleOwner, requestObserver)
    }


    protected fun showPDialog() {
        dialogP = ProgressDialog.newInstance()
        dialogP.showAllowingStateLoss(childFragmentManager, "progress")
        dialogP.isCancelable = false
    }

    protected fun dismissDialog() {
        if (this::dialogP.isInitialized)
            if (dialogP.isAdded)
                dialogP.dismiss()
    }


    private fun showProgress(show: Boolean) {
        if (show) {
            mProgressDialog = android.app.ProgressDialog(context,
                    R.style.CustomFontDialog)
            mProgressDialog.setMessage(resources.getString(R.string.string_message_wait))
            mProgressDialog.setIndeterminate(false)
            mProgressDialog.setCancelable(false)
            mProgressDialog.show()
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss()
            }
        }
    }

    open fun isNetworkAvailablee(context: Context): Boolean {
        var isConnected = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?:
            return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?:
            return false
            isConnected = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                activeNetworkInfo?.run {
                    isConnected = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return false
    }


}
