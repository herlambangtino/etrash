package my.id.appskripsi.etrash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import my.id.appskripsi.etrash.base.BaseAPI;
import my.id.appskripsi.etrash.base.BaseActivity;
import my.id.appskripsi.etrash.base.PrefManager;
import my.id.appskripsi.etrash.list.User;

public class ActivityLogin  extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private EditText etEmail, etPassword;
    private AppCompatButton btnLogin, btnRegister;
    User mUserData;
    private PrefManager prefManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserData = new User(this);
        prefManager = new PrefManager(this);

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password);
                }
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegister.class);
                startActivity(intent);
            }
        });
    }

    private void Login(final String email, final String password) {

        String url = BaseAPI.urlServer + "login";

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
                                String iduser = jsonArray2.getJSONObject(0).getString("iduser");
                                prefManager.setFirstTimeLaunch(false);
                                mUserData.setIdUser(iduser);
                                mUserData.setIsLoggedIn("true");

                                Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ActivityLogin.this, "Email atau password tidak cocok", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan,coba lagi nanti", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest, "updateProfil");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
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
        builder.setPositiveButton("Ya", listener);
        builder.setNegativeButton("Tidak", listener);
        builder.show();
    }
}

