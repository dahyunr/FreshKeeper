package com.example.freshkeeper.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // API 기본 URL, 슬래시(/)로 끝나도록 설정
    public static final String BASE_URL = "https://api.upcitemdb.com/prod/trial/";

    private static Retrofit retrofit = null;

    // Retrofit을 사용하여 API 서비스를 제공하는 메서드
    public static MyApiService getApiService() {
        if (retrofit == null) {
            // HTTP 요청 및 응답 로깅을 위한 인터셉터 설정
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)  // 로깅 인터셉터 추가
                    .build();

            // Retrofit 인스턴스 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // 기본 URL 설정
                    .client(client)     // OkHttp 클라이언트 설정
                    .addConverterFactory(GsonConverterFactory.create())  // JSON 변환기 추가
                    .build();
        }
        // MyApiService 인터페이스 구현체 반환
        return retrofit.create(MyApiService.class);
    }
}
