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

public class ActivityProfil  extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private EditText etEmail, etNmUser, etWhatsapp, etAlamat, etPassword;
    private AppCompatButton btnUpdateProfil, btnLogout;
    private MaterialRippleLayout btnBack;
    User mUserData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        mUserData = new User(this);

        etEmail = findViewById(R.id.etEmail);
        etEmail.setText(mUserData.getEmail());
        etEmail.setEnabled(false);
        etNmUser = findViewById(R.id.etNmUser);
        etNmUser.setText(mUserData.getNmUser());
        etWhatsapp = findViewById(R.id.etWhatsapp);
        etWhatsapp.setText(mUserData.getWhatsapp());
        etAlamat = findViewById(R.id.etAlamat);
        etAlamat.setText(mUserData.getAlamat());
        etPassword = findViewById(R.id.etPassword);

        btnUpdateProfil = findViewById(R.id.btnUpdateProfil);
        btnUpdateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nmuser = etNmUser.getText().toString();
                String whatsapp = etWhatsapp.getText().toString();
                String alamat = etAlamat.getText().toString();
                String password = etPassword.getText().toString();
                if(nmuser.equals("") || whatsapp.equals("") || alamat.equals("")) {
                    Toast.makeText(getApplicationContext(), "Data belum lengkap. Silakan lengkapi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateProfil(mUserData.getIdUser(),nmuser,whatsapp,alamat,password);
                }
            }
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityProfil.this);
                builder.setMessage("Apakah anda ingin keluar dari aplikasi?");
                builder.setCancelable(false);

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            mUserData.setIsLoggedIn(null);
                            startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
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
        });

        initToolbar();
    }

    private void updateProfil(final String iduser, final String nmuser, final String whatsapp, final String alamat, final String password){

        String url = BaseAPI.urlServer+"updateProfil";

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
                                Toast.makeText(getApplicationContext(), "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                                mUserData.setIdUser(mUserData.getIdUser());
                                mUserData.setNmUser(nmuser);
                                mUserData.setEmail(mUserData.getEmail());
                                mUserData.setWhatsapp(whatsapp);
                                mUserData.setAlamat(alamat);
                            }
                            else {
                                Toast.makeText(ActivityProfil.this, "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
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
        addToRequestQueue(stringRequest,"updateProfil");
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

    @Override
    public void onBackPressed() {
        Intent gotoHome = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(gotoHome);
    }
}

