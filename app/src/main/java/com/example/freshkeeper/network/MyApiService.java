package com.example.freshkeeper.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MyApiService {
    @GET("lookup")
    Call<MyResponseType> lookupItem(
            @Header("user_key") String apiKey,  // API 키를 헤더로 전달
            @Query("upc") String upcCode        // 바코드 값을 쿼리 파라미터로 전달
    );
}
