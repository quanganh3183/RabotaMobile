package com.example.rabotamb.api;

import com.example.rabotamb.data.models.auth.ChangePasswordRequest;
import com.example.rabotamb.data.models.auth.LoginRequest;
import com.example.rabotamb.data.models.auth.LoginResponse;
import com.example.rabotamb.data.models.auth.OtpResponse;
import com.example.rabotamb.data.models.auth.OtpVerificationRequest;
import com.example.rabotamb.data.models.auth.OtpVerificationResponse;
import com.example.rabotamb.data.models.auth.RegisterRequest;
import com.example.rabotamb.data.models.auth.RegisterResponse;
import com.example.rabotamb.data.models.company.CompanyDetailResponse;
import com.example.rabotamb.data.models.job.JobDetailResponse;
import com.example.rabotamb.data.models.job.JobHrDetailResponse;
import com.example.rabotamb.data.models.job.JobListResponse;
import com.example.rabotamb.data.models.job.JobsHrResponse;
import com.example.rabotamb.data.models.resumes.ResumesResponse;
import com.example.rabotamb.data.models.user.UpdateProfileRequest;
import com.example.rabotamb.data.models.user.UpdateProfileResponse;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: UTF-8"
    })
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: UTF-8"
    })
    @POST("api/v1/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: UTF-8"
    })
    @POST("api/v1/auth/active")
    Call<OtpVerificationResponse> verifyOtp(@Body OtpVerificationRequest request);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: UTF-8"
    })
    @POST("api/v1/mail/sendOTP")
    Call<RegisterResponse> resendOtp(@Body Map<String, String> email);

    @GET("api/v1/jobs")
    Call<JobListResponse> getJobs(
            @Query("current") Integer current,
            @Query("pageSize") Integer pageSize,
            @Query("name") String name,
            @Query("location") String location,
            @Query("skills") String skills,
            @Query("level") String level,
            @Query("company") String company
    );

    @GET("api/v1/jobs/{id}")
    Call<JobDetailResponse> getJobDetails(@Path("id") String jobId);


    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Accept-Charset: UTF-8"
    })
    @POST("api/v1/auth/change-password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @GET("api/v1/companies/{id}")
    Call<CompanyDetailResponse> getCompanyDetails(@Path("id") String companyId);

    @POST("api/v1/mail/sendOTP")
    Call<OtpResponse> sendOTP(@Body Map<String, String> email);

    @POST("api/v1/verification/checkOtp")
    Call<OtpResponse> checkOTP(@Body Map<String, String> data);

    @POST("api/v1/auth/forget")
    Call<OtpResponse> forgetPassword(@Body Map<String, String> data);

    @PATCH("/api/v1/users/{id}")
    Call<UpdateProfileResponse> updateProfile(
            @Path("id") String userId,
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    // HR Job Management endpoints
    @GET("/api/v1/jobs/by-company/{companyId}")
    Call<JobsHrResponse> getJobsByCompany(
            @Path("companyId") String companyId,
            @Query("current") int current,
            @Query("pageSize") int pageSize
    );

    @GET("/api/v1/jobs/{jobId}")
    Call<JobHrDetailResponse> getJobHrDetail(
            @Path("jobId") String jobId
    );

    @DELETE("/api/v1/jobs/{jobId}")
    Call<Void> deleteJob(
            @Path("jobId") String jobId,
            @Header("Authorization") String token
    );

    @POST("/api/v1/jobs")
    Call<JobHrDetailResponse> createJob(
            @Body Map<String, Object> jobData,
            @Header("Authorization") String authHeader
    );

    @PATCH("/api/v1/jobs/{jobId}")
    Call<JobHrDetailResponse> updateJob(
            @Path("jobId") String jobId,
            @Body Map<String, Object> updates,
            @Header("Authorization") String authHeader
    );

    @GET("/api/v1/resumes/by-company/{companyId}")
    Call<ResumesResponse> getResumesByCompany(
            @Path("companyId") String companyId,
            @Query("current") int current,
            @Query("pageSize") int pageSize
    );
}