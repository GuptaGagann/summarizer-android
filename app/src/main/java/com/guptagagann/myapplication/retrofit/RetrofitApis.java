package com.guptagagann.myapplication.retrofit;

import com.guptagagann.myapplication.entity.FlaskApiResponseBody;
import com.guptagagann.myapplication.entity.SourceFileRequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitApis {

    @POST("/")
    Call<FlaskApiResponseBody> sendSourceFileToFlask(@Body SourceFileRequestBody sourceFileRequestBody);
}
