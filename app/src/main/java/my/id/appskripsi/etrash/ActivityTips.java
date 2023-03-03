package my.id.appskripsi.etrash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ActivityTips  extends AppCompatActivity {

    private ProgressDialog progressDialog = null;
    private AppCompatButton btnToHome;
    private MaterialRippleLayout btnBack;
    View section1, section2, section3, section4, section5, section6;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        initToolbar();
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

