package id.hipe.keyboard;

import android.content.Context;
import com.zuragan.shopkeepr.base.BasePresenter;
import com.zuragan.shopkeepr.data.api.RequestManager;
import com.zuragan.shopkeepr.di.ActivityContext;
import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 09/05/18.
 */
public class SoftKeyboardPresenter extends BasePresenter<ISoftKeyboardView> {

    private RequestManager requestManager;
    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public SoftKeyboardPresenter(RequestManager requestManager, @ActivityContext Context context) {
        this.requestManager = requestManager;
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

    public void checkOngkir() {
        // TODO: 09/05/18 get check ongkir from api
    }
}
