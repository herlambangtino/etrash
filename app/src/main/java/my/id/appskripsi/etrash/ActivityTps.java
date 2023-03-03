package my.id.appskripsi.etrash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import my.id.appskripsi.etrash.adapter.AdapterTPS;
import my.id.appskripsi.etrash.base.BaseAPI;
import my.id.appskripsi.etrash.base.BaseActivity;
import my.id.appskripsi.etrash.list.Tps;
import my.id.appskripsi.etrash.list.User;

public class ActivityTps extends BaseActivity {

    private View parent_view;
    public List<Tps> items,items2;
    private RecyclerView recyclerView, recyclerView2;
    private AdapterTPS mAdapter, mAdapter2;
    private ProgressDialog progressDialog = null;
    private String prevPage, kodepencarian;
    private String kecamatan = "0";
    private String keyword = "";
    private Double latitude, longitude;
    private String lastid = "0";
    private String lastPage = "F";
    LinearLayoutManager layoutManager,layoutManager2;
    private TextView nodata, tvPilihKecamatan;
    private MaterialRippleLayout btnBack;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private EditText cari;
    User mUserData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tps);

        mUserData = new User(getApplicationContext());

        kodepencarian = getIntent().getStringExtra("kodepencarian");
        kecamatan = getIntent().getStringExtra("kecamatan");
        keyword = getIntent().getStringExtra("keyword");
        lastid = getIntent().getStringExtra("lastiditem");

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        cari = (EditText) findViewById(R.id.cari);
        cari.setText(keyword);

        tvPilihKecamatan = findViewById(R.id.tvPilihKecamatan);
        tvPilihKecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityKecamatan.class);
                final String keywordx = cari.getText().toString();
                intent.putExtra("kodepencarian",kodepencarian);
                intent.putExtra("lastiditem","0");
                intent.putExtra("keyword",keywordx);
                intent.putExtra("mylat",latitude);
                intent.putExtra("mylng",longitude);

                startActivity(intent);
            }
        });

        cari.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final String keywordx = cari.getText().toString();
                    getTPS(mUserData.getIdUser(),String.valueOf((latitude)),String.valueOf((longitude)),kecamatan,keywordx);
                    return true;
                }
                return false;
            }
        });

        nodata = findViewById(R.id.nodata);

        items = new ArrayList<>();
        mAdapter = new AdapterTPS(this, items);

        parent_view = findViewById(R.id.parent_view);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);
        getHasilTerdekat(kodepencarian);

        items2 = new ArrayList<>();
        mAdapter2 = new AdapterTPS(this, items2);

        layoutManager2 = new LinearLayoutManager(getApplicationContext());
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView2.setAdapter(mAdapter2);
        initToolbar();

        recyclerView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int total = layoutManager2.getItemCount();
                int firstVisibleItemCount = layoutManager2.findFirstVisibleItemPosition();
                int lastVisibleItemCount = layoutManager2.findLastVisibleItemPosition();

                //to avoid multiple calls to loadMore() method
                    if (total > 0)
                        if ((total - 1) == lastVisibleItemCount){
                            if (lastPage.equals("F")) {
                                getSemuaTPS(kodepencarian, lastid);
                            }
                        } else {
                            progressDialog.dismiss();
                        }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(ActivityTps.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
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
            Toast.makeText(ActivityTps.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(ActivityTps.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(ActivityTps.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(ActivityTps.this, "Gagal mendapatkan koordinat", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, Looper.getMainLooper());
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

    private void getTPS(final String iduser, final String userlat, final String userlng, final String kecamatan, final String keyword){

        String url = BaseAPI.urlServer+"getTPS";

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
                                intent.putExtra("mylat",latitude);
                                intent.putExtra("mylng",longitude);
                                startActivity(intent);
                            } else if(jsonStatus.equals("Null")){
                                Toast.makeText(ActivityTps.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ActivityTps.this, "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
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

    private void getHasilTerdekat(final String kodepencarian) {

        String url = BaseAPI.urlServer + "getHasilTerdekat";

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

                                    String idtps = job.getString("idtps");
                                    String nmtps = job.getString("nmtps");
                                    String alamat = job.getString("alamat");
                                    String idkecamatan = job.getString("idkecamatan");
                                    String nmkecamatan = job.getString("nmkecamatan");
                                    String waktujemput = job.getString("waktujemput");
                                    String deskripsi = job.getString("deskripsi");
                                    String latitude = job.getString("latitude");
                                    String longitude = job.getString("longitude");
                                    String jarak = job.getString("jarak");
                                    String waktutempuh = job.getString("waktutempuh");

                                    Tps list_tps = new Tps(
                                            idtps,
                                            nmtps,
                                            alamat,
                                            idkecamatan,
                                            nmkecamatan,
                                            waktujemput,
                                            deskripsi,
                                            latitude,
                                            longitude,
                                            jarak,
                                            waktutempuh
                                    );
                                    items.add(list_tps);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            getSemuaTPS(kodepencarian, lastid);

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
                params.put("kodepencarian", kodepencarian);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest, "getHasilTerdekat");
    }

    private void getSemuaTPS(final String kodepencarianx, final String lastiditem) {

        String url = BaseAPI.urlServer + "getSemuaTPS";

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
                                lastid = jsonArray2.getJSONObject(0).getString("lastid");
                                lastPage = jsonArray2.getJSONObject(0).getString("lastpage");
                                for (int d = 1; d < jsonArray2.length(); d++) {
                                    JSONObject job = jsonArray2.getJSONObject(d);

                                    String idtps = job.getString("idtps");
                                    String nmtps = job.getString("nmtps");
                                    String alamat = job.getString("alamat");
                                    String idkecamatan = job.getString("idkecamatan");
                                    String nmkecamatan = job.getString("nmkecamatan");
                                    String waktujemput = job.getString("waktujemput");
                                    String deskripsi = job.getString("deskripsi");
                                    String latitude = job.getString("latitude");
                                    String longitude = job.getString("longitude");
                                    String jarak = job.getString("jarak");
                                    String waktutempuh = job.getString("waktutempuh");

                                    Tps list_tps = new Tps(
                                            idtps,
                                            nmtps,
                                            alamat,
                                            idkecamatan,
                                            nmkecamatan,
                                            waktujemput,
                                            deskripsi,
                                            latitude,
                                            longitude,
                                            jarak,
                                            waktutempuh
                                    );
                                    items2.add(list_tps);
                                }
                                mAdapter2.notifyDataSetChanged();
                            }
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ActivityCompat.checkSelfPermission(ActivityTps.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                    Toast.makeText(ActivityTps.this, "Silakan aktifkan izin akses lokasi", Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else {
                                    getLocation();
                                }
                            }

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
                params.put("kodepencarian", kodepencarianx);
                params.put("lastiditem", lastiditem);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest, "getSemuaTPS");
    }

    public void onBackPressed() {
        Intent gotoHome = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(gotoHome);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent gotoHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(gotoHome);
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
