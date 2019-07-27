package id.hipe.keyboard.ceckOngkir;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.zuragan.shopkeepr.R;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 19/04/18.
 */
public class CheckResiPopup extends PopupWindow {


    View rootView;
    Context mContext;


    /**
     * Constructor
     *
     * @param rootView The top most layout in your view hierarchy. The difference of this view and the screen height will be used to calculate the keyboard height.
     * @param mContext The context of current activity.
     */
    public CheckResiPopup(View rootView, Context mContext) {
        super(mContext);
        this.rootView = rootView;
        this.mContext = mContext;
        View customView = createCustomView();
        setContentView(customView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //default size
        setSize((int) mContext.getResources().getDimension(R.dimen.default_keyboard_window_height),
                WindowManager.LayoutParams.MATCH_PARENT);
    }


    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager =
                    (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;
        } else {
            return rootView.getRootView().getHeight();
        }
    }

    /**
     * Manually set the popup window size
     *
     * @param width  Width of the popup
     * @param height Height of the popup
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    private View createCustomView() {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.popup_cek_ongkir_view, null, false);
        return view;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
