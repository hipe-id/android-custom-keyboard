package id.hipe.base;

/**
 * Created by rochmanz on 26/03/18.
 */

public interface BaseView {

    void showLoading(boolean show);

    void showMessage(String message);

    void showError(String error);

}
