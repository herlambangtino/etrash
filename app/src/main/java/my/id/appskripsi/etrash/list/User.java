package my.id.appskripsi.etrash.list;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private SharedPreferences preferences;
    private String KEY_ID_USER = "iduser";
    private String KEY_NM_USER = "nmuser";
    private String KEY_EMAIL = "email";
    private String KEY_WHATSAPP = "whatsapp";
    private String KEY_ALAMAT = "alamat";
    private String KEY_IS_LOGGED_IN = "islogin";

    public User(Context context){
        String PREFS_NAME = "UserPref";
        preferences = context.getSharedPreferences(PREFS_NAME,context.MODE_PRIVATE);
    }

    public void setIdUser(String idUser){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ID_USER,idUser);
        editor.apply();
    }

    public void setIsLoggedIn(String isLoggedIn){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_IS_LOGGED_IN,isLoggedIn);
        editor.apply();
    }

    public void setNmUser(String nmuser){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_NM_USER,nmuser);
        editor.apply();
    }

    public void setEmail(String email){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL,email);
        editor.apply();
    }

    public void setWhatsapp(String whatsapp){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_WHATSAPP,whatsapp);
        editor.apply();
    }

    public void setAlamat(String alamat){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ALAMAT,alamat);
        editor.apply();
    }

    public String getIsLoggedIn(){
        return preferences.getString(KEY_IS_LOGGED_IN,null);
    }

    public String getIdUser(){
        return preferences.getString(KEY_ID_USER,null);
    }

    public String getNmUser(){
        return preferences.getString(KEY_NM_USER,null);
    }

    public String getEmail(){
        return preferences.getString(KEY_EMAIL,null);
    }

    public String getWhatsapp(){
        return preferences.getString(KEY_WHATSAPP,null);
    }

    public String getAlamat(){
        return preferences.getString(KEY_ALAMAT,null);
    }
}
