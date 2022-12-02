package com.example.ridex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageCapturedCallback;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 Class Description:
 This class will allow the user to set up a profile picture when signing up
 */

public class ProfilePictureActivity extends AppCompatActivity {
    private final static String ACTIVITY_NAME = "ProfilePictureActivity";
    PreviewView previewView;
    Camera currCamera;
    ImageButton profileClickBtn;
    Bitmap photoBitmap;
    ImageCapture imageCapture;
    Button continueBtn, uploadBtn;
    ImageView imageView;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private int REQUEST_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        previewView = findViewById(R.id.previewView);
        profileClickBtn = findViewById(R.id.profile_click);
        imageView = findViewById(R.id.profilePictureViewImage);
        continueBtn = findViewById(R.id.profileContinueBtn);
        uploadBtn = findViewById(R.id.profileUploadBtn);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "onStart()");
        if (ContextCompat.checkSelfPermission(ProfilePictureActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Requires camera permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, REQUEST_CAMERA);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "onDestroy()");
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3).setTargetRotation(Surface.ROTATION_180).build();

        Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).setTargetRotation(Surface.ROTATION_180)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        currCamera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
    }

    public void profilePhotoClick(View view){
        // Take the profile picture.
        Executor mainExecutor = ContextCompat.getMainExecutor(getApplicationContext());
//        Toast.makeText(getApplicationContext(), "Clicked on taking the photo", Toast.LENGTH_SHORT).show();
        imageCapture.takePicture(mainExecutor, new OnImageCapturedCallback(){
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                ImageView imageView = findViewById(R.id.profilePictureViewImage);
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                imageView.setImageBitmap(bitmapImage);
                image.close();
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }

    public void continueBtnListener(View view){
        Intent intent = new Intent(ProfilePictureActivity.this, MainActivity.class);
        startActivity(intent);
    }


}