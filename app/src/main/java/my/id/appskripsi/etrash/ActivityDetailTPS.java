package my.id.appskripsi.etrash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my.id.appskripsi.etrash.adapter.AdapterKecamatan;
import my.id.appskripsi.etrash.adapter.AdapterTPS;
import my.id.appskripsi.etrash.base.BaseAPI;
import my.id.appskripsi.etrash.base.BaseActivity;
import my.id.appskripsi.etrash.base.Utility;
import my.id.appskripsi.etrash.list.Kecamatan;
import my.id.appskripsi.etrash.list.Tps;

public class ActivityDetailTPS extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private MaterialRippleLayout btnBack;
    private String nmtps,latitude,longitude,alamat,jarak,waktutempuh,waktujemput,deskripsi;
    private TextView tvNmTPS, tvAlamat, tvJarakDurasi, tvWaktuJemput, tvDeskripsi;
    private ImageView ivNavigasi, ivBagikan;
    private WebView webView;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Double mylat, mylng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailtps);

        nmtps = getIntent().getStringExtra("nmtps");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        alamat = getIntent().getStringExtra("alamat");
        jarak = getIntent().getStringExtra("jarak");
        waktutempuh = getIntent().getStringExtra("waktutempuh");
        waktujemput = getIntent().getStringExtra("waktujemput");
        deskripsi = getIntent().getStringExtra("deskripsi");

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        tvNmTPS = findViewById(R.id.tvNmTPS);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvJarakDurasi = findViewById(R.id.tvJarakDurasi);
        tvWaktuJemput = findViewById(R.id.tvWaktuJemput);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);

        tvNmTPS.setText(nmtps);
        tvAlamat.setText(alamat);
        tvJarakDurasi.setText("Jarak lokasi tujuan dengan lokasi kamu "+jarak+" dan waktu tempuh sekitar "+waktutempuh);
        tvWaktuJemput.setText(waktujemput);
        tvDeskripsi.setText(deskripsi);

        ivNavigasi = findViewById(R.id.btnNavigasi);
        ivNavigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com?q="+latitude+","+longitude));
                startActivity(browserIntent);
            }
        });

        ivBagikan = findViewById(R.id.btnShare);
        ivBagikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://maps.google.com?q="+latitude+","+longitude);
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new myWebclient());
        webView.getSettings().setBuiltInZoomControls(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(ActivityDetailTPS.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                Toast.makeText(ActivityDetailTPS.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                getLocation();
            }
        }

        initToolbar();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(ActivityDetailTPS.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Get location
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ActivityDetailTPS.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(ActivityDetailTPS.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(ActivityDetailTPS.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            mylat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            mylng = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            webView.loadUrl(BaseAPI.urlServer+"webview/"+String.valueOf(mylat)+"/"+String.valueOf(mylng)+"/"+latitude+"/"+longitude);
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(ActivityDetailTPS.this, "Gagal mendapatkan koordinat", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void initToolbar() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class myWebclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.hide();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressDialog.show();
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

