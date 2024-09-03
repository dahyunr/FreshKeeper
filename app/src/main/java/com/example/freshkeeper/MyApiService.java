package com.example.freshkeeper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MyApiService {

    @POST("v1/images:annotate?key=AIzaSyCeweSgLmWGyZqMRzTSRRdV1dzAHMy8R2w")
    Call<MyResponseType> getData(
            @Body YourRequestType requestBody
    );
}
