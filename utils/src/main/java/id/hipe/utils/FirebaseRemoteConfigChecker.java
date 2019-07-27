package id.hipe.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import timber.log.Timber;

import static com.zuragan.shopkeepr.utility.StringUtils.getAppVersion;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 04/07/18.
 */
public class FirebaseRemoteConfigChecker {
    public static final String KEY_UPDATE_REQUIRED = "force_update_require";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";
    public static final String KEY_MIN_VERSION = "force_update_min_version";
    public static final String KEY_APP_VERSION = "app_version";
    public static final String KEY_UPDATE_REQUIRED_DEBUG = "force_update_require_debug";
    public static final String KEY_CURRENT_VERSION_DEBUG = "force_update_current_version_debug";
    public static final String KEY_MIN_VERSION_DEBUG = "force_update_min_version_debug";
    public static final String KEY_APP_VERSION_DEBUG = "app_version_debug";
    private static final String TAG = FirebaseRemoteConfigChecker.class.getSimpleName();
    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public FirebaseRemoteConfigChecker(@NonNull Context context,
                                       OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    /**
     * force update app
     */
    public void checkAppVersion() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        float googlePlayVersion = (float) remoteConfig.getDouble(KEY_APP_VERSION);
        //String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
        String appVersion = StringUtils.getAppNameVersion(context);
        String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
        float minVersion = (float) remoteConfig.getDouble(KEY_MIN_VERSION);
        boolean forceUpdate = remoteConfig.getBoolean(KEY_UPDATE_REQUIRED);
        if (BuildConfig.DEBUG) {
            googlePlayVersion = (float) remoteConfig.getDouble(KEY_APP_VERSION_DEBUG);
            //currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION_DEBUG);
            appVersion = StringUtils.getAppNameVersion(context);
            minVersion = (float) remoteConfig.getDouble(KEY_MIN_VERSION_DEBUG);
            forceUpdate = remoteConfig.getBoolean(KEY_UPDATE_REQUIRED_DEBUG);
        }

        Timber.d("checkAppVersion() : force update required %s", forceUpdate);
        //Timber.d("checkAppVersion() : currentVersion %s", currentVersion);
        Timber.d("checkAppVersion() : appVersion %s", appVersion);
        Timber.d("checkAppVersion() : minVersion %s", minVersion);
        Timber.d("checkAppVersion() : get app version  %s", getAppVersion());
        Timber.d("checkAppVersion() : updateUrl %s", updateUrl);
        Timber.d("checkAppVersion() : gPlay Version %s", googlePlayVersion);

        if (forceUpdate) {
            Timber.d("onUpdateNeeded()");
            if (getAppVersion() < minVersion && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl);
            }
        } else {
            Timber.d("newUpdateReleased()");
            if (getAppVersion() < googlePlayVersion && onUpdateNeededListener != null) {
                onUpdateNeededListener.newUpdateReleased(updateUrl);
            }
        }
    }

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl);

        void newUpdateReleased(String udateUrl);
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public FirebaseRemoteConfigChecker build() {
            return new FirebaseRemoteConfigChecker(context, onUpdateNeededListener);
        }

        public FirebaseRemoteConfigChecker check() {
            FirebaseRemoteConfigChecker firebaseRemoteConfigChecker = build();
            firebaseRemoteConfigChecker.checkAppVersion();

            return firebaseRemoteConfigChecker;
        }
    }
}
