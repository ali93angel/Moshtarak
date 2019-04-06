package com.app.leon.moshtarak.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Models.DbTables.LastBillInfo;
import com.app.leon.moshtarak.Models.Enums.BundleEnum;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.Models.ViewModels.UiElementInActivity;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.HttpClientWrapper;
import com.app.leon.moshtarak.Utils.ICallback;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class SetCounterActivity extends BaseActivity implements ICallback<LastBillInfo> {
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
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
    @BindView(R.id.editTextPhoneNumber)
    EditText editTextPhoneNumber;
    View viewFocus;
    Context context;
    String billId, number, phoneNumber;
    ;

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.set_counter_activity);
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        context = this;
        setComponentPosition();
        setTextChangedListener();
        viewFocus = editText1;
        viewFocus.requestFocus();
        accessData();
        setOnButtonSignClickListener();

    }

    private void setTextChangedListener() {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText2;
                    viewFocus.requestFocus();
                }


            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText3;
                    viewFocus.requestFocus();
                }
            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText4;
                    viewFocus.requestFocus();
                }
            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editText5;
                    viewFocus.requestFocus();
                }
            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = editTextPhoneNumber;
                    viewFocus.requestFocus();
                }
            }
        });
        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    viewFocus = buttonSign;
                    viewFocus.requestFocus();
                }
            }
        });
    }

    private void setOnButtonSignClickListener() {
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {
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
                if (editTextPhoneNumber.getText().length() != 9) {
                    cancel = true;
                    viewFocus = editTextPhoneNumber;
                    viewFocus.requestFocus();
                }
                if (!cancel) {
                    phoneNumber = "09";
                    phoneNumber = phoneNumber.concat(editTextPhoneNumber.getText().toString());
                    number = editText1.getText().toString();
                    number = number.concat(editText2.getText().toString()).concat(editText3.getText().toString())
                            .concat(editText4.getText().toString()).concat(editText5.getText().toString());

                    sendNumber();
                }
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
//        new CustomDialog(DialogType.Green, context, lastBillInfo.getPayable(), context.getString(R.string.dear_user),
//                context.getString(R.string.mamoor), context.getString(R.string.accepted));

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
        intent.putExtra(BundleEnum.DATA.getValue(), bundle);
        startActivity(intent);
    }

    void setComponentPosition() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;
        linearLayout1.setY(height - 13 * height / 25);
        linearLayout2.setY(height - 10 * height / 27);
        linearLayout3.setY(height - 3 * height / 8);
    }
}