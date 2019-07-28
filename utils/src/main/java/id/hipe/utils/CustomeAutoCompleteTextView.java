package id.hipe.utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

public class CustomeAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private ImageButton mClearButton;

    public CustomeAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setActionButton(ImageButton imageButton) {
        mClearButton = imageButton;
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("");
                mClearButton.setVisibility(GONE);
                setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    public void onActionClick() {
        mClearButton.performClick();
    }

    public void hideActionButton() {
        if (mClearButton != null) {
            mClearButton.setVisibility(GONE);
        }
    }


    @Override
    protected void performFiltering(CharSequence text, int keyCode) {

        if (mClearButton != null) {
            mClearButton.setVisibility(VISIBLE);
        }

        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

    @Override
    public void onFilterComplete(int count) {
        super.onFilterComplete(count);
    }


}