package id.hipe.base;

/**
 * Created by rochmanz on 26/03/18.
 */

public class BasePresenter<T extends BaseView> implements Presenter<T> {

    private T view;

    @Override
    public void attachView(T view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public T getView() {
        return view;
    }
}
