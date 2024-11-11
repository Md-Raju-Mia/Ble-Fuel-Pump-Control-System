package com.test.bleproject.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.test.bleproject.R;

import java.util.concurrent.ExecutionException;


public class QRScanActivity extends AppCompatActivity {

    private BarcodeScanner barcodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        //homeactivity customa ctionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.qrscanneractivity_actionbar); // Use your custom layout

            View customActionBarView = actionBar.getCustomView();
            ImageView backbuttonfromQRScanScreen = customActionBarView.findViewById(R.id.backbuttonfromQRScanscreen);
            backbuttonfromQRScanScreen.setOnClickListener(v -> {
                Intent intent = new Intent(QRScanActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            });

        }

        // Initialize the ML Kit Barcode Scanner
        barcodeScanner = BarcodeScanning.getClient();

        startCamera();


            // Register a callback for the back button press
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Create an intent to go to the HomeActivity
                    Intent intent = new Intent(QRScanActivity.this, HomeActivity.class);

                    // Set flags to clear the activity stack and start a new task
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Start the HomeActivity
                    startActivity(intent);

                    // Optionally finish the current activity
                    finish();
                }
            });

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Failed to get camera provider", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        PreviewView previewView = findViewById(R.id.camera_preview);  // Correctly cast to PreviewView
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeAnalyzer());

        // Bind the preview and analysis to the lifecycle
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());  // Use getSurfaceProvider() from PreviewView
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            if (imageProxy.getImage() != null) {
                InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                barcodeScanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String qrCodeData = barcode.getRawValue();
                                if (qrCodeData != null) {
                                    // Send the scanned data back to HomeActivity
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("scannedData", qrCodeData);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("QRScan", "Failed to scan QR code", e))
                        .addOnCompleteListener(task -> imageProxy.close());  // Don't forget to close the imageProxy
            }
        }
    }
}