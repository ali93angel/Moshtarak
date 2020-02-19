package com.app.leon.moshtarak.Activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Infrastructure.ICallback;
import com.app.leon.moshtarak.Models.DbTables.LastBillInfoV2;
import com.app.leon.moshtarak.Models.Enums.BundleEnum;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.Models.InterCommunation.SimpleMessage;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.Code128;
import com.app.leon.moshtarak.Utils.CustomTab;
import com.app.leon.moshtarak.Utils.HttpClientWrapper;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.pec.mpl.pecpayment.view.PaymentInitiator;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LastBillActivity extends BaseActivity {
    @BindView(R.id.textViewBillId)
    TextView textViewBillId;
    @BindView(R.id.textViewPayId)
    TextView textViewPayId;
    @BindView(R.id.textViewPreNumber)
    TextView textViewPreNumber;
    @BindView(R.id.textViewNewNumber)
    TextView textViewNewNumber;
    @BindView(R.id.textViewPreDate)
    TextView textViewPreDate;
    @BindView(R.id.textViewNewDate)
    TextView textViewNewDate;
    @BindView(R.id.textViewAbBaha)
    TextView textViewAbBaha;
    @BindView(R.id.textViewTax)
    TextView textViewTax;
    @BindView(R.id.textViewKarmozdeFazelab)
    TextView textViewKarmozdeFazelab;
    @BindView(R.id.textViewTakalifBoodje)
    TextView textViewTakalifBoodje;
    @BindView(R.id.textViewPreDebtOrOwe)
    TextView textViewPreDebtOrOwe;
    @BindView(R.id.textViewDate)
    TextView textViewDate;
    @BindView(R.id.textViewCost)
    TextView textViewCost;
    @BindView(R.id.textViewTabsare2)
    TextView textViewTabsare2;
    @BindView(R.id.textViewTabsare3Ab)
    TextView textViewTabsare3Ab;
    @BindView(R.id.textViewTabsare3Fazelab)
    TextView textViewTabsare3Fazelab;
    @BindView(R.id.textViewAbonmanAb)
    TextView textViewAbonmanAb;
    @BindView(R.id.textViewAbonmanFazelab)
    TextView textViewAbonmanFazelab;
    @BindView(R.id.textViewFasleGarm)
    TextView textViewFasleGarm;
    @BindView(R.id.textViewMazadOlgoo)
    TextView textViewMazadOlgoo;
    @BindView(R.id.textViewTotal)
    TextView textViewTotal;

    @BindView(R.id.textViewUseM3)
    TextView textViewUseM3;
    @BindView(R.id.textViewUseLitr)
    TextView textViewUseLitr;
    @BindView(R.id.textViewUseAverage)
    TextView textViewUseAverage;
    @BindView(R.id.textViewUseLength)
    TextView textViewUseLength;
    @BindView(R.id.textViewIsPayed)
    TextView textViewIsPayed;
    @BindView(R.id.textViewLavzemKahande)
    TextView textViewLavzemKahande;
    @BindView(R.id.textViewTaxfif)
    TextView textViewTaxfif;
    @BindView(R.id.textViewPayable)
    TextView textViewPayable;
    @BindView(R.id.textViewPayDate)
    TextView textViewPayDate;
    @BindView(R.id.textViewPayTypeTitle)
    TextView textViewPayTypeTitle;
    @BindView(R.id.textViewBankTitle)
    TextView textViewBankTitle;

    @BindView(R.id.imageViewBarcode)
    ImageView imageViewBarcode;
    Context context;
    String billId, payId, apiKey;
    String id;
    //    String zoneId;
    String address = "https://bill.bpm.bankmellat.ir/bpgwchannel/";
    boolean isPayed = false;
    boolean isFromCardex = false;
    boolean isLastBill = false;
    public static final int requestCodePaymentBill = 199;

    @Override
    protected void initialize() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = Objects.requireNonNull(inflater).inflate(R.layout.last_bill_content, findViewById(R.id.last_bill_activity));
        @SuppressLint("CutPasteId") ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        ButterKnife.bind(this);
        context = this;
        accessData();
        textViewCost.setOnClickListener(view -> {
            getToken();
            if (!isPayed) {
                new CustomTab(address, LastBillActivity.this);
            } else
                Toast.makeText(context, context.getString(R.string.payed_2), Toast.LENGTH_SHORT).show();
        });
    }

    void getToken() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService getToken = retrofit.create(IAbfaService.class);
        Call<SimpleMessage> call = getToken.getToken(apiKey);
        GetToken getToken1 = new GetToken();
        HttpClientWrapper.callHttpAsync(call, getToken1, context, ProgressType.SHOW.getValue());
    }

    void pay(String simpleMessage) {
        Intent intent = new Intent(LastBillActivity.this, PaymentInitiator.class);
        intent.putExtra("Type", "2");
        intent.putExtra("Token", simpleMessage);
        startActivityForResult(intent, requestCodePaymentBill);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodePaymentBill) {
            getPaymentResultCode(resultCode, data);
        }
    }

    private void getPaymentResultCode(int resultCode, Intent data) {
//
//        if (G.onMplResult != null) {
//            G.onMplResult.onResult(false);
//        }

        String enData = "", message = "", status = "0";
        int errorType = 0, orderId = 0;

        switch (resultCode) {
            case 1:// payment ok
                enData = data.getStringExtra("enData");
                message = data.getStringExtra("message");
                status = String.valueOf(data.getIntExtra("status", 0));
                break;
            case 2://payment error
                errorType = data.getIntExtra("errorType", 0);
                orderId = data.getIntExtra("OrderID", 0);
                break;
            case 3://bill payment ok
                enData = data.getStringExtra("enData");
                message = data.getStringExtra("message");
                status = String.valueOf(data.getIntExtra("status", 0));
                break;
            case 4://bill payment error
                errorType = data.getIntExtra("errorType", 0);
                break;
            case 5://internal error payment
                errorType = data.getIntExtra("errorType", 0);
                orderId = data.getIntExtra("OrderID", 0);
                break;
            case 6://internal error bill
                errorType = data.getIntExtra("errorType", 0);
                break;
            case 9:// internal error charge
                errorType = data.getIntExtra("errorType", 0);
                break;
        }
        if (errorType != 0) {
            showErrorTypeMpl(errorType);
        }
    }

    private void showErrorTypeMpl(int errorType) {
        String message = "";
        switch (errorType) {
            case 2:
                message = getString(R.string.time_out_error);
                break;
            case 1000:
                message = getString(R.string.connection_error);
                break;
            case 1001:
                message = getString(R.string.server_error);
                break;
            case 1002:
                message = getString(R.string.network_error);
                break;
            case 201:
                message = getString(R.string.dialog_canceled);
                break;
            case 2334:
                message = getString(R.string.device_root);
                break;
        }

        if (message.length() > 0) {
//            HelperError.showSnackMessage(message, false);
        }
    }

    private void accessData() {
        SharedPreference appPrefs = new SharedPreference(context);
        if (!appPrefs.checkIsNotEmpty()) {
            Intent intent = new Intent(getApplicationContext(), SignAccountActivity.class);
            startActivity(intent);
            finish();
        } else {
            billId = appPrefs.getBillID();
            apiKey = appPrefs.getApiKey();
            fillLastBillInfo();
        }
    }

    class GetToken implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            Log.e("token", simpleMessage.getMessage());
//            pay(simpleMessage.getMessage());
        }
    }

    void fillLatBillFromCounter(Bundle bundle) {
        float floatNumber;
        int intNumber;
        textViewBillId.setText(billId);
        textViewPayId.setText(payId);
        setImageBitmap(imageViewBarcode);
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.COST.getValue())));
        intNumber = (int) floatNumber;
        textViewCost.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.NEW.getValue())));
        intNumber = (int) floatNumber;
        textViewNewNumber.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.PRE.getValue())));
        intNumber = (int) floatNumber;
        textViewPreNumber.setText(String.valueOf(intNumber));
        textViewNewDate.setText(bundle.getString(BundleEnum.CURRENT_READING_DATE.getValue()));
        textViewPreDate.setText(bundle.getString(BundleEnum.PRE_READING_DATE.getValue()));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.USE_AVERAGE.getValue())));
        intNumber = (int) floatNumber;
        textViewUseAverage.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.USE_LENGTH.getValue())));
        intNumber = (int) floatNumber;
        textViewUseLength.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.USE.getValue())));
        intNumber = (int) floatNumber;
        textViewUseM3.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.PRE_DEBT_OR_OWE.getValue())));
        intNumber = (int) floatNumber;
        textViewPreDebtOrOwe.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.TAKALIF_BOODJE.getValue())));
        intNumber = (int) floatNumber;
        textViewTakalifBoodje.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.KARMOZDE_FAZELAB.getValue())));
        intNumber = (int) floatNumber;
        textViewKarmozdeFazelab.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.AB_BAHA.getValue())));
        intNumber = (int) floatNumber;
        textViewAbBaha.setText(String.valueOf(intNumber));
        floatNumber = Float.valueOf(Objects.requireNonNull(bundle.getString(BundleEnum.TAX.getValue())));
        intNumber = (int) floatNumber;
        textViewTax.setText(String.valueOf(intNumber));
        textViewDate.setText(bundle.getString(BundleEnum.DATE.getValue()));
    }

    void fillLastBillInfo() {
        if (getIntent().getExtras() != null) {
            Bundle bundle1 = getIntent().getBundleExtra(BundleEnum.DATA.getValue());//setCounter
            Bundle bundle2 = getIntent().getBundleExtra(BundleEnum.THIS_BILL.getValue());//cardex_no_payed
            Bundle bundle3 = getIntent().getBundleExtra(BundleEnum.THIS_BILL_PAYED.getValue());//cardex_payed
            if (bundle1 != null) {
                payId = bundle1.getString(BundleEnum.PAY_ID.getValue());
                billId = bundle1.getString(BundleEnum.BILL_ID.getValue());
                isPayed = bundle1.getBoolean(BundleEnum.IS_PAYED.getValue());
                fillLatBillFromCounter(bundle1);
            } else {
                isFromCardex = true;
                Retrofit retrofit = NetworkHelper.getInstance();
                final IAbfaService getLastBillInfo = retrofit.create(IAbfaService.class);
                Call<LastBillInfoV2> call = null;
                if (bundle2 != null) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("مشاهده قبض");
                    isPayed = false;
                    call = getLastBillInfo.getThisBillInfo(
                            bundle2.getString(BundleEnum.ID.getValue()),
                            bundle2.getString(BundleEnum.ZONE_ID.getValue()));
                } else if (bundle3 != null) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("اطلاعات پرداخت");
                    isPayed = true;
                    call = getLastBillInfo.getThisPayInfo(
                            bundle3.getString(BundleEnum.ID.getValue()),
                            bundle3.getString(BundleEnum.ZONE_ID.getValue()));
                }
                ThisBill thisBill = new ThisBill();
                HttpClientWrapper.callHttpAsync(call, thisBill, context, ProgressType.SHOW.getValue());
            }
        } else {
            isLastBill = true;
            Retrofit retrofit = NetworkHelper.getInstance();
            final IAbfaService getThisBillInfo = retrofit.create(IAbfaService.class);
            byte[] encodeValue = Base64.encode(billId.getBytes(), Base64.DEFAULT);
            String base64 = new String(encodeValue);
            Call<LastBillInfoV2> call = getThisBillInfo.getLastBillInfo(billId, base64.substring(0, base64.length() - 1));
            ThisBill thisBill = new ThisBill();
            HttpClientWrapper.callHttpAsync(call, thisBill, context, ProgressType.SHOW.getValue());
        }
        if (isPayed)
            textViewIsPayed.setText(context.getString(R.string.payed_2));
    }

    void setImageBitmap(ImageView imageView) {
        Code128 code = new Code128(this);
        String barcode = "";
        for (int count = 0; count < 13 - billId.length(); count++) {
            barcode = barcode.concat("0");
        }
        barcode = barcode.concat(billId);
        for (int count = 0; count < 13 - payId.length(); count++) {
            barcode = barcode.concat("0");
        }
        barcode = barcode.concat(payId);
        code.setData(barcode);
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        Bitmap bitmap = code.getBitmap(width, height / 8);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textViewBillId = null;
        textViewPayId = null;
        textViewPreNumber = null;
        textViewNewNumber = null;
        textViewPreDate = null;
        textViewNewDate = null;
        textViewAbBaha = null;
        textViewTax = null;
        textViewKarmozdeFazelab = null;
        textViewTakalifBoodje = null;
        textViewPreDebtOrOwe = null;
        textViewDate = null;
        textViewCost = null;
        textViewTabsare2 = null;
        textViewTabsare3Ab = null;
        textViewTabsare3Fazelab = null;
        textViewAbonmanAb = null;
        textViewAbonmanFazelab = null;
        textViewFasleGarm = null;
        textViewMazadOlgoo = null;
        textViewTotal = null;
        textViewUseM3 = null;
        textViewUseAverage = null;
        textViewUseLength = null;
        textViewIsPayed = null;
        textViewPayable = null;
        textViewPayDate = null;
        textViewPayTypeTitle = null;
        textViewBankTitle = null;

        imageViewBarcode.setImageDrawable(null);
    }

    class ThisBill implements ICallback<LastBillInfoV2> {
        @SuppressLint("DefaultLocale")
        @Override
        public void execute(LastBillInfoV2 lastBillInfo) {
            androidx.appcompat.widget.LinearLayoutCompat linearLayoutCompat;
            if (isFromCardex && isPayed) {
                //اگر از کاردکس بود و پرداخت شده بود
                ScrollView scrollView = findViewById(R.id.scrollView1);
                scrollView.setVisibility(View.GONE);
                scrollView = findViewById(R.id.scrollView2);
                scrollView.setVisibility(View.VISIBLE);
                textViewPayable.setText(lastBillInfo.getPayableReadable());
                textViewPayDate.setText(lastBillInfo.getPayDay());
                textViewPayTypeTitle.setText(lastBillInfo.getPayTypeTitle());
                textViewBankTitle.setText(lastBillInfo.getBankTitle());
            } else if (isLastBill) {
                //اگر از آخرین قبض بود
                billId = lastBillInfo.getBillId().trim();
                payId = lastBillInfo.getPayId().trim();
                textViewBillId.setText(billId);
                textViewPayId.setText(payId);//TODO Sepehr
                setImageBitmap(imageViewBarcode);
                //float floatNumber = Float.valueOf(lastBillInfo.getCurrentReadingNumber());TODO
                float floatNumber = Float.valueOf(lastBillInfo.getCurrentCounterNumber());
                int intNumber = (int) floatNumber;
                textViewNewNumber.setText(String.valueOf(intNumber));

//                floatNumber = Float.valueOf(lastBillInfo.getPreReadingNumber());TODO
                floatNumber = Float.valueOf(lastBillInfo.getPreCounterNumber());
                intNumber = (int) floatNumber;
                textViewPreNumber.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getPayable());
                intNumber = (int) floatNumber;
                textViewCost.setText(String.valueOf(intNumber));

//                textViewNewDate.setText(lastBillInfo.getCurrentReadingDate());TODO
                textViewNewDate.setText(lastBillInfo.getCurrentCounterReadingDate());
//                textViewPreDate.setText(lastBillInfo.getPreReadingDate());TODO
                textViewPreDate.setText(lastBillInfo.getPreCounterReadingDate());

//                floatNumber = Float.valueOf(lastBillInfo.getRate());TODO
                floatNumber = Float.valueOf(lastBillInfo.getMasrafAverage());
                intNumber = (int) floatNumber;
                textViewUseAverage.setText(String.valueOf(intNumber));

//                textViewUseLength.setText(lastBillInfo.getDuration());TODO
                textViewUseLength.setText(lastBillInfo.getDays());

//                floatNumber = Float.valueOf(lastBillInfo.getUsageM3());TODO
                floatNumber = Float.valueOf(lastBillInfo.getMasraf());
                intNumber = (int) floatNumber;
                textViewUseM3.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getMasrafLiter());
                intNumber = (int) floatNumber;
                textViewUseLitr.setText(String.valueOf(intNumber));

//                floatNumber = Float.valueOf(lastBillInfo.getAbBahaDetail());//TODO
                floatNumber = Float.valueOf(lastBillInfo.getAbBaha());
                intNumber = (int) floatNumber;
                textViewAbBaha.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getLavazemKahande());
                intNumber = (int) floatNumber;
                textViewLavzemKahande.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getTaxfif());
                intNumber = (int) floatNumber;
                textViewTaxfif.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getMaliat());
                intNumber = (int) floatNumber;
                textViewTax.setText(String.valueOf(intNumber));

                textViewDate.setText(lastBillInfo.getDeadLine());

//                floatNumber = Float.valueOf(lastBillInfo.getPreDebtOrOwe());TODO
                floatNumber = Float.valueOf(lastBillInfo.getPreBedOrBes());
                intNumber = (int) floatNumber;
                textViewPreDebtOrOwe.setText(String.valueOf(intNumber));

//                floatNumber = Float.valueOf(lastBillInfo.getBoodje());TODO
                floatNumber = Float.valueOf(lastBillInfo.getBudget());
                intNumber = (int) floatNumber;
                textViewTakalifBoodje.setText(String.valueOf(intNumber));

//                floatNumber = Float.valueOf(lastBillInfo.getKarmozdFazelabDetails());TODO
                floatNumber = Float.valueOf(lastBillInfo.getKarmozdFazelab());
                intNumber = (int) floatNumber;
                textViewKarmozdeFazelab.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getJam());
                intNumber = (int) floatNumber;
                textViewTotal.setText(String.valueOf(intNumber));

                isPayed = lastBillInfo.isPayed();
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare2);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare3Ab);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare3Fazelab);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatAbonmanAb);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatAbonmanFazelab);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatFasleGarm);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatMazadOlgoo);
                linearLayoutCompat.setVisibility(View.GONE);

            } else {
                Log.e("status", "From Cardex, not payed Or New Counter");
                billId = lastBillInfo.getBillId().trim();
                payId = lastBillInfo.getPayId().trim();
                textViewBillId.setText(billId);
                textViewPayId.setText(payId);
                setImageBitmap(imageViewBarcode);
                float floatNumber = Float.valueOf(lastBillInfo.getCurrentReadingNumber());
                int intNumber = (int) floatNumber;
                textViewNewNumber.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getPreReadingNumber());
                intNumber = (int) floatNumber;
                textViewPreNumber.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getPayable());
                intNumber = (int) floatNumber;
                textViewCost.setText(String.valueOf(intNumber));

                textViewNewDate.setText(lastBillInfo.getCurrentReadingDate());
                textViewPreDate.setText(lastBillInfo.getPreReadingDate());

                floatNumber = Float.valueOf(lastBillInfo.getRate());
                intNumber = (int) floatNumber;
                textViewUseAverage.setText(String.valueOf(intNumber));

                textViewUseLength.setText(lastBillInfo.getDuration());

                floatNumber = Float.valueOf(lastBillInfo.getUsageM3());
                intNumber = (int) floatNumber;
                textViewUseM3.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getUsageLiter());
                intNumber = (int) floatNumber;
                textViewUseLitr.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getAbBahaDetail());
                intNumber = (int) floatNumber;
                textViewAbBaha.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getMaliat());
                intNumber = (int) floatNumber;
                textViewTax.setText(String.valueOf(intNumber));

                textViewDate.setText(lastBillInfo.getDeadLine());

                floatNumber = Float.valueOf(lastBillInfo.getPreDebtOrOwe());
                intNumber = (int) floatNumber;
                textViewPreDebtOrOwe.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getBoodje());
                intNumber = (int) floatNumber;
                textViewTakalifBoodje.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getKarmozdFazelabDetails());
                intNumber = (int) floatNumber;

                textViewKarmozdeFazelab.setText(String.valueOf(intNumber));

                floatNumber = Float.valueOf(lastBillInfo.getJam());
                intNumber = (int) floatNumber;
                textViewTotal.setText(String.valueOf(intNumber));

                isPayed = lastBillInfo.isPayed();
                if (!isFromCardex) {
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare2);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare3Ab);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatTabsare3Fazelab);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatAbonmanAb);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatAbonmanFazelab);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatFasleGarm);
                    linearLayoutCompat.setVisibility(View.GONE);
                    linearLayoutCompat = findViewById(R.id.linearLayoutCompatMazadOlgoo);
                    linearLayoutCompat.setVisibility(View.GONE);
                }
                if (isFromCardex) {
                    floatNumber = Float.valueOf(lastBillInfo.getTabsare2());
                    intNumber = (int) floatNumber;
                    textViewTabsare2.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getTabsare3Ab());
                    intNumber = (int) floatNumber;
                    textViewTabsare3Ab.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getTabsare3Fazelab());
                    intNumber = (int) floatNumber;
                    textViewTabsare3Fazelab.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getAbonmanAb());
                    intNumber = (int) floatNumber;
                    textViewAbonmanAb.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getAbonmanFazelab());
                    intNumber = (int) floatNumber;
                    textViewAbonmanFazelab.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getFasleGarm());
                    intNumber = (int) floatNumber;
                    textViewFasleGarm.setText(String.valueOf(intNumber));
                    floatNumber = Float.valueOf(lastBillInfo.getMazadOlgoo());
                    intNumber = (int) floatNumber;
                    textViewMazadOlgoo.setText(String.valueOf(intNumber));
                }
            }
            if (isFromCardex) {
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatIDS);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatPayable1);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatPayable2);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatPay);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatTaxfif);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatKahande);
                linearLayoutCompat.setVisibility(View.GONE);
            }
            if (isPayed) {
                textViewIsPayed.setText(context.getString(R.string.payed_2));
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatIDS);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatPayable1);
                linearLayoutCompat.setVisibility(View.GONE);
                linearLayoutCompat = findViewById(R.id.linearLayoutCompatPayable2);
                linearLayoutCompat.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
