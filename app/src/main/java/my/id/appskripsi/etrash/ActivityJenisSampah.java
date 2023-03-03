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

public class ActivityJenisSampah  extends AppCompatActivity {

    private ProgressDialog progressDialog = null;
    private AppCompatButton btnToHome;
    private MaterialRippleLayout btnBack;
    View section1, section2, section3, section4, section5, section6;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jenissampah);

        progressDialog = ProgressDialog.show(this, "", "Please wait...", true, true);
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        section1 = findViewById(R.id.section1);
        section2 = findViewById(R.id.section2);
        section3 = findViewById(R.id.section3);
        section4 = findViewById(R.id.section4);
        section5 = findViewById(R.id.section5);
        section6 = findViewById(R.id.section6);

        View header1 = findViewById(R.id.header1);
        header1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section1.getVisibility() == View.GONE)
                {
                    section1.setVisibility(View.VISIBLE);
                }
                else
                {
                    section1.setVisibility(View.GONE);
                }
            }
        });

        View header2 = findViewById(R.id.header2);
        header2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section2.getVisibility() == View.GONE)
                {
                    section2.setVisibility(View.VISIBLE);
                }
                else
                {
                    section2.setVisibility(View.GONE);
                }
            }
        });

        View header3 = findViewById(R.id.header3);
        header3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section3.getVisibility() == View.GONE)
                {
                    section3.setVisibility(View.VISIBLE);
                }
                else
                {
                    section3.setVisibility(View.GONE);
                }
            }
        });

        View header4 = findViewById(R.id.header4);
        header4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section4.getVisibility() == View.GONE)
                {
                    section4.setVisibility(View.VISIBLE);
                }
                else
                {
                    section4.setVisibility(View.GONE);
                }
            }
        });

        View header5 = findViewById(R.id.header5);
        header5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section5.getVisibility() == View.GONE)
                {
                    section5.setVisibility(View.VISIBLE);
                }
                else
                {
                    section5.setVisibility(View.GONE);
                }
            }
        });

        View header6 = findViewById(R.id.header6);
        header6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (section6.getVisibility() == View.GONE)
                {
                    section6.setVisibility(View.VISIBLE);
                }
                else
                {
                    section6.setVisibility(View.GONE);
                }
            }
        });

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
