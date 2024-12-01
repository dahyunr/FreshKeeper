package com.example.freshkeeper.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyApiService {
    // API 호출 시 쿼리 파라미터로 UPC 코드를 전달
    @GET("lookup")
    Call<MyResponseType> lookupItem(
            @Query("upc") String upcCode       // 바코드 값을 쿼리 파라미터로 전달
    );
}