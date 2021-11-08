package com.jsbl.genix.interfaces;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public interface IResult {
    public void notifySuccess(String response, String requestMessage);
    public void notifyError(VolleyError error);
    public void notifySuccessNetwork(NetworkResponse response, String requestMessage);
}
