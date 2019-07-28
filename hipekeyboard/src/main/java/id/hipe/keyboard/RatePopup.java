package id.hipe.keyboard;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;
import id.hipe.utils.AnalyticUtils;
import id.hipe.utils.DialogFactory;
import id.hipe.utils.PreferenceUtils;

/**
 * Created by Dika Putra on 12/07/18.
 */
public class RatePopup {

    public static void showRating(Context context, View root) {
        DialogFactory.createServiceDialog(context,
                null,
                context.getString(R.string.popup_rating),
                context.getString(R.string.btn_word_love),
                context.getString(R.string.btn_word_no),
                root,
                null,
                (dialog, which) -> showCommentDialog(context, root, dialog, true),
                (dialog, which) -> showCommentDialog(context, root, dialog, false), null);
    }

    private static void showCommentDialog(Context context, View root, DialogInterface rateDialog, boolean love) {
        //// FIXME: 11/07/18 pindah ke kelas sendiri
        rateDialog.dismiss();
        String imei = PreferenceUtils.getDataStringFromSP(context, "imei", "");
        AnalyticUtils.getInstance(context).rateLoveApp(imei, love);

        AlertDialog popupRating = DialogFactory.createServiceDialog(context,
                null,
                context.getString(R.string.popup_comment),
                context.getString(R.string.btn_word_ok),
                context.getString(R.string.btn_word_skip),
                root,
                null,
                (dialog, which) -> {
                    AnalyticUtils.getInstance(context).rateReview(imei);
                    openPlayStore(context);
                },
                (dialog, which) -> {
                    PreferenceUtils.setDataIntTOSP(context, PreferenceUtils.COUNT_CEK_ONGKIR, 0);
                    dialog.dismiss();
                }, null);
    }

    private static void openPlayStore(Context context) {
        Toast.makeText(context, context.getString(R.string.popup_toast_rate), Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
