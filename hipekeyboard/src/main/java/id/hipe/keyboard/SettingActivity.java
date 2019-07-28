package id.hipe.keyboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.hipe.base.BaseActivity;
import id.hipe.utils.FirebaseRemoteConfigChecker;

import java.util.List;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 17/04/18.
 */
public class SettingActivity extends BaseActivity implements FirebaseRemoteConfigChecker.OnUpdateNeededListener {

    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.enable_settings_textView)
    TextView enableSettingsTextView;
    @BindView(R.id.layout_EnableSetting)
    LinearLayout layoutEnableSetting;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.add_languages)
    TextView addLanguages;
    @BindView(R.id.layout_AddLanguages)
    LinearLayout layoutAddLanguages;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.choose_input_textView)
    TextView chooseInputTextView;
    @BindView(R.id.layout_ChooseInput)
    LinearLayout layoutChooseInput;
    private AlertDialog updateDialog;

    @OnClick(R.id.enable_settings_textView)
    public void onClickEnableSetting() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
    }

    @OnClick(R.id.layout_ChooseInput)
    public void onClickChooseInput() {
        if (isInputEnabled()) {
            ((InputMethodManager) getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
        } else {
            Toast.makeText(this, getResources().getString(R.string.enable_keyboard_first),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // check version apps
        //if (!BuildConfig.DEBUG) {
        FirebaseRemoteConfigChecker.with(this).onUpdateNeeded(this).check();
        //}
    }

    public boolean isInputEnabled() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

        final int N = mInputMethodProperties.size();
        boolean isInputEnabled = false;

        for (int i = 0; i < N; i++) {

            InputMethodInfo imi = mInputMethodProperties.get(i);
            Log.d("INPUT ID", String.valueOf(imi.getId()));
            if (imi.getId().contains(getPackageName())) {
                isInputEnabled = true;
            }
        }

        if (isInputEnabled) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updateDialog != null && !updateDialog.isShowing()) {
            FirebaseRemoteConfigChecker.with(this).onUpdateNeeded(this).check();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateDialog != null) {
            updateDialog.dismiss();
            updateDialog = null;
        }
    }

    public void lunchPreferenceActivity() {
        if (isInputEnabled()) {
            Intent intent = new Intent(this, ImePreferences.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.enable_keyboard_first),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onUpdateNeeded(String updateUrl) {
        updateDialog = new AlertDialog.Builder(this).setTitle("Pembaruan Versi")
                .setMessage(
                        getString(R.string.on_update_needed))
                .setPositiveButton("Perbarui", (dialog1, which) -> {
                    redirectStore(updateUrl);
                })
                .setCancelable(false)
                .create();
        updateDialog.show();
    }

    @Override
    public void newUpdateReleased(String updateUrl) {
        updateDialog = new AlertDialog.Builder(this).setTitle("Pembaruan Versi")
                .setMessage(
                        getString(R.string.new_update_released))
                .setPositiveButton("Perbarui", (dialog1, which) -> {
                    redirectStore(updateUrl);
                })
                .create();
        updateDialog.show();
    }
}
