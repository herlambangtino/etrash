package my.id.appskripsi.etrash.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    public static SharedPreferences preferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Boolean hasSendToast(Context context) {
        return preferences(context).getBoolean("Toast", false);
    }

    public static void setSendToast(Context context, Boolean bool) {
        preferences(context).edit()
                .putBoolean("Toast", bool).apply();
    }
}