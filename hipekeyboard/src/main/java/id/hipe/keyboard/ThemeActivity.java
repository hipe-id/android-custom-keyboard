package id.hipe.keyboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String THEME_KEY = "theme_key";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        ImageButton themeButton1 = findViewById(R.id.theme1_imageButton);
        ImageButton themeButton2 = findViewById(R.id.theme2_imageButton);
        ImageButton themeButton3 = findViewById(R.id.theme3_imageButton);
        ImageButton themeButton4 = findViewById(R.id.theme4_imageButton);
        ImageButton themeButton5 = findViewById(R.id.theme5_imageButton);
        ImageButton themeButton6 = findViewById(R.id.theme6_imageButton);
        ImageButton themeButton7 = findViewById(R.id.theme7_imageButton);
        ImageButton themeButton8 = findViewById(R.id.theme8_imageButton);
        ImageButton themeButton9 = findViewById(R.id.theme9_imageButton);
        ImageButton themeButton10 = findViewById(R.id.theme10_imageButton);

        themeButton1.setOnClickListener(this);
        themeButton2.setOnClickListener(this);
        themeButton3.setOnClickListener(this);
        themeButton4.setOnClickListener(this);
        themeButton5.setOnClickListener(this);
        themeButton6.setOnClickListener(this);
        themeButton7.setOnClickListener(this);
        themeButton8.setOnClickListener(this);
        themeButton9.setOnClickListener(this);
        themeButton10.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //AdView adView = this.findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //        .addTestDevice("D17FE6D8441E3F2375E3709A2EED851B")
        //        .build();
        //adView.loadAd(adRequest);

        //final InterstitialAd interstitialAd = new InterstitialAd(this);
        //interstitialAd.setAdUnitId(getString(R.string.interstitial_unitID));
        //interstitialAd.loadAd(adRequest);
        //
        //interstitialAd.setAdListener(new AdListener() {
        //    public void onAdLoaded() {
        //        interstitialAd.show();
        //    }
        //});
    }

    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (view.getId()) {
            case R.id.theme1_imageButton:
                editor.putInt(THEME_KEY, 1).apply();
                break;
            case R.id.theme2_imageButton:
                editor.putInt(THEME_KEY, 2).apply();
                break;
            case R.id.theme3_imageButton:
                editor.putInt(THEME_KEY, 3).apply();
                break;
            case R.id.theme4_imageButton:
                editor.putInt(THEME_KEY, 4).apply();
                break;
            case R.id.theme5_imageButton:
                editor.putInt(THEME_KEY, 5).apply();
                break;
            case R.id.theme6_imageButton:
                editor.putInt(THEME_KEY, 6).apply();
                break;
            case R.id.theme7_imageButton:
                editor.putInt(THEME_KEY, 7).apply();
                break;
            case R.id.theme8_imageButton:
                editor.putInt(THEME_KEY, 8).apply();
                break;
            case R.id.theme9_imageButton:
                editor.putInt(THEME_KEY, 9).apply();
                break;
            case R.id.theme10_imageButton:
                editor.putInt(THEME_KEY, 10).apply();
                break;
            default:
                break;
        }

        Toast.makeText(this, "Theme is selected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}