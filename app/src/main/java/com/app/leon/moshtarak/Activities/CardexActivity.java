package com.app.leon.moshtarak.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.leon.moshtarak.Adapters.CardexCustomAdapter;
import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Infrastructure.ICallback;
import com.app.leon.moshtarak.Models.DbTables.Cardex;
import com.app.leon.moshtarak.Models.Enums.BundleEnum;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.Models.Enums.SharedReferenceKeys;
import com.app.leon.moshtarak.MyApplication;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.HttpClientWrapperNew;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;
import com.app.leon.moshtarak.databinding.CardexContentBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;

public class CardexActivity extends BaseActivity implements ICallback<ArrayList<Cardex>> {
    CardexContentBinding binding;
    CardexCustomAdapter cardexCustomAdapter;
    ArrayList<Integer> yAxisData = new ArrayList<>();
    ArrayList<String> axisValues = new ArrayList<>();
    private Context context;
    private String billId;

    @Override
    protected void initialize() {
        binding = CardexContentBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        @SuppressLint("CutPasteId") ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        context = this;
        accessData();
        setOnLinearLayoutChartClickListener();
    }

    void setOnLinearLayoutChartClickListener() {
        binding.linearLayoutChart.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(BundleEnum.USE.getValue(), yAxisData);
            bundle.putStringArrayList(BundleEnum.DATE.getValue(), axisValues);
            intent.putExtra(BundleEnum.DATA.getValue(), bundle);
            startActivity(intent);
        });
    }

    private void accessData() {
        SharedPreference sharedPreference = new SharedPreference(context);
        if (!sharedPreference.checkIsNotEmpty()) {
            Intent intent = new Intent(getApplicationContext(), SignAccountActivity.class);
            startActivity(intent);
            finish();
        } else {
            billId = sharedPreference.getArrayList(SharedReferenceKeys.BILL_ID.getValue()).
                    get(sharedPreference.getIndex());
            Toast.makeText(MyApplication.getContext(), getString(R.string.active_user).concat(billId), Toast.LENGTH_LONG).show();
            fillCardex();
        }
    }

    void fillCardex() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<ArrayList<Cardex>> call = getKardex.getKardex(billId);
        HttpClientWrapperNew.callHttpAsync(call, CardexActivity.this, context, ProgressType.SHOW.getValue());
    }

    @Override
    public void execute(ArrayList<Cardex> cardexes) {
        cardexCustomAdapter = new CardexCustomAdapter(cardexes, context);
        binding.listViewCardex.setAdapter(cardexCustomAdapter);
        for (int i = 0; i < cardexes.size(); i++) {
            float floatNumber = Float.parseFloat(cardexes.get(i).getUsage());
            yAxisData.add((int) floatNumber);
            axisValues.add(cardexes.get(i).getOweDate());
        }
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.cardex_header, binding.listViewCardex, false);
        binding.listViewCardex.addHeaderView(header, null, false);
    }
}
