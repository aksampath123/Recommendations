package com.akshay.sampath.android.samples.googleplayservices.api;

import android.view.Window;

import com.akshay.sampath.android.samples.googleplayservices.model.ActiveListings;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.Callback;

/**
 * Created by Akshay on 2015-11-21.
 */
public class Etsy {
    private static final String API_KEY = "ozwxdpek492s7ftwx667mssf";

    private static RequestInterceptor getInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addEncodedQueryParam("api_key", API_KEY);
            }
        };
    }

    private static Api getApi(){
        return new RestAdapter.Builder()
                .setEndpoint("https://openapi.etsy.com/v2")
                .setRequestInterceptor(getInterceptor())
                .build()
                .create(Api.class);
    }

    public static void getActiveListings(Callback<ActiveListings> callback){
        getApi().activeListings("Images,Shop", callback);
    }
}
