package com.student.xxc.etime.impl;

import io.reactivex.Observable;

import com.google.gson.JsonObject;
import com.student.xxc.etime.bean.SiteBean;
import com.student.xxc.etime.bean.TraceBean;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRxJavaTrace_Interface {
    @GET("/v3/place/detail")
    Observable<JsonObject>  getPoint(@Query("id") String id, @Query("key") String key, @Query("output") String output);

    @GET("/v3/direction/walking")
    Observable<JsonObject>  getDistance(@Query("origin") String origin,@Query("destination") String destination,
                                        @Query("output") String output,@Query("key") String key);
}
