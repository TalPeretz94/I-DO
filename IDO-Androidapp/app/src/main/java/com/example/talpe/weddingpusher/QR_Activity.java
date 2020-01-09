package com.example.talpe.weddingpusher;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QR_Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final String QR_TEXT_KEY = "QR_TEXT_KEY";

    private ZXingScannerView scannerView;
    private ZXingScannerView.ResultHandler scannerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        scannerHandler = this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setContentView(scannerView);
                        scannerView.setResultHandler(scannerHandler);
                        scannerView.startCamera();
                        scannerView.setAutoFocus(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        runOnUiThread(() -> {
                            returnResult(null);
                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        runOnUiThread(() ->
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Camera Permission")
                                        .setMessage("please accept")
//                                            .setIcon(R.drawable.ic_location_on_indigo_900_24dp)
                                        .setPositiveButton("ok", (dialogInterface, i) ->
                                                token.continuePermissionRequest())
                                        .setNegativeButton("cancel", (dialogInterface, i) -> {
                                                token.cancelPermissionRequest();
                                                returnResult(null);
                                        })
                                        .create()
                                        .show());
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
        scannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    public void returnResult(String result) {
        Intent returnIntent = new Intent();
        if (result!=null) {
            returnIntent.putExtra(QR_TEXT_KEY, result);
            setResult(RESULT_OK, returnIntent);
        }
        else
            setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void handleResult(Result rawResult) {
        returnResult(rawResult.getText());
    }
}
