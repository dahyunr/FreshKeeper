package com.example.freshkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.freshkeeper.YourRequestType.Image;
import com.example.freshkeeper.YourRequestType.Feature;
import com.example.freshkeeper.YourRequestType.Request;

public class BarcodeScanActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int ADD_ITEM_REQUEST_CODE = 200;
    private PreviewView previewView;
    private BarcodeScanner barcodeScanner;
    private ProcessCameraProvider cameraProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        previewView = findViewById(R.id.camera_preview);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button scanButton = findViewById(R.id.scan_button);
        Button manualEntryButton = findViewById(R.id.manual_entry_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        scanButton.setOnClickListener(v -> {
            // 바코드 스캔 기능 구현
        });

        manualEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(BarcodeScanActivity.this, AddItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
        });

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();

        barcodeScanner = BarcodeScanning.getClient(options);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreviewAndAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void bindPreviewAndAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            if (imageProxy.getImage() == null) {
                imageProxy.close();
                return;
            }

            @SuppressWarnings("UnsafeOptInUsageError")
            InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (barcodes.isEmpty()) {
                            Log.d("BarcodeScan", "No barcodes detected");
                        } else {
                            for (Barcode barcode : barcodes) {
                                String rawValue = barcode.getDisplayValue();
                                Log.d("BarcodeScan", "Barcode detected: " + rawValue);

                                // API 호출 부분 추가
                                MyApiService apiService = ApiClient.getApiService();

                                // JSON 요청 생성
                                Image imageRequest = new Image();
                                imageRequest.setContent("base64_encoded_image_data");

                                Feature featureRequest = new Feature();
                                featureRequest.setType("TEXT_DETECTION");

                                Request request = new Request();
                                request.setImage(imageRequest);

                                List<Feature> features = new ArrayList<>();
                                features.add(featureRequest);
                                request.setFeatures(features);

                                List<Request> requests = new ArrayList<>();
                                requests.add(request);

                                YourRequestType requestBody = new YourRequestType();
                                requestBody.setRequests(requests);

                                Call<MyResponseType> call = apiService.getData("Bearer " + BuildConfig.API_KEY, requestBody);
                                call.enqueue(new Callback<MyResponseType>() {
                                    @Override
                                    public void onResponse(Call<MyResponseType> call, Response<MyResponseType> response) {
                                        if (response.isSuccessful()) {
                                            MyResponseType product = response.body();
                                            if (product != null) {
                                                // AddItemActivity에 제품 정보를 전달
                                                Intent intent = new Intent(BarcodeScanActivity.this, AddItemActivity.class);
                                                intent.putExtra("barcodeValue", rawValue);
                                                intent.putExtra("productName", product.getName());
                                                intent.putExtra("productImage", product.getImageUrl());
                                                startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
                                            }
                                        } else {
                                            Log.e("API_ERROR", "Response code: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponseType> call, Throwable t) {
                                        Log.e("API_ERROR", "Error: " + t.getMessage());
                                    }
                                });

                                break; // 바코드가 인식되면 루프 종료
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BarcodeScan", "Barcode detection failed", e);
                    })
                    .addOnCompleteListener(task -> {
                        imageProxy.close(); // 반드시 close 호출
                    });
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();  // 카메라 리소스 해제
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();  // 바코드 스캐너 리소스 해제
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ITEM_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Intent resultIntent = new Intent(this, FkmainActivity.class);
                resultIntent.putExtras(data.getExtras());
                startActivity(resultIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "아이템 추가가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
