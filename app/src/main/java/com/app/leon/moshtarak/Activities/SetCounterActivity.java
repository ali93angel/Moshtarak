package com.app.leon.moshtarak.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Infrastructure.ICallback;
import com.app.leon.moshtarak.Models.DbTables.LastBillInfo;
import com.app.leon.moshtarak.Models.Enums.BundleEnum;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.HttpClientWrapper;
import com.app.leon.moshtarak.Utils.LovelyTextInputDialog;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SetCounterActivity extends BaseActivity implements ICallback<LastBillInfo> {
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.buttonSign)
    Button buttonSign;
    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.editText3)
    EditText editText3;
    @BindView(R.id.editText4)
    EditText editText4;
    @BindView(R.id.editText5)
    EditText editText5;
    View viewFocus;
    Context context;
    String billId, number, phoneNumber;
    SharedPreference sharedPreference;
    boolean f = false;

    @Override
    protected void initialize() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = Objects.requireNonNull(inflater).inflate(R.layout.set_counter_content, findViewById(R.id.set_counter_activity));
        @SuppressLint("CutPasteId") ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        parentLayout.addView(childLayout);
        ButterKnife.bind(this);
        context = this;
        setComponentPosition();
        accessData();
        sharedPreference = new SharedPreference(context);
        phoneNumber = sharedPreference.getMobileNumber();//.replaceFirst("09", "");
        setTextChangedListener();
        setOnButtonSignClickListener();
    }

    void changeEditTextSize(boolean b) {
        if (b && f) {
        }
        f = true;
    }

    void showDialog() {
        LovelyTextInputDialog lovelyTextInputDialog = new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.orange1)
                .setTitle("test1")
                .setMessage("test2")
                .setInputFilter("همه فیلدها باید پر شود.", text -> {
                    String s1, s2, s3, s4, s5;
                    EditText editTextNumber = LovelyTextInputDialog.getEditTextNumber(1);
                    if (editTextNumber.getText().length() < 1)
                        return false;
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(2);
                    if (editTextNumber.getText().length() < 1)
                        return false;
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(3);
                    if (editTextNumber.getText().length() < 1)
                        return false;
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(4);
                    if (editTextNumber.getText().length() < 1)
                        return false;
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(5);
                    return editTextNumber.getText().length() >= 1;
//                            return text.matches("\\w+");
                })
                .setConfirmButton(R.string.confirm, text -> {
                    String s1, s2, s3, s4, s5;
                    EditText editTextNumber = LovelyTextInputDialog.getEditTextNumber(1);
                    s1 = editTextNumber.getText().toString();
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(2);
                    s2 = editTextNumber.getText().toString();
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(3);
                    s3 = editTextNumber.getText().toString();
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(4);
                    s4 = editTextNumber.getText().toString();
                    editTextNumber = LovelyTextInputDialog.getEditTextNumber(5);
                    s5 = editTextNumber.getText().toString();
                    Log.e("Number", s1.concat(s2).concat(s3).concat(s4).concat(s5));
                    editText1.setText(s1);
                    editText2.setText(s2);
                    editText3.setText(s3);
                    editText4.setText(s4);
                    editText5.setText(s5);
                })
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                });
        lovelyTextInputDialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTextChangedListener() {
        editText1.setOnClickListener(v -> showDialog());
        editText2.setOnClickListener(v -> showDialog());
        editText3.setOnClickListener(v -> showDialog());
        editText4.setOnClickListener(v -> showDialog());
        editText5.setOnClickListener(v -> showDialog());
    }

    private void setOnButtonSignClickListener() {
        buttonSign.setOnClickListener(view -> {
            boolean cancel = false;
            View viewFocus;
            if (editText5.getText().length() < 1) {
                cancel = true;
                viewFocus = editText5;
                viewFocus.requestFocus();
            }
            if (editText4.getText().length() < 1) {
                cancel = true;
                viewFocus = editText4;
                viewFocus.requestFocus();
            }
            if (editText3.getText().length() < 1) {
                cancel = true;
                viewFocus = editText3;
                viewFocus.requestFocus();
            }

            if (editText2.getText().length() < 1) {
                cancel = true;
                viewFocus = editText2;
                viewFocus.requestFocus();
            }
            if (editText1.getText().length() < 1) {
                cancel = true;
                viewFocus = editText1;
                viewFocus.requestFocus();
            }
            if (!cancel) {
                number = editText1.getText().toString();
                number = number.concat(editText2.getText().toString()).concat(editText3.getText().toString())
                        .concat(editText4.getText().toString()).concat(editText5.getText().toString());
                sendNumber();
            }
        });
    }

    private void accessData() {
        SharedPreference appPrefs = new SharedPreference(context);
        if (!appPrefs.checkIsNotEmpty()) {
            Intent intent = new Intent(getApplicationContext(), SignAccountActivity.class);
            startActivity(intent);
            finish();
        } else {
            billId = appPrefs.getBillID();
        }
    }

    private void sendNumber() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService sendNumber = retrofit.create(IAbfaService.class);
        Call<LastBillInfo> call = sendNumber.sendNumber(billId, number, phoneNumber);
        HttpClientWrapper.callHttpAsync(call, SetCounterActivity.this, context, ProgressType.SHOW.getValue());
    }

    @Override
    public void execute(LastBillInfo lastBillInfo) {
        Intent intent = new Intent(getApplicationContext(), LastBillActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleEnum.BILL_ID.getValue(), lastBillInfo.getBillId());
        bundle.putString(BundleEnum.PAY_ID.getValue(), lastBillInfo.getPayable());
        bundle.putString(BundleEnum.NEW.getValue(), lastBillInfo.getCurrentReadingNumber());
        bundle.putString(BundleEnum.PRE.getValue(), lastBillInfo.getPreReadingNumber());
        bundle.putString(BundleEnum.AB_BAHA.getValue(), lastBillInfo.getAbBaha());
        bundle.putString(BundleEnum.TAX.getValue(), lastBillInfo.getMaliat());
        bundle.putString(BundleEnum.DATE.getValue(), lastBillInfo.getDeadLine());
        bundle.putString(BundleEnum.COST.getValue(), lastBillInfo.getPayable());

        bundle.putString(BundleEnum.USE.getValue(), lastBillInfo.getUsageM3());
        bundle.putString(BundleEnum.USE_LENGTH.getValue(), lastBillInfo.getDuration());
        bundle.putString(BundleEnum.USE_AVERAGE.getValue(), lastBillInfo.getRate());
        bundle.putString(BundleEnum.PRE_READING_DATE.getValue(), lastBillInfo.getPreReadingDate());
        bundle.putString(BundleEnum.CURRENT_READING_DATE.getValue(), lastBillInfo.getCurrentReadingDate());


        bundle.putString(BundleEnum.PRE_DEBT_OR_OWE.getValue(), lastBillInfo.getPreDebtOrOwe());
        bundle.putString(BundleEnum.TAKALIF_BOODJE.getValue(), lastBillInfo.getBoodje());
        bundle.putString(BundleEnum.KARMOZDE_FAZELAB.getValue(), lastBillInfo.getKarmozdFazelab());

        bundle.putBoolean(BundleEnum.IS_PAYED.getValue(), lastBillInfo.isPayed());

        intent.putExtra(BundleEnum.DATA.getValue(), bundle);
        startActivity(intent);
    }

    void setComponentPosition() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = Objects.requireNonNull(wm).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;
        linearLayout1.setY(height - 14 * height / 25);
        linearLayout2.setY(height - 2 * height / 5);
    }
}
