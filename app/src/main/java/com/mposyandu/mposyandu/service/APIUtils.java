package com.mposyandu.mposyandu.service;

import com.mposyandu.mposyandu.tools.Database;

public class APIUtils {
    private APIUtils() {}

    public static final String BASE_URL = Database.getUrl();

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
