package id.hipe.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 07/05/18.
 */
public class ClearableEditTextView extends AppCompatEditText
        implements View.OnTouchListener, View.OnFocusChangeListener,
        TextWatcherAdapter.TextWatcherListener {

    private static final int[] STATE_ERROR = {R.attr.state_error};
    private boolean enableCursorMove;
    private Location loc = Location.RIGHT;
    private Drawable xD;
    private Listener listener;
    private View.OnTouchListener l;
    private View.OnFocusChangeListener f;
    private boolean isError = false;

    public ClearableEditTextView(Context context) {
        super(context);
        init();
    }

    public ClearableEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearableEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnTouchListener(View.OnTouchListener l) {
        this.l = l;
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener f) {
        this.f = f;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getDisplayedDrawable() != null) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int left =
                    (loc == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - xD.getIntrinsicWidth();
            int right = (loc == Location.LEFT) ? getPaddingLeft() + xD.getIntrinsicWidth() : getWidth();
            boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                    if (listener != null) {
                        listener.didClearText();
                    }
                }
                return true;
            }
        }
        return l != null && l.onTouch(v, event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(!TextUtils.isEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
        if (f != null) {
            f.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public void onTextChanged(EditText view, String text) {
        if (isFocused()) {
            setError(null);
            setClearIconVisible(!TextUtils.isEmpty(text));
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        initIcon();
    }

    @Override
    public void setError(CharSequence error) {
        isError = error != null;
        super.setError(error);
        refreshDrawableState();
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        isError = error != null;
        super.setError(error, icon);
        refreshDrawableState();
    }

    public void setEnableCursorMove(boolean enableCursorMove) {
        this.enableCursorMove = enableCursorMove;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onSelectionChanged(int start, int end) {
        super.onSelectionChanged(start, end);

        if (!enableCursorMove) {
            CharSequence text = getText();
            if (text != null) {
                if (start != text.length() || end != text.length()) {
                    setSelection(text.length(), text.length());
                    return;
                }
            }

            super.onSelectionChanged(start, end);
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isError) {
            mergeDrawableStates(drawableState, STATE_ERROR);
        }
        return drawableState;
    }

    /**
     * Set null disables the icon
     */
    public void setIconLocation(Location loc) {
        this.loc = loc;
        initIcon();
    }

    private void init() {
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(new TextWatcherAdapter(this, this));
        initIcon();
        setClearIconVisible(false);
    }

    private void init(Context context, AttributeSet attrs) {
        init();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.clearable_textview,
                0, 0);
        try {
            enableCursorMove = a.getBoolean(R.styleable.clearable_textview_enableCursorMove, false);
        } finally {
            a.recycle();
        }
    }

    private void initIcon() {
        xD = null;
        if (loc != null) {
            xD = getCompoundDrawables()[loc.idx];
        }
        if (xD == null) {
            xD = ContextCompat.getDrawable(getContext(), R.drawable.ic_close_grey_500_24dp);
        }
        xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
        int min = getPaddingTop() + xD.getIntrinsicHeight() + getPaddingBottom();
        if (getSuggestedMinimumHeight() < min) {
            setMinimumHeight(min);
        }
    }

    private Drawable getDisplayedDrawable() {
        return (loc != null) ? getCompoundDrawables()[loc.idx] : null;
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable[] cd = getCompoundDrawables();
        Drawable displayed = getDisplayedDrawable();
        boolean wasVisible = (displayed != null);
        if (visible != wasVisible) {
            Drawable x = visible ? xD : null;
            super.setCompoundDrawables((loc == Location.LEFT) ? x : cd[0], cd[1],
                    (loc == Location.RIGHT) ? x : cd[2], cd[3]);
        }
    }

    public static enum Location {
        LEFT(0), RIGHT(2);

        final int idx;

        private Location(int idx) {
            this.idx = idx;
        }
    }

    public interface Listener {
        void didClearText();
    }
}
