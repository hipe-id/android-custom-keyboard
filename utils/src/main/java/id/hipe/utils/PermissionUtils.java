package id.hipe.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import androidx.fragment.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by dika on 01/03/18.
 */

public class PermissionUtils {
    public static void requestPermission(Activity activity, Fragment fragment,
                                         String[] permissionNames, int requestCode) {
        if (ActivityManager.isUserAMonkey()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && permissionNames != null
                && permissionNames.length > 0) {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, permissionNames, requestCode);
            } else if (fragment != null) {
                fragment.requestPermissions(permissionNames, requestCode);
            }
        }
    }

    public static boolean isGranted(int[] grantResults) {
        boolean isDenied = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isDenied = true;
            }
        }
        return !isDenied;
    }

    public static boolean hasPermission(Activity activity, String... permission) {
        for (String aPermission : permission) {
            if (ContextCompat.checkSelfPermission(activity, aPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
