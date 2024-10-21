package com.example.freshkeeper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.example.freshkeeper.network.ApiClient;
import com.example.freshkeeper.network.MyApiService;
import com.example.freshkeeper.network.MyResponseType;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BarcodeScanActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int ADD_ITEM_REQUEST_CODE = 200;
    private PreviewView previewView;
    private BarcodeScanner barcodeScanner;
    private ProcessCameraProvider cameraProvider;
    private boolean isScanning = true;
    private String lastScannedBarcode = "";
    private static final String TAG = "BarcodeScanActivity";  // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        previewView = findViewById(R.id.camera_preview);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button manualEntryButton = findViewById(R.id.manual_entry_button);

        // 카메라 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        manualEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(BarcodeScanActivity.this, AddItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
        });

        // 바코드 스캐너 설정
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ALL_FORMATS)
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
                Toast.makeText(this, "카메라를 시작할 수 없습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void bindPreviewAndAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
            if (!isScanning || imageProxy.getImage() == null) {
                imageProxy.close();
                return;
            }

            InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            for (com.google.mlkit.vision.barcode.common.Barcode barcode : barcodes) {
                                String rawValue = barcode.getDisplayValue();

                                Log.d(TAG, "Scanned Barcode: " + rawValue);
                                if (!rawValue.equals(lastScannedBarcode)) {
                                    isScanning = false;
                                    lastScannedBarcode = rawValue;
                                    fetchProductInfo(rawValue);

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> isScanning = true, 3000);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Barcode detection failed", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void fetchProductInfo(String barcode) {
        Log.d(TAG, "Fetching product info for barcode: " + barcode);
        MyApiService apiService = ApiClient.getApiService();
        Call<MyResponseType> call = apiService.lookupItem(barcode);

        call.enqueue(new Callback<MyResponseType>() {
            @Override
            public void onResponse(Call<MyResponseType> call, Response<MyResponseType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyResponseType.Item> items = response.body().getItems();
                    if (!items.isEmpty()) {
                        MyResponseType.Item item = items.get(0);
                        String productName = item.getTitle();
                        String productImage = item.getImageUrl();

                        if (productImage == null || productImage.isEmpty()) {
                            productImage = "default_image_url";
                        }

                        if (productName == null || productName.isEmpty()) {
                            productName = "Unknown Product";
                        }

                        Intent intent = new Intent(BarcodeScanActivity.this, AddItemActivity.class);
                        intent.putExtra("itemName", productName);  // 상품명 전달
                        intent.putExtra("imagePath", productImage); // 이미지 경로 전달

                        // 로그로 데이터 전달 확인
                        Log.d(TAG, "상품명: " + productName);
                        Log.d(TAG, "상품 이미지: " + productImage);

                        // AddItemActivity로 이동하고 저장 후 FkmainActivity로 이동
                        startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
                    } else {
                        Toast.makeText(BarcodeScanActivity.this, "상품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API 실패, 오류 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MyResponseType> call, Throwable t) {
                Log.e(TAG, "API 호출 실패: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            // AddItemActivity에서 상품이 성공적으로 등록된 후 FkmainActivity로 이동
            Intent intent = new Intent(BarcodeScanActivity.this, FkmainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
        }
    }
}
