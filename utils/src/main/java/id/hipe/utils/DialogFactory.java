package id.hipe.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by cak_upik on 5/8/18.
 */

public class DialogFactory {

    public static MaterialDialog createLoadingDialog(Context context, String content,
                                                     boolean isHorizontalProgress) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 0)
                .progressIndeterminateStyle(isHorizontalProgress)
                .cancelable(false)
                .canceledOnTouchOutside(false);
        return dialog.build();
    }

    public static MaterialDialog createLoadingDialogWithCallback(Context context, String content,
                                                                 boolean isHorizontalProgress, DialogInterface.OnDismissListener dismissListener) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 0)
                .progressIndeterminateStyle(isHorizontalProgress)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .dismissListener(dismissListener);
        return dialog.show();
    }

    public static void createInfoDialog(Context context, String title, String content,
                                        String positiveText) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .show();
    }

    public static MaterialDialog createInfoDialogWithCallback(Context context, String title,
                                                              String content, String positiveText, MaterialDialog.SingleButtonCallback callback) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .onPositive(callback);
        return dialog.show();
    }

    public static MaterialDialog createConfirmationDialog(Context context, String title,
                                                          String content, String positiveText, String negativeText,
                                                          MaterialDialog.SingleButtonCallback onPositiveClick,
                                                          MaterialDialog.SingleButtonCallback onNegativeClick) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .onPositive(onPositiveClick)
                .onNegative(onNegativeClick);
        return dialog.show();
    }

    public static void createErrorDialog(Context context, String error) {
        new MaterialDialog.Builder(context)
                .title(R.string.error_title_dialog)
                .content(error)
                .positiveText("OK")
                .show();
    }

    public static MaterialDialog createInputDialog(Context context, String title, int resHint,
                                                   int maxLength, int minLength, int inputType, MaterialDialog.InputCallback callback) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .title(title)
                .inputRange(minLength, maxLength)
                .inputType(inputType)
                .input(resHint, 0, false, callback)
                .negativeText(R.string.cancel_text_dialog)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
        return dialog.show();
    }

    public static MaterialDialog createCustomDialog(Context context, String title, String content,
                                                    View view, String positiveText, String negativeText,
                                                    MaterialDialog.SingleButtonCallback onPositiveClick,
                                                    MaterialDialog.SingleButtonCallback onNegativeClick) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .customView(view, false)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .onPositive(onPositiveClick)
                .onNegative(onNegativeClick);
        return dialog.show();
    }

    public static AlertDialog createServiceDialog(Context context, String title, String message,
                                                  String pText, String nText, View root, View content,
                                                  DialogInterface.OnClickListener pListener,
                                                  DialogInterface.OnClickListener nListener,
                                                  DialogInterface.OnDismissListener dListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(content)
                .setCancelable(false)
                .setPositiveButton(pText, pListener)
                .setNegativeButton(nText, nListener)
                .setOnDismissListener(dListener);
        AlertDialog alert = null;
        try {
            alert = builder.create();
            if (root != null) {
                Window window = alert.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.token = root.getWindowToken();
                lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                window.setAttributes(lp);
                window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alert;
    }

    private void showInputDialog(Context context, InputMethodManager imm, String title,
                                 String pText, String nText, View root,
                                 DialogInterface.OnClickListener pListener,
                                 DialogInterface.OnClickListener nListener,
                                 DialogInterface.OnDismissListener dListener) {
        LinearLayout ll = new LinearLayout(context);
        ll.setPadding(64, 0, 64, 0);
        AppCompatEditText commentTxt = new AppCompatEditText(context);
        commentTxt.setBackgroundResource(R.drawable.bg_outline_grey);
        commentTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});//limit 100 char
        commentTxt.setHint(R.string.popup_hint);
        commentTxt.setLines(3);
        commentTxt.setPadding(16, 8, 16, 8);
        commentTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        InputConnection icComment =
                commentTxt.onCreateInputConnection(new EditorInfo());
        commentTxt.setFocusableInTouchMode(true);
        commentTxt.requestFocus();
        ll.addView(commentTxt);

        AlertDialog popupRating = DialogFactory.createServiceDialog(context,
                null,
                title,
                pText,
                nText,
                root,
                ll,
                pListener,
                nListener,
                dListener);
        if (popupRating.isShowing()) {
            try {
                Window window = popupRating.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                root.requestFocus();
                imm.showSoftInput(root, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
