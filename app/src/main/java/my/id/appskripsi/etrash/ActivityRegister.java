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
import my.id.appskripsi.etrash.list.User;

public class ActivityRegister  extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private EditText etEmail, etNmUser, etWhatsapp, etAlamat, etPassword;
    private AppCompatButton btnLogin, btnRegister;
    User mUserData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        etEmail = findViewById(R.id.etEmail);
        etNmUser = findViewById(R.id.etNmUser);
        etWhatsapp = findViewById(R.id.etWhatsapp);
        etAlamat = findViewById(R.id.etAlamat);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String nmuser = etNmUser.getText().toString();
                String whatsapp = etWhatsapp.getText().toString();
                String alamat = etAlamat.getText().toString();
                String password = etPassword.getText().toString();
                if(email.equals("") || nmuser.equals("") || whatsapp.equals("") || alamat.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Data belum lengkap. Silakan lengkapi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(email,nmuser,whatsapp,alamat,password);
                }
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });
    }

    private void register(final String email, final String nmuser, final String whatsapp, final String alamat, final String password){

        String url = BaseAPI.urlServer+"register";

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
                                Toast.makeText(getApplicationContext(), "Register berhasil. Silakan login", Toast.LENGTH_SHORT).show();
                            }
                            else if (jsonStatus.equals("Duplicate")) {
                                Toast.makeText(getApplicationContext(), "Register gagal. Email telah digunakan pengguna lain", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
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
                params.put("email", email);
                params.put("nmuser", nmuser);
                params.put("whatsapp", whatsapp);
                params.put("alamat", alamat);
                params.put("password", password);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest,"register");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
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

