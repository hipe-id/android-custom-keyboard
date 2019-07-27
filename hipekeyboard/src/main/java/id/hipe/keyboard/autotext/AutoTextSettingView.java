package id.hipe.keyboard.autotext;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import com.zuragan.shopkeepr.R;
import com.zuragan.shopkeepr.base.BaseAdapter;
import com.zuragan.shopkeepr.data.api.model.AutoTextWord;
import com.zuragan.shopkeepr.data.database.RoomDB;
import com.zuragan.shopkeepr.utility.AnalyticUtils;
import com.zuragan.shopkeepr.utility.PreferenceUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.List;

public class AutoTextSettingView extends LinearLayout
        implements View.OnClickListener, BaseAdapter.ItemCallback<AutoTextWord> {
    private final Context context;
    private WordAdapter adapter;
    private Callback callback;
    private RoomDB db;

    public AutoTextSettingView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public AutoTextSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zuragan_auto_text_view_setting, this, true);
        ImageView addBtn = view.findViewById(R.id.add_btn);
        ImageView backBtn = view.findViewById(R.id.back_btn);
        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        RecyclerView list = view.findViewById(R.id.autotext_list);

        db = RoomDB.getInstance(context);

        list.setLayoutManager(new LinearLayoutManager(context));
        list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        adapter = new WordAdapter();
        adapter.setItemCallback(this);
        list.setAdapter(adapter);
        refreshData();
    }

    public void show() {
        if (this.getVisibility() == GONE || this.getVisibility() == INVISIBLE) {
            this.setVisibility(VISIBLE);
        }
    }

    public void hide() {
        if (this.getVisibility() == VISIBLE) {
            this.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            switch (v.getId()) {
                case R.id.add_btn:
                    callback.onAddShortcut();
                    break;
                case R.id.back_btn:
                    callback.onBackSetting();
                    break;
            }
        }
    }

    public void refreshData() {
        Observable.fromCallable(() -> db.autoTextWordDao().getAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<AutoTextWord>>() {
                    @Override
                    public void onNext(List<AutoTextWord> autoTextWords) {
                        adapter.addAll(autoTextWords);
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

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onAdapterClick(View view, int position, AutoTextWord autoTextWord, Bundle bundle) {
        switch (view.getId()) {
            case R.id.autotext_option_btn:
                showMenu(view, autoTextWord);
                break;
            default:
                if (callback != null) {
                    String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
                    AnalyticUtils.getInstance(getContext()).showQuickReplyToolbar(imei);
                    callback.onAutoTextClick(autoTextWord);
                }
        }
    }

    private void showMenu(View view, final AutoTextWord word) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.getMenuInflater().inflate(R.menu.autotext_popup, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    if (callback != null) {
                        callback.onEditShortcut(word);
                    }
                    break;
                case R.id.menu_delete:
                    delete(word);
                    break;
            }
            return false;
        });
        menu.show();
    }

    private void delete(AutoTextWord word) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(R.string.delete_confirm)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> Completable.fromAction(() -> db.autoTextWordDao().delete(word))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        String imei = PreferenceUtils.getDataStringFromSP(getContext(), "imei", "");
                                        AnalyticUtils.getInstance(getContext()).deleteQuickReply(imei);
                                        refreshData();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Timber.e(e);
                                    }
                                }))
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
        try {
            AlertDialog alert = builder.create();
            Window window = alert.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.token = getWindowToken();
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void onAddShortcut();

        void onBackSetting();

        void onEditShortcut(AutoTextWord item);

        void onAutoTextClick(AutoTextWord text);
    }

    class WordAdapter extends BaseAdapter<AutoTextWord> {

        @Override
        protected int setView(int viewType) {
            return R.layout.item_autotext_config;
        }

        @Override
        protected RecyclerView.ViewHolder itemViewHolder(View view, int viewType) {
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (holder instanceof WordViewHolder) {
                ((WordViewHolder) holder).onBind(get(position), position, itemCallback);
            }
        }
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView optionBtn;
        private AppCompatTextView title;
        private AppCompatTextView content;
        private View itemView;

        public WordViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.autotext_format_title);
            content = itemView.findViewById(R.id.autotext_format_content);
            optionBtn = itemView.findViewById(R.id.autotext_option_btn);
        }

        public void onBind(final AutoTextWord item, final int position,
                           final BaseAdapter.ItemCallback<AutoTextWord> callback) {
            title.setText(item.getShortcut());
            content.setText(item.getContent());
            optionBtn.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onAdapterClick(v, position, item, null);
                }
            });

            itemView.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onAdapterClick(v, position, item, null);
                }
            });
        }
    }
}
