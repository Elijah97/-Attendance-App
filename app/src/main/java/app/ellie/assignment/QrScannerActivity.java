package app.ellie.assignment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        zXingScannerView = findViewById(R.id.zxscannerview);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        zXingScannerView.setResultHandler(QrScannerActivity.this);
                        zXingScannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QrScannerActivity.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(QrScannerActivity.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    @Override
    protected void onStop() {
        zXingScannerView.stopCamera();
        super.onStop();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra(SaveFragment.SCAN_RESULT, rawResult.getText());
        setResult(SaveFragment.RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.startCamera(); // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();           // Stop camera on pause
    }

}
