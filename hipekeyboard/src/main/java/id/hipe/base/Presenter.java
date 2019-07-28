package id.hipe.base;

/**
 * Created by rochmanz on 26/03/18.
 */

public interface Presenter<V extends BaseView> {

    void attachView(V view);

    void detachView();
}
