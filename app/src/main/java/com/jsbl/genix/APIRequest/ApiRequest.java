package com.jsbl.genix.APIRequest;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jsbl.genix.interfaces.IResult;
import com.jsbl.genix.model.ParmsModel;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.me.hardill.volley.multipart.MultipartRequest;


public class ApiRequest {
    private IResult iResult = null;
    private Context context;
    private String currentDateTimeString= java.text.DateFormat.getDateTimeInstance().format(new Date());;

    public ApiRequest(IResult resultCallback, Context context) {
        iResult = resultCallback;
        this.context = context;
    }

    public void apiRequestPostMethodWithJsonObject(String url, final JSONObject jsonObject, final String requestMessage) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (iResult != null)
                                iResult.notifySuccess(response.toString(), requestMessage);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.d("jsonreqesssst", "..............");
                    if (iResult != null)
                        iResult.notifyError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("X-Authorization", "" + KeyandCodesConstant.defaultAuthKey);
//                    headers.put("Authorization", "Bearer "+ SharedPreferencesSotreData.getInstance().getLoginToken());
                    return headers;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    120000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjReq);


        } catch (Exception e) {
        }
    }

    public void apiRequestPostMethod(String url, final List<ParmsModel> parmsModelList, final String requestMessage) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest jsonObj = new StringRequest(Request.Method.POST,   url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (iResult != null)
                        iResult.notifySuccess(response,requestMessage);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (iResult != null)
                        iResult.notifyError(error);
                }
            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> headers = new HashMap<String, String>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("X-Authorization", ""+ KeyandCodesConstant.defaultAuthKey);
//                    headers.put("Authorization", SharedPreferencesSotreData.getInstance().getLoginToken());
//                    return headers;
//                }

                @Override
                protected Map<String, String> getParams() {
                    //Post params to login url
                    Map<String, String> params = new HashMap<String, String>();
                    for (int i = 0; i< parmsModelList.size(); i++){
                        params.put(parmsModelList.get(i).getParmsTag(), parmsModelList.get(i).getParmsValue());
                        Log.d("****params", parmsModelList.get(i).getParmsTag()+",,"+parmsModelList.get(i).getParmsValue());
                    }
                    return params;
                }

            };
            jsonObj.setRetryPolicy(new DefaultRetryPolicy(
                    120000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObj);
        } catch (Exception e) {
        }
    }





    public void apiRequestFilePostMethod(String url, final List<ParmsModel> parmsModelList, final String requestMessage, String fileFieldName, String mimeType, String fileName, byte data[]) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Disposition", "multipart/form-data");
//            headers.put("content-type", "application/json; charset=utf-8");

            MultipartRequest request = new MultipartRequest(url, headers,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("**netResponse",""+response);
                            try {
                                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                if (iResult != null)
                                    iResult.notifySuccess(json,requestMessage);
                                Log.d("*fileResponse","response :   "+json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            for (int i = 0; i< parmsModelList.size(); i++){
                request.addPart(new MultipartRequest.FormPart(parmsModelList.get(i).getParmsTag(), parmsModelList.get(i).getParmsValue()));
            }
            request.addPart(new MultipartRequest.FilePart(fileFieldName, mimeType, fileName, data));
            queue.add(request);
        } catch (Exception e) {
        }
    }

    public void apiRequestPostADDPostMethod(String url, final List<ParmsModel> parmsModelList, final String requestMessage, String fileFieldName, String mimeType, String fileName, byte data[]) {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            Map<String, String> headers = new HashMap<String, String>();

            headers.put("Content-Disposition", "multipart/form-data");

            MultipartRequest request = new MultipartRequest(url, headers,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("**netResponse",""+response);
                            try {
                                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                                if (iResult != null)
                                    iResult.notifySuccess(json,requestMessage);
                                Log.d("*addPostResp","response :   "+json);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            for (int i = 0; i< parmsModelList.size(); i++){
                request.addPart(new MultipartRequest.FormPart(parmsModelList.get(i).getParmsTag(), parmsModelList.get(i).getParmsValue()));
            }
            request.addPart(new MultipartRequest.FilePart(fileFieldName, mimeType, fileName, data));
            queue.add(request);

        } catch (Exception e) {
        }
    }

    public void apiRequestGetMethod(String method, final String requestMessage){
        try{
            RequestQueue queue = Volley.newRequestQueue(context);

            StringRequest jsonObj = new StringRequest(Request.Method.GET,   method, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (iResult != null)
                        iResult.notifySuccess(response,requestMessage);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (iResult != null)
                        iResult.notifyError(error);
                }

            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("X-Authorization", ""+KeyandCodesConstant.defaultAuthKey);
//                    headers.put("Authorization", "Bearer "+ SharedPreferencesSotreData.getInstance().getLoginToken());
                    return headers;
                }
            };
            jsonObj.setRetryPolicy(new DefaultRetryPolicy(
                    120000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObj);
        }catch (Exception e){
        }
    }
}
