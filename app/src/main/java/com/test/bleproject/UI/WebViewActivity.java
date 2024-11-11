package com.test.bleproject.UI;


import static com.test.bleproject.UI.MainActivity.BASE_URL;
import static com.test.bleproject.UI.MainActivity.created_by;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.test.bleproject.ApiService.ApiService;
import com.test.bleproject.DataModel.GetOTPResponse;
import com.test.bleproject.DataModel.ScannedInfoResponse;
import com.test.bleproject.DataModel.submitFormInfoResponse;
import com.test.bleproject.R;
import com.test.bleproject.RetrofitInstance.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WebViewActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE1 = 101;
    private static final int CAMERA_REQUEST_CODE2 = 102;


    private Spinner driverSpinner;
    private Spinner fuelSpinner;

    private TextView receiptHeading,datetime, literLabel, modelview, brta_reg_numview, serial_numview,avg_km_lView,last_intake_priceView,last_intakeView, prev_meter_readingView;
    private EditText priceValue,fuelliterInput, carReading_value,InsertOTPText;
    private ImageView imageView1, imageView2;
    private String price;
    public static int driver_id;
    public static String selectedDriverName,vehicle_id,prev_meter_reading, brta_reg_num, fuelType, model, serial_num, last_intake, last_intake_price, avg_km_l;
    public static double totalFuelPrice;
    public static double fuelLiters;
    public static String meterReadingStr;

    private String currentDate, currentTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.purple_700));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            // Ensuring status bar icons are always white
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
        }

        //homeactivity customa ctionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.webviewactivity_actionbar); // Use your custom layout

            View customActionBarView = actionBar.getCustomView();
            ImageView backbuttonfromQRScanScreen = customActionBarView.findViewById(R.id.backbuttonfromQRScanscreen);
            backbuttonfromQRScanScreen.setOnClickListener(v -> {
                Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            });

        }


        imageView1 = findViewById(R.id.camera1);
        imageView2 = findViewById(R.id.camera2);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView1.setEnabled(false);

                Log.e("Raju", "imageView1  called");
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, CAMERA_REQUEST_CODE1);

                new Handler().postDelayed(() -> imageView1.setEnabled(true), 1000);
            }

        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView2.setEnabled(false);

                Log.e("Raju", "imageView2 called");
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, CAMERA_REQUEST_CODE2);

                new Handler().postDelayed(() -> imageView2.setEnabled(true), 1000);
            }
        });

        Button RequestOTPButton = findViewById(R.id.RequestOTPButton);
        LinearLayout RegenerateOTPSection = findViewById(R.id.RegenerateOTPSection);
        Button btn_submit = findViewById(R.id.btn_submit);
        RequestOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestOTPButton.setEnabled(false);

                RequestOTPButton.setVisibility(View.GONE);
                RegenerateOTPSection.setVisibility(View.VISIBLE);
                btn_submit.setVisibility(View.VISIBLE);
                RequestOTPAPICall();
                new Handler().postDelayed(() -> RequestOTPButton.setEnabled(true), 1000);
            }
        });

        Button RegenarateOTPButton = findViewById(R.id.RegenarateOTPButton);
        RegenarateOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegenarateOTPButton.setEnabled(false);
                RequestOTPAPICall();
                new Handler().postDelayed(() -> RegenarateOTPButton.setEnabled(true), 1000);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_submit.setEnabled(false);
                Log.e("raju", "CallSubmitButtonMethod called");
                CallSubmitButtonMethod();
                new Handler().postDelayed(() -> btn_submit.setEnabled(true), 1000);
            }
        });


        model = getIntent().getStringExtra("model");
        brta_reg_num = getIntent().getStringExtra("brta_reg_num");
        serial_num = getIntent().getStringExtra("serial_number");
        prev_meter_reading = getIntent().getStringExtra("prev_meter_reading");
        last_intake = getIntent().getStringExtra("last_intake");
        last_intake_price = getIntent().getStringExtra("last_intake_price");
        avg_km_l = getIntent().getStringExtra("avg_km_l");
        vehicle_id = getIntent().getStringExtra("vehicle_id");

        List<ScannedInfoResponse.Driver> drivers = getIntent().getParcelableArrayListExtra("drivers");
        List<ScannedInfoResponse.Fuel> fuels = getIntent().getParcelableArrayListExtra("fuel");



        receiptHeading = findViewById(R.id.receiptHeading);
        datetime = findViewById(R.id.datetime);
        literLabel = findViewById(R.id.liter_label);
        fuelliterInput = findViewById(R.id.liter_input);
        priceValue = findViewById(R.id.price_value);
        modelview = findViewById(R.id.model);
        brta_reg_numview = findViewById(R.id.brta_reg_num);
        serial_numview = findViewById(R.id.serial_number);
        prev_meter_readingView = findViewById(R.id.prev_meter_reading);
        last_intakeView = findViewById(R.id.last_intake);
        last_intake_priceView = findViewById(R.id.last_intake_price);
        avg_km_lView = findViewById(R.id.avg_km_l);
        driverSpinner = findViewById(R.id.driver_spinner);
        fuelSpinner = findViewById(R.id.fuel_spinner);
        carReading_value = findViewById(R.id.carReading_value);
        InsertOTPText = findViewById(R.id.InsertOTPText);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        currentDate = dateFormat.format(new Date());
        currentTime = timeFormat.format(new Date());

        String formattedText = "Luna construction & Engineering";
        receiptHeading.setText(Html.fromHtml(formattedText));
        String formattedText2 = "Date: " + currentDate + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Time: " + currentTime;
        datetime.setText(Html.fromHtml(formattedText2));

        modelview.setText(model);
        brta_reg_numview.setText(brta_reg_num);
        serial_numview.setText(serial_num);
        prev_meter_readingView.setText(prev_meter_reading);
        last_intakeView.setText(last_intake);
        last_intake_priceView.setText(last_intake_price);
        avg_km_lView.setText(avg_km_l);
        literLabel.setText(Html.fromHtml("Fuel Amount (<small>Liter</small>)"));


        //Populate Driver Spinner
        List<String> driverNames = new ArrayList<>();
        for (ScannedInfoResponse.Driver driver : drivers) {
            driverNames.add(driver.getDriver_name());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);


        //Set driver listener for the Spinner
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ScannedInfoResponse.Driver selectedDriver = drivers.get(position);
                driver_id = selectedDriver.getId();
                selectedDriverName = selectedDriver.getDriver_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //Populate Fuel Spinner
        List<String> Fuel_Type = new ArrayList<>();
        for (ScannedInfoResponse.Fuel fuel : fuels) {
            Fuel_Type.add(fuel.getFuel_type());

        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Fuel_Type);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelSpinner.setAdapter(adapter2);

        //set Fuel listener for the Spinner
        fuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ScannedInfoResponse.Fuel selectedFuel = fuels.get(position);
                price = String.valueOf(selectedFuel.getUnit_price());
                fuelType = selectedFuel.getFuel_type();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fuelliterInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.WebViewActivity), (v, insets) -> {
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
                .setTitle("Exit to Home")
                .setMessage("Are you sure you want to go back to Home?")

                // If the user chooses "Yes", navigate to the home activity
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Optional, finishes the current activity
                })

                // If the user chooses "No", just dismiss the dialog and stay on the current activity
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST_CODE1) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView1.setImageBitmap(photo);
            }else if (requestCode == CAMERA_REQUEST_CODE2) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView2.setImageBitmap(photo);
            }
        }

    }

    private void calculateTotalPrice(){
        String fuelLiterStr = fuelliterInput.getText().toString();
        if(!fuelLiterStr.isEmpty() && price !=null && !price.isEmpty()){

            try{
                fuelLiters = Double.parseDouble(fuelLiterStr);
                double fuelPrice = Double.parseDouble(price);

                totalFuelPrice = fuelLiters*fuelPrice;
                priceValue.setText(String.format("%.2f", totalFuelPrice));
            }catch (NumberFormatException e){
                e.printStackTrace();
                priceValue.setText("");
            }
        } else{
            priceValue.setText("");
        }
    }


    private void RequestOTPAPICall(){
            Log.e("Raju", "you are in RequestOTPAPICall API");
            Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
            ApiService apiService = retrofit.create(ApiService.class);

            Log.e("Raju", "BaseUrl: "+ BASE_URL);
            Log.e("Raju", "driver_id" + driver_id);


            Call<GetOTPResponse> call = apiService.sendDriverId(driver_id);

            Log.e("Raju", "Api Call Successfull");

            call.enqueue(new Callback<GetOTPResponse>(){

                @Override
                public void onResponse(Call<GetOTPResponse> call, Response<GetOTPResponse> response) {
                    if(response.isSuccessful() && response.body() !=null){
                        GetOTPResponse GetOTPResponse = response.body();
                        if(GetOTPResponse.getStatus()==200){
                            Toast.makeText(WebViewActivity.this, "OTP Sent Successfully: ",Toast.LENGTH_SHORT).show();
                            Log.e("Raju", "OTP successfull");

                        } else if(GetOTPResponse.getStatus()==400){
                            Toast.makeText(WebViewActivity.this, "OTP Send failed: "+GetOTPResponse.getStatus(),Toast.LENGTH_SHORT).show();
                            Log.e("Raju", "OTP send Faild.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetOTPResponse> call, Throwable t) {
                    Toast.makeText(WebViewActivity.this, "Request OTP Failed For System Error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Rajuwebviewactivity","SendOTP API Called Failed");

                }
            });
        }


        private void CallSubmitButtonMethod(){

            if(areFieldsFilled()){
                String driverNameStr  = selectedDriverName;
                String fuelNameStr  = fuelType;
                String fuelLiterStr = fuelliterInput.getText().toString();
                meterReadingStr  = carReading_value.getText().toString();
                String otpStr   = InsertOTPText.getText().toString();
                String fueltotalpriceStr  = priceValue.getText().toString();

                //Prepare text fields as RequestBody
                RequestBody driverId = RequestBody.create(MultipartBody.FORM, String.valueOf(driver_id));
                RequestBody driverName = RequestBody.create(MultipartBody.FORM, driverNameStr);
                RequestBody fuelName = RequestBody.create(MultipartBody.FORM, fuelNameStr);
                RequestBody PrevMeterReading = RequestBody.create(MultipartBody.FORM, prev_meter_reading);
                RequestBody PresentmeterReading = RequestBody.create(MultipartBody.FORM, meterReadingStr);
                RequestBody fuelLiter = RequestBody.create(MultipartBody.FORM, fuelLiterStr);
                RequestBody fueltotalprice = RequestBody.create(MultipartBody.FORM, fueltotalpriceStr);
                RequestBody Vehicle_Id = RequestBody.create(MultipartBody.FORM,vehicle_id);
                RequestBody otp = RequestBody.create(MultipartBody.FORM, otpStr);
                RequestBody Brta_Reg_Number = RequestBody.create(MultipartBody.FORM, brta_reg_num);
                RequestBody Created_By = RequestBody.create(MultipartBody.FORM, created_by);


                MultipartBody.Part image1 = convertImageToMultipart(imageView1, "image1");
                MultipartBody.Part image2 = convertImageToMultipart(imageView2, "image2");


//                Bitmap bitmap1 = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
//                Bitmap bitmap2 = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();
//                String imageBase64_1 = encodeImageToBase64(bitmap1);
//                String imageBase64_2 = encodeImageToBase64(bitmap2);

//                sendSubmitInfoToApi(driverName, fuelLiter,fueltotalprice, meterReading, otp, imageBase64_1, imageBase64_2);
                // Call API
                sendSubmitInfoToApi(driverId,driverName,fuelName, PrevMeterReading,PresentmeterReading, fuelLiter, fueltotalprice, Vehicle_Id, otp, Brta_Reg_Number, Created_By, image1, image2);
            }
        }

//        private String encodeImageToBase64(Bitmap bitmap){
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//            return Base64.encodeToString(byteArray,Base64.DEFAULT);
//        }

        private MultipartBody.Part convertImageToMultipart(ImageView imageView, String imageName){
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if(drawable != null){
                Bitmap bitmap = drawable.getBitmap();
                File file = convertBitmapToFile(bitmap, imageName);
                RequestBody requestFile = RequestBody.create(okhttp3.MediaType.parse("image/jpeg"),file);
                return MultipartBody.Part.createFormData(imageName, file.getName(), requestFile);
            }
            return null;
        }

        private File convertBitmapToFile(Bitmap bitmap, String fileName){
            File file = new File(getCacheDir(), fileName + ".jpg");
            try{
                file.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            return file;
        }

        private boolean areFieldsFilled(){

            if(driverSpinner.getSelectedItem() == null){
                Toast.makeText(this, "Please select a driver", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(fuelliterInput.getText().toString().isEmpty()){
                fuelliterInput.setError("Please Enter fuel amount");
                return false;
            }

            if(carReading_value.getText().toString().isEmpty()){
                carReading_value.setError("Please enter car meter reading");
                return false;
            }

            // Check if Image1 and Image2 are captured (and ensure they are BitmapDrawable)
            if (!(imageView1.getDrawable() instanceof BitmapDrawable)) {
                Toast.makeText(this, "Please capture Car image", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!(imageView2.getDrawable() instanceof BitmapDrawable)) {
                Toast.makeText(this, "Please capture Pump image", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(InsertOTPText.getText().toString().isEmpty()){
                InsertOTPText.setError("Please enter OTP");
                return false;
            }

            return true;
        }


    private void sendSubmitInfoToApi(RequestBody driverId,RequestBody driverName,RequestBody fuelName,RequestBody PrevMeterReading,RequestBody PresentmeterReading,RequestBody fuelLiter,RequestBody fueltotalprice,RequestBody Vehicle_Id,RequestBody otp,RequestBody Brta_Reg_Number,RequestBody Created_By,MultipartBody.Part image1,MultipartBody.Part image2){
        Log.d("Raju","sendSubmitInfoToApi Call");
        printRequestBodyToJson(driverId,driverName,fuelName,PrevMeterReading,PresentmeterReading, fuelLiter, fueltotalprice, Vehicle_Id, otp, Brta_Reg_Number,Created_By, image1, image2);
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        //Make the POST request
        apiService.sendforminfo(driverId,driverName,fuelName,PrevMeterReading,PresentmeterReading, fuelLiter, fueltotalprice, Vehicle_Id, otp, Brta_Reg_Number,Created_By, image1, image2).enqueue(new Callback<submitFormInfoResponse>() {
            @Override
            public void onResponse(Call<submitFormInfoResponse> call, Response<submitFormInfoResponse> response) {
                if(response.isSuccessful() && response.body() !=null){
                    submitFormInfoResponse submitFormInfoResponse = response.body();
                    Log.d("Raju", "Response: " +submitFormInfoResponse.getMessage());

                    if(submitFormInfoResponse.getStatus()==200){
                        Toast.makeText(WebViewActivity.this, "Submit Successful", Toast.LENGTH_SHORT).show();
                        Log.d("Raju", "Send Form Info Successful");
                        Intent intent = new Intent(WebViewActivity.this, PrintVoucer.class);
                        startActivity(intent);
                        finish();
                    }else if(submitFormInfoResponse.getStatus()==400){
                        Toast.makeText(WebViewActivity.this, "Submit Failed", Toast.LENGTH_SHORT).show();

                        Log.d("Raju", "Send Form Info Unsuccessful");

                        if(response.code() == 400 && response.errorBody() !=null){
                            try{
                                String errorBodyString = response.errorBody().string();

                                JSONObject errorJson = new JSONObject(errorBodyString);

                                int status = errorJson.optInt("status",-1);
                                Log.e("Raju", "Error Status:"+status);

                                JSONObject errors = errorJson.optJSONObject("errors");
                                if(errors !=null){
                                    logFieldErrors(errors, "brta_reg_num");
                                    logFieldErrors(errors, "driver_id");
                                    logFieldErrors(errors, "driver_name");
                                    logFieldErrors(errors, "todays_km");
                                    logFieldErrors(errors, "fuel_unit");
                                    logFieldErrors(errors, "fuel_cost");
                                    logFieldErrors(errors, "vehicle_id");
                                    logFieldErrors(errors, "otp");
                                    logFieldErrors(errors, "image1");
                                    logFieldErrors(errors, "image2");
                                }

                            }catch (Exception e) {
                                Log.e("Raju", "Error parsing error body: " + e.getMessage());
                            }
                        }
                    }
                } else {
                    Toast.makeText(WebViewActivity.this, "Submit Failed", Toast.LENGTH_SHORT).show();
                    Log.e("Raju", "Server API Response Unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<submitFormInfoResponse> call, Throwable t) {
                Toast.makeText(WebViewActivity.this, "Submit Data Failed For Client API Call Error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Raju", "Client API call failed: " + t.getMessage());
            }
        });
    }



    // Prepare JSON printout
    private void printRequestBodyToJson(
            RequestBody driverId, RequestBody driverName,RequestBody fuelName, RequestBody previousMeterReading,
            RequestBody meterReading, RequestBody fuelLiter, RequestBody fueltotalprice,
            RequestBody vehicle_id, RequestBody otp, RequestBody brta_reg_num,
            RequestBody created_by, MultipartBody.Part image1, MultipartBody.Part image2) {

        try {
            // Create a JSON object to represent the request
            JSONObject jsonObject = new JSONObject();

            // Add the RequestBody fields
            jsonObject.put("driver_id", getRequestBodyValue(driverId));
            jsonObject.put("driver_name", getRequestBodyValue(driverName));
            jsonObject.put("fuel_type", getRequestBodyValue(fuelName));
            jsonObject.put("previous_km", getRequestBodyValue(previousMeterReading));
            jsonObject.put("todays_km", getRequestBodyValue(meterReading));
            jsonObject.put("fuel_unit", getRequestBodyValue(fuelLiter));
            jsonObject.put("fuel_cost", getRequestBodyValue(fueltotalprice));
            jsonObject.put("vehicle_id", getRequestBodyValue(vehicle_id));
            jsonObject.put("otp", getRequestBodyValue(otp));
            jsonObject.put("brta_reg_num", getRequestBodyValue(brta_reg_num));
            jsonObject.put("created_by", getRequestBodyValue(created_by));

            // Handle the MultipartBody.Part fields by adding file names or descriptions
            jsonObject.put("image1", image1 != null ? Objects.requireNonNull(image1.body().contentType()).toString() : "No image");
            jsonObject.put("image2", image2 != null ? Objects.requireNonNull(image2.body().contentType()).toString() : "No image");

            // Log or print the JSON
            Log.d("RequestBodyToJSON", jsonObject.toString(4)); // Pretty print with indentation
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("RequestBodyToJSON", "Error creating JSON from RequestBody: " + e.getMessage());
        }
    }


    // Helper function to get the value from RequestBody
    private String getRequestBodyValue(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting value";
        }
    }



    // Helper function to log errors for each field
    private void logFieldErrors(JSONObject errors, String fieldName) {
        try {
            JSONArray fieldErrors = errors.optJSONArray(fieldName);
            if (fieldErrors != null) {
                for (int i = 0; i < fieldErrors.length(); i++) {
                    Log.e("Ornab", fieldName + " error: " + fieldErrors.getString(i));
                }
            }
        } catch (JSONException e) {
            Log.e("Ornab", "Error parsing field errors for " + fieldName + ": " + e.getMessage());
        }
    }



}
