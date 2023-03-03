package my.id.appskripsi.etrash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
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
import my.id.appskripsi.etrash.list.User;

public class ActivityKecamatan extends BaseActivity {

    private View parent_view;
    public List<Kecamatan> items;
    private RecyclerView recyclerView, recyclerView2;
    private AdapterKecamatan mAdapter;
    private ProgressDialog progressDialog = null;
    private String kecamatan, keyword, prevPage, kodepencarian;
    private Double latitude, longitude;
    LinearLayoutManager layoutManager;
    private MaterialRippleLayout btnBack;
    private TextView nodata;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    User mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kecamatan);

        mUserData = new User(getApplicationContext());

        kodepencarian = getIntent().getStringExtra("kodepencarian");
        kecamatan = getIntent().getStringExtra("kecamatan");
        keyword = getIntent().getStringExtra("keyword");

        LocalBroadcastManager.getInstance(ActivityKecamatan.this).registerReceiver(mMessageReceiver,
                new IntentFilter("pilihkecamatan"));

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        nodata = findViewById(R.id.nodata);

        items = new ArrayList<>();
        mAdapter = new AdapterKecamatan(this, items);

        parent_view = findViewById(R.id.parent_view);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);
        getLocation();
        getKecamatan();
        initToolbar();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            kecamatan = intent.getStringExtra("kecamatan");
            String nmkecamatan = intent.getStringExtra("nmkecamatan");
            getTPS(mUserData.getIdUser(),String.valueOf((latitude)),String.valueOf((longitude)),kecamatan,keyword);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(ActivityKecamatan.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
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
            Toast.makeText(ActivityKecamatan.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(ActivityKecamatan.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(ActivityKecamatan.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(ActivityKecamatan.this, "Gagal mendapatkan koordinat", Toast.LENGTH_SHORT).show();
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
                                intent.putExtra("lastiditem","0");
                                intent.putExtra("kecamatan",kecamatan);
                                intent.putExtra("mylat",latitude);
                                intent.putExtra("mylng",longitude);
                                startActivity(intent);
                            } else if(jsonStatus.equals("Null")){
                                    Toast.makeText(ActivityKecamatan.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ActivityKecamatan.this, "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
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
        btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getKecamatan() {

        String url = BaseAPI.urlServer + "getKecamatan";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray2 = null;
                            jsonArray2 = new JSONArray(response);
                            String jsonStatus = jsonArray2.getJSONObject(0).getString("status");
                            if (jsonStatus.equals("Null")) {
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                for (int d = 1; d < jsonArray2.length(); d++) {
                                    JSONObject job = jsonArray2.getJSONObject(d);

                                    String idkecamatan = job.getString("idkecamatan");
                                    String nmkecamatan = job.getString("nmkecamatan");

                                    Kecamatan list_kecamatan = new Kecamatan(
                                            idkecamatan,
                                            nmkecamatan
                                    );
                                    items.add(list_kecamatan);
                                }
                                mAdapter.notifyDataSetChanged();
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest, "getKecamatan");
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

