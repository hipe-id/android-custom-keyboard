package id.hipe.keyboard.autotext;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.model.AutoTextWord;
import com.zuragan.shopkeepr.data.database.RoomDB;
import com.zuragan.shopkeepr.utility.AnalyticUtils;
import com.zuragan.shopkeepr.utility.ClearableEditTextView;
import com.zuragan.shopkeepr.utility.PreferenceUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Dika Putra on 08/05/18.
 */
public class AutoTextAddView extends LinearLayout implements View.OnClickListener {
    private final Pattern pattern = Pattern.compile("^\\\\[a-z]*");
    private ClearableEditTextView shortcutTxt;
    private ClearableEditTextView contentTxt;
    private AppCompatButton doneBtn;
    private Callback callback;
    private InputConnection icShortcut;
    private InputConnection icContent;
    private AutoTextWord currentItem;
    private RoomDB db;
    private String prevShortcut;

    public AutoTextAddView(Context context) {
        super(context);
        initView(context);
    }

    public AutoTextAddView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zuragan_auto_text_add, this, true);
        doneBtn = view.findViewById(R.id.done_btn);
        ImageView backBtn = view.findViewById(R.id.back_btn);
        shortcutTxt = view.findViewById(R.id.shortcut_txt);
        contentTxt = view.findViewById(R.id.content_txt);
        icShortcut = shortcutTxt.onCreateInputConnection(new EditorInfo());
        icContent = contentTxt.onCreateInputConnection(new EditorInfo());
        doneBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        db = RoomDB.getInstance(getContext());
        shortcutTxt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                shortcutTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_backslash_black_18dp, 0,
                        0, 0);
            } else {
                if (TextUtils.isEmpty(shortcutTxt.getText())) {
                    shortcutTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_spellcheck_grey_18dp,
                            0, 0, 0);
                }
            }
            if (callback != null) {
                callback.onShortcutFocus(hasFocus);
            }
        });
    }

    public void show(AutoTextWord item) {
        if (this.getVisibility() == GONE || this.getVisibility() == INVISIBLE) {
            if (item != null) {
                this.prevShortcut = item.getShortcut();//with backslash
                this.shortcutTxt.setText(item.getShortcut().substring(1));//without backslash
                this.shortcutTxt.setSelection(item.getShortcut().length() - 1);
                this.contentTxt.setText(item.getContent());
                this.doneBtn.setText(getResources().getString(R.string.edit).toUpperCase());
            } else {
                this.doneBtn.setText(getResources().getString(R.string.save).toUpperCase());
                this.shortcutTxt.setText("");
                this.contentTxt.setText("");
            }
            shortcutTxt.setFocusableInTouchMode(true);
            contentTxt.setFocusableInTouchMode(true);
            contentTxt.requestFocus();
            shortcutTxt.requestFocus();
            this.currentItem = item;
            this.setVisibility(VISIBLE);
        }
    }

    public void show() {
        show(null);
    }

    public void hide() {
        if (this.getVisibility() == VISIBLE) {
            shortcutTxt.setFocusableInTouchMode(false);
            contentTxt.setFocusableInTouchMode(false);
            shortcutTxt.clearFocus();
            contentTxt.clearFocus();
            this.currentItem = null;
            this.prevShortcut = null;
            this.setVisibility(GONE);
        }
    }

    public boolean isShortcutFocused() {
        return shortcutTxt.isFocused();
    }

    public boolean isFocused() {
        return shortcutTxt.isFocused() || contentTxt.isFocused();
    }

    public void commit(StringBuilder composing) {
        if (shortcutTxt.isFocused()) {
            icShortcut.commitText(composing, composing.length());
        }
        if (contentTxt.isFocused()) {
            icContent.commitText(composing, composing.length());
        }
    }

    public int getCapsMode(EditorInfo attr) {
        if (shortcutTxt.isFocused()) {
            return 0;
        }
        if (contentTxt.isFocused()) {
            return icContent.getCursorCapsMode(attr.inputType);
        }
        return 0;
    }

    public void insert(int keyCode) {
        Timber.d("hasil kar: " + keyCode);
        if (shortcutTxt.isFocused()) {
            shortcutTxt.getText()
                    .insert(shortcutTxt.getSelectionStart(), String.valueOf((char) keyCode));
        }
        if (contentTxt.isFocused()) {
            contentTxt.getText()
                    .insert(contentTxt.getSelectionStart(), String.valueOf((char) keyCode));
        }
    }

    public ClearableEditTextView getEditText() {
        if (shortcutTxt.isFocused()) {
            return shortcutTxt;
        }
        if (contentTxt.isFocused()) {
            return contentTxt;
        }
        return null;
    }

    public void finishComposing() {
        if (shortcutTxt.isFocused()) {
            icShortcut.finishComposingText();
        }
        if (contentTxt.isFocused()) {
            icContent.finishComposingText();
        }
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            switch (v.getId()) {
                case R.id.done_btn:
                    String format = shortcutTxt.getText().toString().toLowerCase().trim();
                    String content = contentTxt.getText().toString();
                    if (currentItem == null) {
                        add(new AutoTextWord(fixFormat(format), content));
                    } else {
                        currentItem.setShortcut(fixFormat(format));
                        currentItem.setContent(content);
                        edit(currentItem);
                    }
                    break;
                case R.id.back_btn:
                    callback.onAutoTextAddBackPress();
                    break;
            }
        }
    }

    public String fixFormat(String format) {
        if (format != null && !format.contains("\\")) {
            format = "\\" + format;
        }
        return format;
    }

    private boolean validate(String format, String content) {
        if (format.isEmpty() || content.isEmpty()) {
            Toast.makeText(getContext(), getResources().getString(R.string.empty_field),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!pattern.matcher(format).matches()) {
            Toast.makeText(getContext(), getResources().getString(R.string.format_not_compatible),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void clearText() {
        shortcutTxt.setText("");
        contentTxt.setText("");
        shortcutTxt.clearFocus();
        contentTxt.clearFocus();
    }

    private void add(AutoTextWord data) {
        if (!validate(data.getShortcut(), data.getContent())) {
            return;
        }
        Observable.fromCallable(() -> db.autoTextWordDao().insertIgnore(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (aLong == -1) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.shortcut_already_exist), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            clearText();
                            String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
                            AnalyticUtils.getInstance(getContext()).addQuickReply(imei, data.getShortcut());
                            if (callback != null) {
                                callback.onAutoTextDonePress();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void edit(AutoTextWord data) {
        if (!validate(data.getShortcut(), data.getContent())) {
            return;
        }
        if (this.prevShortcut.equals(data.getShortcut())) {
            updateDb(data);
        } else {
            checkDuplicate(data);
        }
    }

    private void checkDuplicate(AutoTextWord data) {
        Observable.fromCallable(() -> db.autoTextWordDao().getByName(data.getShortcut()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<AutoTextWord>>() {
                    @Override
                    public void onNext(List<AutoTextWord> autoTextWords) {
                        if (autoTextWords.size() == 0) {
                            updateDb(data);
                        } else {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.shortcut_already_exist), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateDb(AutoTextWord data) {
        Completable.fromAction(() -> db.autoTextWordDao().update(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        clearText();
                        String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
                        AnalyticUtils.getInstance(getContext()).editQuickReply(imei);
                        if (callback != null) {
                            callback.onAutoTextDonePress();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public interface Callback {
        void onAutoTextAddBackPress();

        void onAutoTextDonePress();

        void onShortcutFocus(boolean focus);
    }
}
