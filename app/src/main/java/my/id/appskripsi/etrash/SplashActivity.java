package my.id.appskripsi.etrash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import my.id.appskripsi.etrash.base.BaseAPI;
import my.id.appskripsi.etrash.base.BaseActivity;
import my.id.appskripsi.etrash.list.User;

public class SplashActivity  extends BaseActivity {
    User mUserData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mUserData = new User(this);
        internetCheck();
    }

    private void getUserData(final String iduser){

        String url = BaseAPI.urlServer+"getUserData";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray2 = null;
                            jsonArray2 = new JSONArray(response);
                            String jsonStatus = jsonArray2.getJSONObject(0).getString("status");
                            if (jsonStatus.equals("OK")) {
                                String nmuser  = jsonArray2.getJSONObject(0).getString("nmuser");
                                String email  = jsonArray2.getJSONObject(0).getString("email");
                                String whatsapp  = jsonArray2.getJSONObject(0).getString("whatsapp");
                                String alamat  = jsonArray2.getJSONObject(0).getString("alamat");
                                mUserData.setIdUser(mUserData.getIdUser());
                                mUserData.setNmUser(nmuser);
                                mUserData.setEmail(email);
                                mUserData.setWhatsapp(whatsapp);
                                mUserData.setAlamat(alamat);
                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        },
                                        1500);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan,coba lagi nanti", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", iduser);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest,"getUserData");
    }

    private void internetCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            if (mUserData.getIsLoggedIn() != null){
                getUserData(mUserData.getIdUser());
            } else {
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                                startActivity(intent);
                            }
                        },
                        1500);
            }

        } else {
            // internet tidak terhubung
            showErrorMessage("Tidak ada koneksi Internet");
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
