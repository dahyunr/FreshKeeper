package com.example.freshkeeper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://vision.googleapis.com/";

    private static Retrofit retrofit = null;

    public static MyApiService getApiService() {
        if (retrofit == null) {
            // 로그 기록을 위한 인터셉터 추가
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient에 인터셉터 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Retrofit 인스턴스 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // OkHttpClient 설정 추가
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(MyApiService.class);
    }
}
