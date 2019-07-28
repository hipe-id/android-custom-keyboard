package id.hipe.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import id.hipe.utils.AnalyticUtils;
import id.hipe.utils.PreferenceUtils;
import timber.log.Timber;

import java.util.List;

public class LatinKeyboardView extends KeyboardView {
    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    private final int OFFSET_BIG = 0;//before 30
    private final int OFFSET_SMALL = 0;//before 10
    Paint paint = new Paint();

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isInputEnabled() {
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

        final int N = mInputMethodProperties.size();
        boolean isInputEnabled = false;

        for (int i = 0; i < N; i++) {

            InputMethodInfo imi = mInputMethodProperties.get(i);
            if (imi.getId().contains(getContext().getPackageName())) {
                isInputEnabled = true;
            }
        }

        if (isInputEnabled) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The long press insert key for popup single key instead of displaying popup window.
     *
     * @return boolean
     */
    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == '.' && !key.modifier) {
            Timber.d("long press punctuation");
            String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
            AnalyticUtils.getInstance(getContext()).keyboardAccessPunctuation(imei);
        }
        return super.onLongPress(key);
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();

        invalidateAllKeys();
    }

    /**
     * Add 123 numbers to labels of top row.
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getResources().getDimension(R.dimen.key_secondary_text_size));
        int keyYLocation = getResources().getDimensionPixelSize(R.dimen.key_secondary_y_offset);
        paint.setColor(getResources().getColor(R.color.white_50));
        //get all your keys and draw whatever you want
        List<Key> keys = getKeyboard().getKeys();
        for (Key key : keys) {
            if (key.label != null) {

                if (key.label.toString().equals(".") && !key.modifier) {
                    canvas.drawText(",!?", key.x + (key.width / 2) + OFFSET_BIG, key.y + keyYLocation, paint);
                }
            }
        }
    }
}