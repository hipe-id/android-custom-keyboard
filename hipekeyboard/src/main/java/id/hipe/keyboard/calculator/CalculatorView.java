package id.hipe.keyboard.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.hipe.keyboard.R;
import id.hipe.utils.AnalyticUtils;
import id.hipe.utils.PreferenceUtils;

/**
 * Created by
 * Rochman Zaelani
 * im.rochmanz@gmail.com
 * on 27/04/18.
 * Edited by Burhan
 * on 2/7/18
 */
public class CalculatorView extends LinearLayout {

    public CalculatorListener calculatorListener;
    @BindView(R.id.lbl_process)
    TextView lblProcess;
    @BindView(R.id.lbl_result)
    TextView lblResult;

    public CalculatorView(Context context) {
        super(context);
    }

    public CalculatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public void setListener(CalculatorListener calculatorListener) {
        this.calculatorListener = calculatorListener;
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hipe_calculator_dark_view, this, true);
        ButterKnife.bind(view);
    }

    @OnClick(R.id.btn_back_calculator)
    public void onBtnBack() {
        calculatorListener.onClickBackFromCalculator();
    }

    @OnClick(R.id.btn_copy)
    public void onBtnCopy() {
        AnalyticUtils.getInstance(getContext()).calculatorCopyResult(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));

        String process = lblProcess.getText().toString().trim();
        String result = lblResult.getText().toString().trim();
        if (!process.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("calculator", process + " = " + result);
            clipboard.setPrimaryClip(clip);

            calculatorListener.onCopyClicked(process + " = " + result);
        }

    }

    @OnClick(R.id.btn_clear)
    public void onBtnClear() {
        AnalyticUtils.getInstance(getContext()).calculatorClear(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));

        lblProcess.setText(" ");
        lblResult.setText("0");
    }

    @OnClick(R.id.btn_del)
    public void onBtnDel() {
        AnalyticUtils.getInstance(getContext()).calculatorDelete(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));

        String process = lblProcess.getText().toString().trim();
        if (!process.isEmpty()) {
            lblProcess.setText(process.substring(0, process.length() - 1));
        }
    }

    @OnClick(R.id.btn_dot)
    public void onBtnDot() {
        onKey(".");
    }

    @OnClick(R.id.btn_div)
    public void onBtnDiv() {
        AnalyticUtils.getInstance(getContext()).calculatorDivision(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("/");
    }

    @OnClick(R.id.btn_min)
    public void onBtnMin() {
        AnalyticUtils.getInstance(getContext()).calculatorSubstraction(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("-");
    }

    @OnClick(R.id.btn_kali)
    public void onBtnKali() {
        AnalyticUtils.getInstance(getContext()).calculatorMultiplication(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("*");
    }

    @OnClick(R.id.btn_result)
    public void onBtnResult() {
        try {
            EvaluateEngine engine = new EvaluateEngine();
            double result = engine.evaluate(lblProcess.getText().toString().trim());
            if (isInteger(result)) {
                lblResult.setText(thousandFormat((int) result));
            } else {
                lblResult.setText(String.valueOf(result));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.err_equation, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_plus)
    public void onBtnPlus() {
        AnalyticUtils.getInstance(getContext()).calculatorAddition(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("+");
    }

    @OnClick(R.id.btn_1)
    public void onBtn1() {
        onKey("1");
    }

    @OnClick(R.id.btn_2)
    public void onBtn2() {
        onKey("2");
    }

    @OnClick(R.id.btn_3)
    public void onBtn3() {
        onKey("3");
    }

    @OnClick(R.id.btn_4)
    public void onBtn4() {
        onKey("4");
    }

    @OnClick(R.id.btn_5)
    public void onBtn5() {
        onKey("5");
    }

    @OnClick(R.id.btn_6)
    public void onBtn6() {
        onKey("6");
    }

    @OnClick(R.id.btn_7)
    public void onBtn7() {
        onKey("7");
    }

    @OnClick(R.id.btn_8)
    public void onBtn8() {
        onKey("8");
    }

    @OnClick(R.id.btn_9)
    public void onBtn9() {
        onKey("9");
    }

    @OnClick(R.id.btn_0)
    public void onBtn0() {
        AnalyticUtils.getInstance(getContext()).calculatorUse0(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("0");
    }

    @OnClick(R.id.btn_00)
    public void onBtn00() {
        AnalyticUtils.getInstance(getContext()).calculatorUse00(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("00");
    }

    @OnClick(R.id.btn_000)
    public void onBtn000() {
        AnalyticUtils.getInstance(getContext()).calculatorUse000(PreferenceUtils.getDataStringFromSP(getContext(), "imei", ""));
        onKey("000");
    }

    public void onKey(String key) {
        lblProcess.setText(String.format("%s%s", lblProcess.getText().toString(), key));
    }

    public String thousandFormat(Object object) {
        return String.format("%,d", object);
    }

    public boolean isInteger(double result) {
        return result % 1 == 0;
    }

    public interface CalculatorListener {
        void onClickBackFromCalculator();

        void onCopyClicked(String result);
    }
}
