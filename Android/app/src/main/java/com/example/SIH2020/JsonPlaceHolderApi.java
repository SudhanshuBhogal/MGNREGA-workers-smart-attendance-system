package com.example.SIH2020;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {


    @POST("markattendance")
    Call<AttendanceMark> markAttendance(@Body AttendanceMark attendanceMark);
    @FormUrlEncoded
    @POST("markattendance")
    Call<AttendanceMark> markAttendance(
            @Field("name") String name
    );


//    @FormUrlEncoded
//    @POST/GET/PUT/DELETE("/your_endpoint")
//    Object yourMethodName(@Field("your_field") String yourField,...);

    @FormUrlEncoded
    @POST("markattendance")
    Call<AttendanceMark> markAttendance(@FieldMap Map<String, String> fields);

}
