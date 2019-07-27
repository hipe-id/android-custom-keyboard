package id.hipe.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;

/**
 * Created by Dika Putra on 04/07/18.
 */
public class AnalyticUtils {
    private static volatile AnalyticUtils mInstance;
    private static FirebaseAnalytics f = null;
    private MixpanelAPI mx = null;

    public AnalyticUtils(Context context) {
        if (f == null) {
            f = FirebaseAnalytics.getInstance(context);
        }
        if (mx == null) {
            mx = MixpanelAPI.getInstance(context,
                    context.getString(R.string.mix_panel_token));
        }
    }

    public static AnalyticUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AnalyticUtils.class) {
                if (mInstance == null) {
                    mInstance = new AnalyticUtils(context);
                }
            }
        }
        return mInstance;
    }

    public String getMxId() {
        return mx.getDistinctId();
    }

    public void trackScreen(Activity activity, String name) {
        f.setCurrentScreen(activity, name, null);
    }

    public void identify(String id) {
        f.setUserId(id);
        mx.identify(id);
        mx.getPeople().identify(id);
        mx.getPeople().set("$name", id);
    }

    public void addQuickReply(String imei, String shortcut) {
        String keyEvent = "quickreply_add";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        bundle.putString("shortcut", shortcut);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void editQuickReply(String imei) {
        String keyEvent = "quickreply_edit";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void deleteQuickReply(String imei) {
        String keyEvent = "quickreply_delete";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void showQuickReplySuggestion(String imei) {
        String keyEvent = "quickreply_show_backslash";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void showQuickReplyToolbar(String imei) {
        String keyEvent = "quickreply_show_toolbar";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void cekOngkirCorrectWeight() {
        String keyEvent = "cekongkir_correct_weight";
        Bundle bundle = new Bundle();
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void cekOngkirOriginNotFound(String imei, String value) {
        String keyEvent = "cek_ongkir_origin_not_found";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        bundle.putString("origin", value);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void cekOngkirDestinationNotFound(String imei, String value) {
        String keyEvent = "cek_ongkir_destination_not_found";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        bundle.putString("destination", value);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void cekOngkirCheckedCouriers(String imei, ArrayList<String> couriers) {
        String keyEvent = "cekongkir_checked_couriers";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        bundle.putStringArrayList("couriers", couriers);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void cekOngkirCopyResult(String imei) {
        String keyEvent = "cekongkir_copy";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorAccessMenu(String imei) {
        String keyEvent = "calculator_access_menu";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorCopyResult(String imei) {
        String keyEvent = "calculator_copy_calculation";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorDivision(String imei) {
        String keyEvent = "calculator_use_division";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorMultiplication(String imei) {
        String keyEvent = "calculator_use_multiplication";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorSubstraction(String imei) {
        String keyEvent = "calculator_use_substraction";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorAddition(String imei) {
        String keyEvent = "calculator_use_addition";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorDelete(String imei) {
        String keyEvent = "calculator_use_delete";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorClear(String imei) {
        String keyEvent = "calculator_use_clear";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorUse0(String imei) {
        String keyEvent = "calculator_use_0";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorUse00(String imei) {
        String keyEvent = "calculator_use_00";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void calculatorUse000(String imei) {
        String keyEvent = "calculator_use_000";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void shareItemAccessMenu(String imei) {
        String keyEvent = "shareitem_access_menu";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void onLogoClicked() {
        String keyEvent = "logoZ_access_menu";
        Bundle bundle = new Bundle();
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void keyboardChange(String imei) {
        String keyEvent = "keyboard_change_default";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void keyboardToNumeric(String imei) {
        String keyEvent = "keyboard_change_to_numeric";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void keyboardAccessPunctuation(String imei) {
        String keyEvent = "keyboard_access_punctuation";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void rateLoveApp(String imei, boolean love) {
        String keyEvent = "love_shopkeepr";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        bundle.putBoolean("love", love);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }

    public void rateReview(String imei) {
        String keyEvent = "review_shopkeepr";
        Bundle bundle = new Bundle();
        bundle.putString("imei", imei);
        f.logEvent(keyEvent, bundle);
        mx.track(keyEvent, GsonConverter.bundleToJson(bundle));
    }
}
