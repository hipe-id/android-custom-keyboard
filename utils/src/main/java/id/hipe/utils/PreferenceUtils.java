package id.hipe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import timber.log.Timber;

/**
 * Created by rochmanz on 26/03/18.
 */

public class PreferenceUtils {

    public static final String PREF_NAME = "Hipe Keyboard";
    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String IS_LOGIN = "is_login";
    public static final String USERID = "userid";
    public static final String USERTYPE = "usertype";
    public static final String SUBJECTID = "subjectid";
    public static final String DEF_ORIGIN_LOCATION_ID = "origin_location_id";
    public static final String DEF_ORIGIN_LOCATION_NAME = "origin_location_name";
    public static final String DEF_ORIGIN_LOCATION_FULLNAME = "origin_location_fullname";
    public static final String DEF_ORIGIN_LOCATION_TYPE = "origin_location_type";
    public static final String SHOW_ONBOARDING = "show_onboarding";
    public static final String COUNT_CEK_ONGKIR = "count_cek_ongkir";
    private static SharedPreferences sp;

    /**
     * clear all sp
     */
    public static void clearSP(Context context) {
        Timber.d("clear sp");
        context.getSharedPreferences(PREF_NAME, 0).edit().clear().apply();
    }

    public static void setDataIntTOSP(Context context, String to, int data) {
        Timber.d("set data int to SP " + to + " data :" + data);
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(to, data);
        //editor.commit();
        editor.apply();
    }

    public static int getDataIntFromSP(Context context, String from, int defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Timber.d("result : " + sp.getInt(from, defaultValue));
        return sp.getInt(from, defaultValue);
    }

    public static void setDataBooleanToSP(Context context, String to, boolean data) {
        Timber.d("setDataBooleanToSP " + data + " to :" + to);
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(to, data);
        //editor.commit();
        editor.apply();
    }

    public static boolean getDataBooleanFromSP(Context context, String from, boolean defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Timber.d("result " + from + ": " + sp.getBoolean(from, defaultValue));
        return sp.getBoolean(from, defaultValue);
    }

    public static void setDataDoubleToSP(Context context, String to, long data) {
        Timber.d("setDataDoubleToSp " + data);
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(to, data);
        //editor.commit();
        editor.apply();
    }

    public static double getDataDoubleFromSP(Context context, String from, long defaultVal) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Timber.d("data ->" + sp.getLong(from, defaultVal));
        return sp.getLong(from, defaultVal);
    }

    public static void setDataStringToSP(Context context, String to, String data) {
        Timber.d("setDataStringToSP " + to + " data :" + data);
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(to, data);
        //editor.commit();
        editor.apply();
    }

    public static String getDataStringFromSP(Context context, String from, String defaultValue) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Timber.d("data ->" + sp.getString(from, defaultValue));
        return sp.getString(from, defaultValue);
    }
}
