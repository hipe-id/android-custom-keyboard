package id.hipe.keyboard;

import android.content.Context;
import id.hipe.base.BasePresenter;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 09/05/18.
 */
public class SoftKeyboardPresenter extends BasePresenter<ISoftKeyboardView> {

    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public SoftKeyboardPresenter(Context context) {
        this.compositeDisposable = new CompositeDisposable();
        this.context = context;
    }

    @Override
    public void attachView(ISoftKeyboardView view) {
        super.attachView(view);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
