package com.minardwu.yiyue.http;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author wumingyuan
 * @date 2018/7/31.
 */

public class ServiceGenerator {

    private static final String BASE_URL = "https://api.imjad.cn/";

    private static ServiceGenerator serviceGenerator;

    private static Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ServiceGenerator() {
    }

    public static ServiceGenerator getInstance() {
        if (serviceGenerator == null) {
            synchronized (ServiceGenerator.class) {
                if (serviceGenerator == null) {
                    serviceGenerator = new ServiceGenerator();
                }
            }
        }
        return serviceGenerator;
    }

    public  <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }


}
