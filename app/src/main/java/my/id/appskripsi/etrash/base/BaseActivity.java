package my.id.appskripsi.etrash.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import my.id.appskripsi.etrash.NoConnectionActivity;

public class BaseActivity extends AppCompatActivity {

    SweetAlertDialog pDialogLoading, pDialodInfo;
    private RequestQueue requestQueue;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        internetCheck();

    }

    private void internetCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        } else {
            //internet tidak terhubung
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showLoading() {
        pDialogLoading.show();
    }

    public void dismissLoading() {
        pDialogLoading.dismiss();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    public void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showSuccessMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showWarningMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showInfoMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }


    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueuez(JsonObjectRequest request) {
        getRequestQueue().add(request);
    }

    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
