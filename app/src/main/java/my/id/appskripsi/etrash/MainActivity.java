package my.id.appskripsi.etrash;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import my.id.appskripsi.etrash.base.BaseAPI;
import my.id.appskripsi.etrash.base.BaseActivity;
import my.id.appskripsi.etrash.list.Tps;
import my.id.appskripsi.etrash.list.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private ImageButton imagejenisampah,imagetips,imagepembuangan,imagekomplain;
    private ImageView btnProfil;
    private MaterialRippleLayout btnBack;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Double latitude, longitude;
    private String kodepencarian, jarak, waktutempuh;
    private EditText cari;
    private TextView tvNmUser;
    User mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserData = new User(this);

        tvNmUser = findViewById(R.id.tvNmUser);
        tvNmUser.setText(mUserData.getNmUser());

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        btnProfil = findViewById(R.id.btnProfil);
        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProfil.class);
                startActivity(intent);
            }
        });

        imagejenisampah = findViewById(R.id.imagejenisampah);
        imagejenisampah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityJenisSampah.class);
                startActivity(intent);
            }
        });

        imagetips = findViewById(R.id.imagetips);
        imagetips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityTips.class);
                startActivity(intent);
            }
        });

        imagekomplain = findViewById(R.id.imagekomplain);
        imagekomplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityKomplain.class);
                startActivity(intent);
            }
        });

        imagepembuangan = findViewById(R.id.imagepembuangan);
        imagepembuangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTPS(mUserData.getIdUser(),String.valueOf((latitude)),String.valueOf((longitude)),"0","");
            }
        });

       cari = (EditText) findViewById(R.id.cari);
        cari.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final String keywordx = cari.getText().toString();
                    getTPS(mUserData.getIdUser(),String.valueOf((latitude)),String.valueOf((longitude)),"0",keywordx);
                    return true;
                }
                return false;
            }
        });

        initToolbar();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                Toast.makeText(MainActivity.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                getLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
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
            Toast.makeText(MainActivity.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Gagal mendapatkan koordinat", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getTPS(final String iduser, final String userlat, final String userlng, final String kecamatan, final String keyword){

        String url = BaseAPI.urlServer+"getTPS";

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray2 = null;
                            jsonArray2 = new JSONArray(response);
                            String jsonStatus = jsonArray2.getJSONObject(0).getString("status");
                            if (jsonStatus.equals("OK")) {
                                kodepencarian = jsonArray2.getJSONObject(0).getString("kodepencarian");
                                Intent intent = new Intent(getApplicationContext(), ActivityTps.class);
                                intent.putExtra("kodepencarian",kodepencarian);
                                intent.putExtra("keyword","");
                                intent.putExtra("kecamatan","0");
                                intent.putExtra("lastiditem","0");
                                intent.putExtra("mylat",latitude);
                                intent.putExtra("mylng",longitude);
                                startActivity(intent);
                            } else if(jsonStatus.equals("Null")){
                                Toast.makeText(MainActivity.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage("Terjadi kesalahan,coba lagi nanti");
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", iduser);
                params.put("userlat", userlat);
                params.put("userlng", userlng);
                params.put("kecamatan", kecamatan);
                params.put("keyword", keyword);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest,"getTPS");
    }

    private void initToolbar() {
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Apakah anda ingin keluar dari aplikasi?");
        builder.setCancelable(false);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    finishAffinity();
                    System.exit(0);
                }

                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel();
                }
            }
        };
        builder.setPositiveButton("Ya",listener);
        builder.setNegativeButton("Tidak", listener);
        builder.show();
    }
}