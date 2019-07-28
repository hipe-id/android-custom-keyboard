package id.hipe.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.RecyclerView;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 25/05/18.
 */
public class CustomSuggestionRecyclerview extends RecyclerView {

    public CustomSuggestionRecyclerview(Context context) {
        super(context);
    }

    public CustomSuggestionRecyclerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSuggestionRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = View.MeasureSpec.makeMeasureSpec(StringUtils.getDp(getContext(), 85), View.MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);

    }
}
