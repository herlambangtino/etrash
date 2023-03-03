package my.id.appskripsi.etrash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class NoConnectionActivity extends AppCompatActivity {

    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    Button bt_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection);
        initComponent();
    }

    private void initComponent() {
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        bt_retry = findViewById(R.id.bt_retry);

        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);

        bt_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    progress_bar.setVisibility(View.VISIBLE);
                    lyt_no_connection.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(intent);
                }
                else {
                    progress_bar.setVisibility(View.VISIBLE);
                    lyt_no_connection.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar.setVisibility(View.GONE);
                            lyt_no_connection.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }
            }
        });
    }
}