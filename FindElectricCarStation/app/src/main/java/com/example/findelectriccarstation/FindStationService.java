package com.example.findelectriccarstation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FindStationService {
    @GET("openapi/services/rest/EvChargerService")
    Call<StationInfo>getStationInfo(@Query(value = "serviceKey",encoded = true) String serviceKey);
}
