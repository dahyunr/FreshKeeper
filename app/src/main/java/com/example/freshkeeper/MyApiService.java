package com.example.freshkeeper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MyApiService {

    @POST("v1/images:annotate")
    Call<MyResponseType> getData(
            @Header("Authorization") String apiKey,
            @Body YourRequestType requestBody
    );
}
