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

public class ActivityKomplain  extends BaseActivity {

    private ProgressDialog progressDialog = null;
    private EditText etNmUser, etWhatsapp, etAlamat, etKomplain;
    private AppCompatButton btnKirimKomplain;
    private MaterialRippleLayout btnBack;
    User mUserData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain);

        mUserData = new User(getApplicationContext());

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        etNmUser = findViewById(R.id.etNmUser);
        etNmUser.setText(mUserData.getNmUser());
        etNmUser.setEnabled(false);
        etWhatsapp = findViewById(R.id.etWhatsapp);
        etWhatsapp.setText(mUserData.getWhatsapp());
        etWhatsapp.setEnabled(false);
        etAlamat = findViewById(R.id.etAlamat);
        etAlamat.setText(mUserData.getAlamat());
        etAlamat.setEnabled(false);
        etKomplain = findViewById(R.id.etKomplain);

        btnKirimKomplain = findViewById(R.id.btnKirimKomplain);
        btnKirimKomplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isikomplain = etKomplain.getText().toString();
                if(isikomplain.equals("")) {
                    Toast.makeText(getApplicationContext(), "Komplain tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendKomplain(mUserData.getIdUser(), isikomplain);
                }
            }
        });

        initToolbar();
    }

    private void sendKomplain(final String iduser, final String isi){

        String url = BaseAPI.urlServer+"sendKomplain";

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
                                etKomplain.setText("");
                                Toast.makeText(getApplicationContext(), "Komplain berhasil dikirim", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ActivityKomplain.this, "Terjadi kesalahan pada API", Toast.LENGTH_SHORT).show();
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
                params.put("isi", isi);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        addToRequestQueue(stringRequest,"sendKomplain");
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

