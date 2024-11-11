package com.test.bleproject.UI;

import static com.test.bleproject.SplashActivity.SplashActivity.PREFS_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.test.bleproject.ApiService.ApiService;
import com.test.bleproject.DataModel.LoginResponse;
import com.test.bleproject.R;
import com.test.bleproject.RetrofitInstance.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String BASE_URL ="https://lunabd-ltd.com/";
    public static String androidId;
    public static String deviceModel;
    public static String buildNumber;
    public static String deviceBrand;
    public static String ScannedData;
    public static String UserName="TestDefault";
    public static String UserContactNumber="017.........";
    public static String UserPassword;
    public static String showuserName;
    public static String created_by;


    private boolean showPassword = false;


    private CardView UserLoginLayout;

    private static String Login_Flag = "isLoggedIn";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Window window = getWindow();
//        View decorView = window.getDecorView();
//
//        // Remove the light status bar flag (for light icons on dark background)
//        int flags = decorView.getSystemUiVisibility();
//        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//        decorView.setSystemUiVisibility(flags);
//
//        // Set status bar and navigation bar color
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700));
//        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.purple_700));

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

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Log.e("Raju", "Retrieve DeviceInfo Called from MainActivity Oncreate");
        RetrieveDeviceInfo();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(Login_Flag,false);
        if(isLoggedIn){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }


        setContentView(R.layout.activity_main);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.purple_700));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            //Ensuring status bar icons are always white
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
        }




        UserLoginLayout = findViewById(R.id.loginlayout);
        EditText userNameEditText, passwordEditText;
        userNameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // To toggle password visibility
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the right drawable (eye icon) is clicked
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Toggle between showing and hiding the password
                    if (showPassword) {
                        // Hide password
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.passwordlock, 0, R.drawable.closedeye, 0);
                    } else {
                        // Show password
                        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.passwordlock, 0, R.drawable.open_eye, 0);
                    }
                    // Move the cursor to the end of the text
                    passwordEditText.setSelection(passwordEditText.length());

                    // Toggle the password visibility flag
                    showPassword = !showPassword;

                    // Call performClick() for accessibility
                    passwordEditText.performClick();
                    return true;
                }
            }
            return false;
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);

                String username = userNameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                Log.e("Raju", "User Login Called");


                if(userNameEditText.getText().toString().isEmpty() ){
                    userNameEditText.setError("Please Enter User Name");
                } else if(passwordEditText.getText().toString().isEmpty() ){
                    passwordEditText.setError("Please Enter Password");
                } else{
                    loginUser(username, password);
                }
                new Handler().postDelayed(() -> loginButton.setEnabled(true), 1000);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MainActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    public void RetrieveDeviceInfo(){
        Log.e("Raju", "Retrieve device information from MainActivity SharedPreferences");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        androidId  = sharedPreferences.getString("AndroidID", "Unknown");
        deviceModel = sharedPreferences.getString("DeviceModel", "Unknown");
        buildNumber = sharedPreferences.getString("BuildNumber", "Unknown");
        deviceBrand  = sharedPreferences.getString("DeviceBrand", "Unknown");
        UserName = sharedPreferences.getString("UserName", "Unknown");
        showuserName = sharedPreferences.getString("showuserName", "Unknown");
        UserPassword  = sharedPreferences.getString("UserPassword", "Unknown");
        UserContactNumber  = sharedPreferences.getString("UserContactNumber", "Unknown");
        ScannedData = sharedPreferences.getString("scannedData", "Unknown");
        created_by = sharedPreferences.getString("created_by", "Unknown");


        Log.e("Raju", "Device Info - Android ID: " + androidId + ", Model: " + deviceModel + ", Build: " + buildNumber + ", Brand: "+deviceBrand +
                "User Name: "+ UserName + ", Show User Name: "+ showuserName+  ", User Password: "+ UserPassword +", User Contact Number: "+ UserContactNumber + " ScannedData: " +ScannedData);
    }


    private void loginUser(String username, String password){
        Log.e("Raju", "You are in User Login");
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Call<LoginResponse> call = apiService.loginUser(username, password);
        call.enqueue(new Callback<LoginResponse>(){

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() !=null){
                    LoginResponse loginResponse = response.body();
                    if(loginResponse.getStatusCode()==200){

                        Log.e("RajuLogin", "Login Successful");
//
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getStatus());
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getName());
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getNumber());
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getStatusCode());

                        String usercontactnumber = loginResponse.getNumber();
                        String showuserName = loginResponse.getName();
                        String created_by = loginResponse.created_by();


                        saveLoginFlag(true, username, password, usercontactnumber, showuserName, created_by);



                    } else if(loginResponse.getStatusCode()==400){
                        Toast.makeText(MainActivity.this, "Employee Id/Mobile Number or Password Invalid",Toast.LENGTH_SHORT).show();
                        Log.e("RajuLogin", "Login Failed");
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getStatus());
                        Log.e("RajuLogin","Resopnse Data" +loginResponse.getStatusCode());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                    Toast.makeText(MainActivity.this, "API call failed"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RajuL","Api Called Failed for" +t.getMessage());

            }
        });
    }

    private void saveLoginFlag(boolean isLoggedIn, String username, String password, String usercontactnumber,String showuserName, String created_by){
        Log.e("Raju", "saveLogiFlag Method Called");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn",isLoggedIn);
        editor.putString("UserName",username);
        editor.putString("showuserName",showuserName);
        editor.putString("UserPassword",password);
        editor.putString("UserContactNumber",usercontactnumber);
        editor.putString("created_by",created_by);
        editor.apply();
        Log.e("Raju", "Goes to HomeActivity");
        Log.e("Raju", "Retrieve DeviceInfo Called from MainActivity saveLoginFlag");
        RetrieveDeviceInfo();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }


}