package id.hipe.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


/**
 * Created by dikaputra on 8/24/17.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager manager, @NonNull Fragment
            fragment, boolean animate, int frame) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (animate) {
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim
                    .slide_out_right);
        }
        transaction.replace(frame, fragment);
        transaction.commit();
    }

    public static void addFragmentToActivity(@NonNull FragmentManager manager, @NonNull Fragment
            fragment, boolean animate, int frame, String TAG, FragmentManager
                                                     .OnBackStackChangedListener listener) {
        if (listener != null) {
            manager.addOnBackStackChangedListener(listener);
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if (animate) {
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim
                    .slide_out_right);
        }
        transaction.add(frame, fragment);
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    public static Fragment getCurrentFragment(FragmentManager manager, int frame) {
        return manager.findFragmentById(frame);
    }

    public static boolean isActivityFinish(Context context) {
        return context == null || ((Activity) context).isFinishing();
    }

    public static void setStatusBarColor(Activity activity, int color, boolean lightIcon) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(color));
            View decor = window.getDecorView();
            if (lightIcon) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                decor.setSystemUiVisibility(0);
            }
        }
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        if (focus != null)
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void fullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static boolean isServiceRunning(Context activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("hasil service is run", true + "");
                return true;
            }
        }
        Log.d("hasil service is run", false + "");
        return false;
    }
}
