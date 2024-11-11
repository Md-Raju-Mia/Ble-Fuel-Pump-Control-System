package com.test.bleproject.UI;

import static com.test.bleproject.SplashActivity.SplashActivity.PREFS_NAME;
import static com.test.bleproject.UI.MainActivity.BASE_URL;
import static com.test.bleproject.UI.MainActivity.ScannedData;
import static com.test.bleproject.UI.MainActivity.UserContactNumber;
import static com.test.bleproject.UI.MainActivity.UserName;
import static com.test.bleproject.UI.MainActivity.showuserName;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.test.bleproject.ApiService.ApiService;
import com.test.bleproject.DataModel.DashboardRequestBody;
import com.test.bleproject.DataModel.DashboardResponseBody;
import com.test.bleproject.DataModel.PreviousFuelHistory;
import com.test.bleproject.DataModel.ScannedInfo;
import com.test.bleproject.DataModel.ScannedInfoResponse;
import com.test.bleproject.DataModel.SortedReportRequestBody;
import com.test.bleproject.R;
import com.test.bleproject.RetrofitInstance.RetrofitClient;
import com.test.bleproject.SplashActivity.SplashActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";

    private static final int CAMERA_REQUEST_CODE =1001;

    TextView userName,userMobileNumber;

    public static String startDate = "";
    public static String endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //homeactivity customa ctionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.homeactivity_actionbar); // Use your custom layout

            View customActionBarView = actionBar.getCustomView();
            ImageView logoutButtonHomeView = customActionBarView.findViewById(R.id.logoutFromHomeView);
            logoutButtonHomeView.setOnClickListener(v -> {
                logoutButtonAndClearData();
            });

        }


        userName = findViewById(R.id.UserName);
        userMobileNumber = findViewById(R.id.UserMobileNumber);
        userName.setText(showuserName);
        userMobileNumber.setText(UserContactNumber);


        // Initialize Scan Button
        CardView scanButton = findViewById(R.id.cardRefueling);
        scanButton.setOnClickListener(v -> {
            scanButton.setEnabled(false);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                startCameraForQRScan();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
            }
            new Handler().postDelayed(() -> scanButton.setEnabled(true), 1000);
        });


        CardView cardDashboard = findViewById(R.id.cardDashboard);
        cardDashboard.setOnClickListener(v -> {
            // Disable the button to prevent multiple clicks
            cardDashboard.setEnabled(false);
            GetDashBoardResponse();
            new Handler().postDelayed(() -> cardDashboard.setEnabled(true), 1000);
        });


        CardView generateReport = findViewById(R.id.card_generate_report);
        LinearLayout select_report_date = findViewById(R.id.select_report_date);
        CardView  card_view_report = findViewById(R.id.card_view_report);
        EditText startDateEditText = findViewById(R.id.Report_start_date);
        EditText endDateEditText = findViewById(R.id.Report_end_date);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        endDateEditText.setInputType(InputType.TYPE_NULL);

        final int[] listenervalue = {0};
        generateReport.setOnClickListener(v -> {

                if(listenervalue[0] ==0){
                    select_report_date.setVisibility(View.VISIBLE);
                    card_view_report.setVisibility(View.VISIBLE);
                    // Reset startDate and endDate after generating the report
                    startDate = null;
                    endDate = null;

                    // Clear the text in the EditText fields
                    startDateEditText.setText("");
                    endDateEditText.setText("");

                    Toast.makeText(this,"Give start & End Date", Toast.LENGTH_SHORT).show();
                    listenervalue[0]++;
                }else{
                    listenervalue[0]=0;
                    select_report_date.setVisibility(View.GONE);
                    card_view_report.setVisibility(View.GONE);
                }
        });

        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText,true));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText, false));

        //Click For Show Report
        card_view_report.setOnClickListener(v -> {
            Log.d("raju", "View Report Call");

            card_view_report.setEnabled(false);

            // Check if startDate or endDate are null or empty
            if (startDate == null || startDate.isEmpty()) {
                Toast.makeText(this, "Please select a Start Date", Toast.LENGTH_SHORT).show();
            } else if (endDate == null || endDate.isEmpty()) {
                Toast.makeText(this, "Please select an End Date", Toast.LENGTH_SHORT).show();
            } else {
                // If both dates are filled, proceed to get the report
                GetSortedReport();

                // Reset startDate and endDate after generating the report
                startDate = null;
                endDate = null;

            }

            // Re-enable the button after 1 second
            new Handler().postDelayed(() -> card_view_report.setEnabled(true), 1000);
        });



        // Handle window insets for better UI experience (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.HomeActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Show an alert dialog asking if the user wants to go to the home activity
                showExitConfirmationDialog();
            }
        });

    }

    // Method to display the AlertDialog
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")

                // If the user chooses "Yes", close the app
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity(); // Closes all activities and exits the app
                })

                // If the user chooses "No", just dismiss the dialog and stay on the current activity
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startCameraForQRScan();
        }else{
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
        }
    }

    // Start camera and scan QR code
    private void startCameraForQRScan() {
        Intent intent = new Intent(this, QRScanActivity.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String scannedData = data.getStringExtra("scannedData");

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("scannedData", scannedData);
            editor.apply();

            ScannedData = scannedData;

            Log.e("Raju", "RetrieveDeviceInfo called from onActivityResult method on HomeActivity");

            Log.e("Raju", "SendScanInfo Called");
            GetScanInfo();

        }
    }

    private void logoutButtonAndClearData(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void GetScanInfo(){
        Log.e("Raju", "you are in GetScanInfo API");

        ScannedInfo scannedInfo = new ScannedInfo(ScannedData);


        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Log.e("Raju", "BaseUrl: "+ BASE_URL);
        Log.e("Raju", "ScanneData" + ScannedData);

        //Make the POST request
        apiService.scannedDevice(scannedInfo).enqueue(new Callback<ScannedInfoResponse>() {
            @Override
            public void onResponse(Call<ScannedInfoResponse> call, Response<ScannedInfoResponse> response) {
                if(response.isSuccessful() && response.body() !=null){
                    ScannedInfoResponse scannedInfoResponse = response.body();
                    Log.e("Raju","Get Status is : "+scannedInfoResponse.getStatus());

                    if(scannedInfoResponse.getStatus()==200){

                        Log.e("Raju", "SendScanInfo successfull");
                      //  Toast.makeText(HomeActivity.this, "Send Data to Api Successful: ",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                        intent.putExtra("model", scannedInfoResponse.getModel());
                        intent.putExtra("brta_reg_num", scannedInfoResponse.getBrta_reg_num());
                        intent.putExtra("serial_number", scannedInfoResponse.getSerialNumber());
                        intent.putExtra("prev_meter_reading", String.valueOf(scannedInfoResponse.getPrev_meter_reading()));
                        intent.putExtra("last_intake", String.valueOf(scannedInfoResponse.getLast_intake()));
                        intent.putExtra("last_intake_price", String.valueOf(scannedInfoResponse.getlast_intake_price()));
                        intent.putExtra("avg_km_l", String.valueOf(scannedInfoResponse.getAvg_km_l()));
                        intent.putExtra("vehicle_id", String.valueOf(scannedInfoResponse.getvehicle_id()));


                        intent.putParcelableArrayListExtra("drivers", new ArrayList<>(scannedInfoResponse.getDrivers()));
                        intent.putParcelableArrayListExtra("fuel", new ArrayList<>(scannedInfoResponse.getFuel()));
                        startActivity(intent);
                        finish();


                    } else if(scannedInfoResponse.getStatus()==400){
                        Toast.makeText(HomeActivity.this,scannedInfoResponse.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e("Raju", "Status code 400 Error");
                    }

                } else {
                    Toast.makeText(HomeActivity.this,"Resopnse Unsuccessful",Toast.LENGTH_SHORT).show();
                    Log.e("Raju", "Resopnse Unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<ScannedInfoResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "API call Error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"API Call Error");
            }
        });


    }



    private void GetDashBoardResponse(){
        Log.e("Raju", "you are in GetScanInfo API");

        DashboardRequestBody dashboardRequestBody = new DashboardRequestBody(UserName);


        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Log.e("Raju", "BaseUrl: "+ BASE_URL);
        Log.e("Raju", "UserName" + UserName);

        //Make the POST request
        apiService.scannedDevice(dashboardRequestBody).enqueue(new Callback<DashboardResponseBody>() {
            @Override
            public void onResponse(Call<DashboardResponseBody> call, Response<DashboardResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponseBody dashboardResponseBody = response.body();
                    Log.e("Raju", "Get Status is : " + dashboardResponseBody.getStatus());

                    if (dashboardResponseBody.getStatus() == 200) {

                        Intent intent = new Intent(HomeActivity.this, Dashboard.class);
                        intent.putExtra("dashboard_data", dashboardResponseBody);
                        startActivity(intent);
                        finish();

                        Log.e("Raju", "DashboardRequestBody successful");

                        // Log the fuel cost data
                        Log.e("Raju", "Weekly Fuel Cost: " + dashboardResponseBody.getWeeklyFuelCost());
                        Log.e("Raju", "Monthly Fuel Cost: " + dashboardResponseBody.getMonthlyFuelCost());
                        Log.e("Raju", "Total Fuel Cost: " + dashboardResponseBody.getTotalFuelCost());

                        // Loop through previous_fuel_histories and log each entry
                        List<PreviousFuelHistory> fuelHistories = dashboardResponseBody.getPreviousFuelHistories();
                        for (PreviousFuelHistory history : fuelHistories) {
                            Log.e("Raju", "Date: " + history.getDate());
                            Log.e("Raju", "Vehicle: " + history.getBrtaRegNum());
                            Log.e("Raju", "Driver Name: " + history.getDriverName());
                            Log.e("Raju", "Fuel: " + history.getFuel());
                            Log.e("Raju", "Fuel Unit: " + history.getFuelUnit());
                            Log.e("Raju", "Fuel Cost: " + history.getFuelCost());
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Response Unsuccessful", Toast.LENGTH_SHORT).show();
                    Log.e("Raju", "Response Unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<DashboardResponseBody> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "API call Error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"API Call Error");
            }
        });

    }







    private void showDatePickerDialog(final EditText dateEditText, boolean isStartDate){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                HomeActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date as YYYY-MM-DD
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateEditText.setText(formattedDate);

                    if(isStartDate){
                        startDate = formattedDate;
                    } else{
                        endDate = formattedDate;
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }



    private void GetSortedReport(){
        Log.e("Raju", "you are in GetScanInfo API");

        SortedReportRequestBody sortedReportRequestBody = new SortedReportRequestBody(UserName, startDate, endDate);


        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Log.e("Raju", "BaseUrl: "+ BASE_URL);
        Log.e("Raju", "UserName" + UserName);
        Log.d("Selected Dates", "Start Date: " + startDate + ", End Date: " + endDate);

        //Make the POST request
        apiService.sortedReport(sortedReportRequestBody).enqueue(new Callback<DashboardResponseBody>() {
            @Override
            public void onResponse(Call<DashboardResponseBody> call, Response<DashboardResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponseBody dashboardResponseBody = response.body();
                    Log.e("Raju", "Get Status is : " + dashboardResponseBody.getStatus());

                    if (dashboardResponseBody.getStatus() == 200) {

                        Intent intent = new Intent(HomeActivity.this, Dashboard.class);
                        intent.putExtra("dashboard_data", dashboardResponseBody);
                        intent.putExtra("reportType","sortedReport");
                        startActivity(intent);
                        finish();


                        Log.e("Raju", "DashboardRequestBody successful");

                        // Log the fuel cost data
                        Log.e("Raju", "Weekly Fuel Cost: " + dashboardResponseBody.getWeeklyFuelCost());
                        Log.e("Raju", "Monthly Fuel Cost: " + dashboardResponseBody.getMonthlyFuelCost());
                        Log.e("Raju", "Total Fuel Cost: " + dashboardResponseBody.getTotalFuelCost());

                        // Loop through previous_fuel_histories and log each entry
                        List<PreviousFuelHistory> fuelHistories = dashboardResponseBody.getPreviousFuelHistories();
                        for (PreviousFuelHistory history : fuelHistories) {
                            Log.e("Raju", "Date: " + history.getDate());
                            Log.e("Raju", "Vehicle: " + history.getBrtaRegNum());
                            Log.e("Raju", "Driver Name: " + history.getDriverName());
                            Log.e("Raju", "Fuel: " + history.getFuel());
                            Log.e("Raju", "Fuel Unit: " + history.getFuelUnit());
                            Log.e("Raju", "Fuel Cost: " + history.getFuelCost());
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Response Unsuccessful", Toast.LENGTH_SHORT).show();
                    Log.e("Raju", "Response Unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<DashboardResponseBody> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "API call Error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG,"API Call Error");
            }
        });

    }



}