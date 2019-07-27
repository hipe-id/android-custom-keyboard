package id.hipe.keyboard.autotext;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.data.api.model.AutoTextWord;
import com.zuragan.shopkeepr.data.database.RoomDB;
import com.zuragan.shopkeepr.utility.AnalyticUtils;
import com.zuragan.shopkeepr.utility.PreferenceUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.List;

/**
 * Created by Dika Putra on 23/05/18.
 */
public class AutoTextSuggestionView extends LinearLayout implements View.OnClickListener {
    public static final int DEFAULT_LIMIT = 3;
    private RoomDB db;
    private Callback callback;

    public AutoTextSuggestionView(Context context) {
        super(context);
        initViews(context);
    }

    public AutoTextSuggestionView(Context context,
                                  @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        db = RoomDB.getInstance(context);
    }

    public boolean isShowing() {
        return this.getVisibility() == VISIBLE;
    }

    public void show() {
        if (this.getVisibility() == GONE || this.getVisibility() == INVISIBLE) {
            this.setVisibility(VISIBLE);
            if (callback != null) {
                callback.onAutoTextSuggestionShow(true);
            }
        }
    }

    public void hide() {
        if (this.getVisibility() == VISIBLE) {
            removeAllViews();
            this.setVisibility(GONE);
            if (callback != null) {
                callback.onAutoTextSuggestionShow(false);
            }
        }
    }

    public void refreshData(String format, int limit) {
        if (format != null) {
            Timber.d("hasil text: " + format);
            Observable.fromCallable(() -> db.autoTextWordDao().getAll(format.toLowerCase() + "%", limit))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<List<AutoTextWord>>() {
                        @Override
                        public void onNext(List<AutoTextWord> autoTextWords) {
                            removeAllViews();
                            for (int i = 0; i < autoTextWords.size(); i++) {
                                if (i == autoTextWords.size() - 1) {
                                    addSuggestion(autoTextWords.get(i), autoTextWords.size(), false);
                                } else {
                                    addSuggestion(autoTextWords.get(i), autoTextWords.size(), true);
                                }
                            }
                            if (autoTextWords.size() > 0 && !isShowing()) {
                                show();
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
    }

    private void addSuggestion(AutoTextWord word, int size, boolean divider) {
        LayoutParams p =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
        p.weight = (1f / size);

        LinearLayout ll = new LinearLayout(getContext());
        ll.setTag(word);
        ll.setOrientation(VERTICAL);
        ll.setPadding(32, 4, 32, 4);
        ll.setLayoutParams(p);
        ll.setOnClickListener(this);

        ll.addView(
                initText(word.getShortcut(), R.color.white, R.dimen.suggestion_text_size_small, true));
        ll.addView(initText(word.getContent(), R.color.grey, R.dimen.suggestion_text_size_big, false));

        this.addView(ll);

        if (divider) {
            View d = new View(getContext());
            LayoutParams dp =
                    new LayoutParams(1,
                            80);
            d.setLayoutParams(dp);
            d.setBackgroundResource(R.drawable.autotext_suggestion_divider);
            this.addView(d);
        }
        String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
        AnalyticUtils.getInstance(getContext()).showQuickReplySuggestion(imei);
    }

    private AppCompatTextView initText(String text, @ColorRes int color, @DimenRes int size,
                                       boolean isShortcut) {
        LayoutParams p =
                new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
        AppCompatTextView tv = new AppCompatTextView(getContext());
        if (isShortcut) {
            char fl = text.toUpperCase().charAt(1);
            StringBuilder sb = new StringBuilder(text);
            sb.setCharAt(1, fl);
            tv.setText(sb);
        } else {
            tv.setText(text);
        }
        tv.setSingleLine(true);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(size));
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setHorizontallyScrolling(false);
        tv.setTextColor(getResources().getColor(color));
        if (!isShortcut) {
            tv.setPadding(12, 0, 0, 0);
        }
        tv.setLayoutParams(p);
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            AutoTextWord word = (AutoTextWord) v.getTag();
            if (word != null) {
                callback.onAutoTextSuggestionClick(word.getContent());
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onAutoTextSuggestionClick(String text);

        void onAutoTextSuggestionShow(boolean isShow);
    }
}
