package com.test.bleproject.UI;

import static com.test.bleproject.SplashActivity.SplashActivity.PREFS_NAME;
import static com.test.bleproject.UI.WebViewActivity.brta_reg_num;
import static com.test.bleproject.UI.WebViewActivity.fuelLiters;
import static com.test.bleproject.UI.WebViewActivity.fuelType;
import static com.test.bleproject.UI.WebViewActivity.meterReadingStr;
import static com.test.bleproject.UI.WebViewActivity.model;
import static com.test.bleproject.UI.WebViewActivity.selectedDriverName;
import static com.test.bleproject.UI.WebViewActivity.serial_num;
import static com.test.bleproject.UI.WebViewActivity.totalFuelPrice;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.test.bleproject.R;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class PrintVoucer extends AppCompatActivity {

    private TextView receiptHeading,datetime, driverName, modelview, brta_reg_numview, serial_numview, current_meter_reading, Fuel_Type,fuel_ammount,total_price;
    private ListView printerListView; // ListView for paired printers
    private ArrayAdapter<String> adapter;
    private ArrayList<BluetoothDevice> pairedDevices; // To store paired Bluetooth devices
    private String currentDate, currentTime;

    //new added
    private BluetoothDevice selectedDevice;
    private static final String SELECTED_PRINTER = "Ble";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_voucer);

        // Set status bar color and ensure icons are always white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.purple_700));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }



            // HomeActivity custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.printvoucher_actionbar);

            View customActionBarView = actionBar.getCustomView();

            // Handle "Cancel" click
            ImageView Cancel_Voucher_Print = customActionBarView.findViewById(R.id.Cancel_Voucher_Print);
            Cancel_Voucher_Print.setOnClickListener(v -> {
                Intent intent = new Intent(PrintVoucer.this, HomeActivity.class);
                startActivity(intent);
                finish();
            });
        }

        initializeUI();

        Button selectprinter = findViewById(R.id.seletprinter);
        selectprinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectprinter.setEnabled(false);

                if (!ensureBluetoothIsEnabled()) {
                    Toast.makeText(PrintVoucer.this, "Turn On Your Bluetooth & Connect Printer", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    displayPairedDevices();
                }
                new Handler().postDelayed(() -> selectprinter.setEnabled(true), 1000);
            }
        });

        printerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RajuListView", "you touch listview");
                // Check for Bluetooth connect permission
                if (ActivityCompat.checkSelfPermission(PrintVoucer.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission if not granted
                    ActivityCompat.requestPermissions(PrintVoucer.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                    return;
                }

                // Get the selected position
                int position1 = printerListView.getCheckedItemPosition();
                if (position1 != ListView.INVALID_POSITION) {
                    // Get the selected Bluetooth device
                    BluetoothDevice selectedDevice = pairedDevices.get(position1);
                    saveSelectedPrinter(selectedDevice);
                    printerListView.setVisibility(View.GONE);

                    // Show a message confirming the selection
                    Toast.makeText(PrintVoucer.this, "Selected Printer: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrintVoucer.this, "Please select a printer", Toast.LENGTH_SHORT).show();
                }
            }
        });





        Button print = findViewById(R.id.Print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print.setEnabled(false);

                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String printerInfo = sharedPreferences.getString(SELECTED_PRINTER, null);

                if (printerInfo != null) {
                    BluetoothDevice selectedDevice = getPairedDeviceByName(printerInfo.split("\n")[1]);
                    if (selectedDevice != null) {
                        doPrint(selectedDevice);
                        printerListView.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(PrintVoucer.this, "Saved printer not found. Please select a printer.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int position = printerListView.getCheckedItemPosition();
                    if (position != ListView.INVALID_POSITION) {
                        BluetoothDevice selectedDevice = pairedDevices.get(position);
                        saveSelectedPrinter(selectedDevice);
                        printerListView.setVisibility(View.GONE);
                        doPrint(selectedDevice);
                    } else {
                        Toast.makeText(PrintVoucer.this, "Please select a printer", Toast.LENGTH_SHORT).show();
                    }
                }

                new Handler().postDelayed(() -> print.setEnabled(true), 1000);
            }
        });



        loadSelectedPrinter();



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
                    Intent intent = new Intent(PrintVoucer.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Optional, finishes the current activity
                })

                // If the user chooses "No", just dismiss the dialog and stay on the current activity
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void initializeUI(){
        // Initialize UI components
        receiptHeading = findViewById(R.id.receiptHeading);
        driverName = findViewById(R.id.driverName);
        modelview = findViewById(R.id.model);
        brta_reg_numview = findViewById(R.id.brta_reg_num);
        serial_numview = findViewById(R.id.serial_number);
        current_meter_reading = findViewById(R.id.current_meter_reading);
        Fuel_Type = findViewById(R.id.Fuel_Type);
        fuel_ammount = findViewById(R.id.fuel_ammount);
        total_price = findViewById(R.id.total_price);

        printerListView = findViewById(R.id.listviewbluetoothdevice);
        pairedDevices = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, new ArrayList<>());
        printerListView.setAdapter(adapter);
        printerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Set up the receipt details
        setupReceiptDetails();
    }

    private void setupReceiptDetails() {
        receiptHeading = findViewById(R.id.receiptHeading);
        datetime = findViewById(R.id.datetime);
        driverName.setText(selectedDriverName);
        modelview.setText(model);
        brta_reg_numview.setText(brta_reg_num);
        serial_numview.setText(serial_num);
        current_meter_reading.setText(meterReadingStr);
        Fuel_Type.setText(fuelType);
        fuel_ammount.setText(String.valueOf(fuelLiters));
        total_price.setText(String.valueOf(totalFuelPrice));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        currentDate = dateFormat.format(new Date());
        currentTime = timeFormat.format(new Date());

        String formattedText = "Luna construction & Engineering";
        receiptHeading.setText(Html.fromHtml(formattedText));
        String formattedText2 = "Date: " + currentDate + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "Time: " + currentTime;
        datetime.setText(Html.fromHtml(formattedText2));
    }

    // Method to display paired devices in the ListView
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void displayPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            //check if permission is granted
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) !=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},1);
                return;
            }

            // Clear the list and adapter before adding new data
            pairedDevices.clear();
            adapter.clear();

            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

            if(devices != null && !devices.isEmpty()){
                printerListView.setVisibility(View.VISIBLE);
                for (BluetoothDevice device : devices) {
                    pairedDevices.add(device);
                    adapter.add(device.getName() + "\n" + device.getAddress());
                }
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean ensureBluetoothIsEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            return false;
        }

        return true;
    }




//private void doPrint(BluetoothDevice printerDevice) {
//
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Request Bluetooth permissions if not granted (for Android 12+)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//            return;
//        }
//
//        // Connect to the printer
//        try {
//            BluetoothSocket bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); // Use standard SPP UUID
//            bluetoothSocket.connect();
//
//            // Get the output stream for sending data to the printer
//            OutputStream outputStream = bluetoothSocket.getOutputStream();
//
//            // Prepare and print receipt data
//            String receiptContent = getReceiptContent();
//            outputStream.write(receiptContent.getBytes("UTF-8"));
//            outputStream.flush();
//
//            // Send ESC/POS command to print and feed
//            outputStream.write(new byte[]{0x0A});  // Line feed (new line)
//            outputStream.write(new byte[]{0x1D, 0x56, 0x41});  // ESC/POS command for print and cut (optional)
//
//            outputStream.close();
//            bluetoothSocket.close();
//
//            Toast.makeText(this, "Print successful", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            Toast.makeText(this, "Failed to print: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }



//    private void doPrint(BluetoothDevice printerDevice) {
//
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Request Bluetooth permissions if not granted (for Android 12+)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//            return;
//        }
//
//        BluetoothSocket bluetoothSocket = null;
//        try {
//            // Try to connect to the printer
//            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); // Use standard SPP UUID
//            bluetoothSocket.connect();
//
//            // Get the output stream for sending data to the printer
//            OutputStream outputStream = bluetoothSocket.getOutputStream();
//
//            // Prepare and print receipt data
//            String receiptContent = getReceiptContent();
//            outputStream.write(receiptContent.getBytes("UTF-8"));
//            outputStream.flush();
//
//            // Send ESC/POS command to print and feed
//            outputStream.write(new byte[]{0x0A});  // Line feed (new line)
//            outputStream.write(new byte[]{0x1D, 0x56, 0x41});  // ESC/POS command for print and cut (optional)
//
//            outputStream.close();
//            Toast.makeText(this, "Print successful", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            // Handle Bluetooth connection errors
//            if (e.getMessage().contains("socket might closed")) {
//                Toast.makeText(this, "Printer is unavailable or turned off", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Failed to print: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//            e.printStackTrace();
//        } finally {
//            // Ensure the Bluetooth socket is closed, even in case of errors
//            if (bluetoothSocket != null) {
//                try {
//                    bluetoothSocket.close();
//                } catch (IOException closeException) {
//                    closeException.printStackTrace();
//                }
//            }
//        }
//    }

    private void doPrint(BluetoothDevice printerDevice) {
        // Check Bluetooth support
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Bluetooth permissions (Android 12+ requires BLUETOOTH_CONNECT)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        // Permission is granted, perform the Bluetooth connection and printing in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            BluetoothSocket bluetoothSocket = null;
            try {
                // Try to connect to the printer
                bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                bluetoothSocket.connect();

                // Get the output stream for sending data to the printer
                OutputStream outputStream = bluetoothSocket.getOutputStream();

                // Prepare and print receipt data
                String receiptContent = getReceiptContent();
                outputStream.write(receiptContent.getBytes("UTF-8"));
                outputStream.flush();

                // Send ESC/POS command to print and feed
                outputStream.write(new byte[]{0x0A});  // Line feed (new line)
                outputStream.write(new byte[]{0x1D, 0x56, 0x41});  // ESC/POS command for print and cut (optional)

                outputStream.close();

                // Update UI after printing is done (must use the main thread)
                runOnUiThread(() -> Toast.makeText(PrintVoucer.this, "Print successful", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                // Handle Bluetooth connection errors
                final String errorMsg;
                if (e.getMessage().contains("socket might closed")) {
                    errorMsg = "Printer is unavailable or turned off";
                } else {
                    errorMsg = "Failed to print: " + e.getMessage();
                }

                // Update UI with the error message (must use the main thread)
                runOnUiThread(() -> Toast.makeText(PrintVoucer.this, errorMsg, Toast.LENGTH_SHORT).show());

                e.printStackTrace();
            } finally {
                // Ensure the Bluetooth socket is closed, even in case of errors
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException closeException) {
                        closeException.printStackTrace();
                    }
                }
            }
        });
    }



    private void saveSelectedPrinter(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SELECTED_PRINTER, device.getName() + "\n" + device.getAddress());
        editor.apply();
        Toast.makeText(this, "Printer selected: " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    private  void loadSelectedPrinter() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String printerInfo = sharedPreferences.getString(SELECTED_PRINTER, null);
        if (printerInfo != null) {
            Toast.makeText(this, "Using saved printer: " + printerInfo, Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothDevice getPairedDeviceByName(String address) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                return null;  // Return null if permission is not granted
            }

            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                if (device.getAddress().equals(address)) {
                    return device;
                }
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


        // Method to prepare receipt content
    private String getReceiptContent() {
        StringBuilder receipt = new StringBuilder();
        receipt.append(" Luna Construction & Engineering \n");
        receipt.append("      Refueling Receipt \n");
        receipt.append("-----------------------------\n");
        receipt.append("Date: ").append(currentDate).append("\n");
        receipt.append("Time: ").append(currentTime).append("\n\n");
        receipt.append("Driver: ").append(selectedDriverName).append("\n");
        receipt.append("Model: ").append(model).append("\n");
        receipt.append("Reg Number: ").append(brta_reg_num).append("\n");
        receipt.append("Serial Number: ").append(serial_num).append("\n");
        receipt.append("Current Meter Reading: ").append(meterReadingStr).append("\n");
        receipt.append("Fuel Type: ").append(fuelType).append("\n");
        receipt.append("Fuel Ammount(L): ").append(fuelLiters).append("\n");
        receipt.append("Total Price(Taka): ").append(totalFuelPrice).append("\n");
        receipt.append("-----------------------------\n");
        receipt.append("Thank you for using our service!\n\n");
        return receipt.toString();
    }
}
