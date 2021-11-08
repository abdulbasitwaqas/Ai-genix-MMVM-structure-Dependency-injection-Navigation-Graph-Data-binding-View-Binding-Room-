package com.jsbl.genix.Presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.jsbl.genix.APIRequest.ApiRequest;
import com.jsbl.genix.interfaces.IPresenter;
import com.jsbl.genix.interfaces.IResult;
import com.jsbl.genix.interfaces.RequestViews;
import com.jsbl.genix.model.ParmsModel;

import org.json.JSONObject;

import java.util.List;

public class Presenter implements IResult {
    ApiRequest apiRequests;
    private RequestViews requestViews;
    IPresenter iPresenter;
    Context context;
    private String requestMessage;
    private String LOG_TAG = "vooolllyerror";

    public Presenter(RequestViews requestViews, Context context, IPresenter iPresenter){
        this.requestViews = requestViews;
        this.context=context;
        this.iPresenter=iPresenter;
        this.apiRequests = new ApiRequest(this,context);
    }



    public void setGetMethod(String url,String requestMessage){
        requestViews.showProgress();
        requestMessage = requestMessage;
        apiRequests.apiRequestGetMethod(url,requestMessage);
    }

    public void setPostMethod(String url, List<ParmsModel> parmsList, String requestMessage){
        requestViews.showProgress();
        this.requestMessage = requestMessage;
        apiRequests.apiRequestPostMethod(url,parmsList,requestMessage);
    }



    public void setFilePostMethod(String url, List<ParmsModel> parmsList, String requestMessage, String fileFieldName, String mimeType, String fileName, byte data[]){
        requestViews.showProgress();
        this.requestMessage = requestMessage;
        apiRequests.apiRequestFilePostMethod(url,parmsList,requestMessage, fileFieldName, mimeType, fileName,data);
    }

    public void setPostAddPostMethod(String url, List<ParmsModel> parmsList, String requestMessage, String fileFieldName, String mimeType, String fileName, byte data[]){
        requestViews.showProgress();
        this.requestMessage = requestMessage;
        apiRequests.apiRequestPostADDPostMethod(url,parmsList,requestMessage, fileFieldName, mimeType, fileName,data);
    }


    public void setPostMethodJsonObject(String url, JSONObject jsonObject, String requestMessage) {
        requestViews.showProgress();
        this.requestMessage = requestMessage;
        apiRequests.apiRequestPostMethodWithJsonObject(url, jsonObject, requestMessage);
    }


    @Override
    public void notifySuccess(String response, String requestMessage) {
        logLargeString(response);
        requestViews.hideProgress();
        iPresenter.getResponse(response,requestMessage);
    }

    @Override
    public void notifySuccessNetwork(NetworkResponse response, String requestMessage) {
        Log.d("notifySuccessNetwork", String.valueOf(response));
        requestViews.hideProgress();
        iPresenter.getSuccessNetwork(response,requestMessage);
    }
    @Override
    public void notifyError(VolleyError error) {
        iPresenter.getError(error);
        requestViews.hideProgress();
        if (error instanceof NetworkError) {
            //errorCode = NETWORK_ERROR;
            Log.i(LOG_TAG, "NetworkError" + error);
        } else if (error instanceof ServerError) {
            Log.i(LOG_TAG, "ServerError" + error);
        } else if (error instanceof AuthFailureError) {
            Log.i(LOG_TAG, "AuthFailureError" + error);
        } else if (error instanceof ParseError) {
            Log.i(LOG_TAG, "ParseError" + error);
        } else if (error instanceof NoConnectionError) {
            Log.i(LOG_TAG, "NoConnectionError" + error);
        } else if (error instanceof TimeoutError) {
            Log.i(LOG_TAG, "TimeoutError" + error);
        } else {
            Log.i(LOG_TAG, "TimeoutError" + error);
        }
    }


    public void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.i("getRequestresponse", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i("getRequestresponse", ""+str);
        }
    }
}
