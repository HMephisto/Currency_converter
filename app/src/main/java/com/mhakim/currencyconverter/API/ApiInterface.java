package com.mhakim.currencyconverter.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("latest/currencies/{from}/{to}.json")
    @Headers("Accept: application/json")
    Call<Object> convert(@Path("from") String from,
                      @Path("to") String to);
}
