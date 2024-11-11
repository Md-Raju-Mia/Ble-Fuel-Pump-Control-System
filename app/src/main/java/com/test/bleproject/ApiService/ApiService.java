package com.test.bleproject.ApiService;


import com.test.bleproject.DataModel.DashboardRequestBody;
import com.test.bleproject.DataModel.DashboardResponseBody;
import com.test.bleproject.DataModel.GetOTPResponse;
import com.test.bleproject.DataModel.LoginResponse;
import com.test.bleproject.DataModel.ScannedInfo;
import com.test.bleproject.DataModel.ScannedInfoResponse;
import com.test.bleproject.DataModel.SortedReportRequestBody;
import com.test.bleproject.DataModel.submitFormInfoResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/log-in/{username}/{password}")
    Call<LoginResponse> loginUser(
            @Path("username") String username,
            @Path("password") String password
    );



    @POST("api/scan")
    Call<ScannedInfoResponse> scannedDevice(@Body ScannedInfo scannedInfo );

    @POST("api/dashboard-data")
    Call<DashboardResponseBody> scannedDevice(@Body DashboardRequestBody DashboardInfo );

    @POST("api/dtaewise-supervisor-data")
    Call<DashboardResponseBody> sortedReport(@Body SortedReportRequestBody shortedReportRequestBody);

    @GET("api/generate-otp/{driverId}")
    Call<GetOTPResponse> sendDriverId(
            @Path("driverId") int driverId
    );

    @Multipart
    @POST("api/fuel-history")
    Call<submitFormInfoResponse> sendforminfo(
            @Part ("driver_id") RequestBody driverId,
            @Part ("driver_name") RequestBody driverName,
            @Part ("fuel_type") RequestBody fuelName,
            @Part ("previous_km") RequestBody previousMeterReading,
            @Part ("todays_km")  RequestBody meterReading,
            @Part ("fuel_unit") RequestBody fuelLiter,
            @Part ("fuel_cost") RequestBody fueltotalprice,
            @Part ("vehicle_id") RequestBody vehicle_id,
            @Part ("otp") RequestBody otp,
            @Part ("brta_reg_num") RequestBody brta_reg_num,
            @Part ("created_by") RequestBody created_by,
            @Part MultipartBody.Part image1,
            @Part MultipartBody.Part image2
            );
}
