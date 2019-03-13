package com.example.weatherforecast;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherForecastService {

    @GET("openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty")
    Call<AirKorea>getDustInfo(@Query(value = "ServiceKey",encoded = true) String key,
                                    @Query(value = "sidoName",encoded = true) String sidoName,
                                    @Query("numOfRows") int rows,
                                    @Query("ver") float ver);
    @GET("service/MiddleFrcstInfoService/{operation}")
    Call<Weather>getWeatherForcast(@Path("operation") String operation,
                            @Query(value = "ServiceKey",encoded = true) String key,
                            @Query(value = "regId",encoded = true) String regId,
                            @Query("tmFc") String date);

}
