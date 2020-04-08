package com.example.reactivepractice.api;

import android.content.Context;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.reactivepractice.data.Constants.API_BASE_URL;


public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();

    //http client
    private static OkHttpClient okHttpClient;

    //retrofit
    private static Retrofit retrofit;


    public static Retrofit getApiClient(Context context) {

        if (okHttpClient == null) {
            initializeOkHttpClient();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create( new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create())
                    )
                    .build();
        }

        return retrofit;
    }

    private static void initializeOkHttpClient() {
        //new http client which an interceptor would be added to
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        //logging interceptor
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //add interceptor to client
        httpClient.addNetworkInterceptor(loggingInterceptor);

        okHttpClient = httpClient.build();
    }
}
