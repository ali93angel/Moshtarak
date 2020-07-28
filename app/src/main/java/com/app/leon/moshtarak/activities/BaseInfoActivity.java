package com.app.leon.moshtarak.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Infrastructure.ICallback;
import com.app.leon.moshtarak.Infrastructure.ICallbackError;
import com.app.leon.moshtarak.Infrastructure.ICallbackIncomplete;
import com.app.leon.moshtarak.Models.DbTables.MemberInfo;
import com.app.leon.moshtarak.Models.Enums.DialogType;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.Models.Enums.SharedReferenceKeys;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.CustomDialog;
import com.app.leon.moshtarak.Utils.CustomErrorHandlingNew;
import com.app.leon.moshtarak.Utils.HttpClientWrapper;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;
import com.app.leon.moshtarak.databinding.BaseInfoContentBinding;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BaseInfoActivity extends BaseActivity {
    Context context;
    String billId;
    BaseInfoContentBinding binding;

    @SuppressLint("CutPasteId")
    @Override
    protected void initialize() {
        binding = BaseInfoContentBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        context = this;
        accessData();
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
            fillInfo();
        }
    }

    private void fillInfo() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService service = retrofit.create(IAbfaService.class);
        Call<MemberInfo> call = service.getInfo(billId);
        GetInfo getInfo = new GetInfo();
        GetInfoIncomplete incomplete = new GetInfoIncomplete();
        GetError getError = new GetError();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                getInfo, incomplete, getError);

    }

    class GetInfo implements ICallback<MemberInfo> {
        @Override
        public void execute(MemberInfo memberInfo) {
            binding.textViewId.setText(memberInfo.getBillId());
            binding.textViewFile.setText(memberInfo.getRadif().substring(0,
                    memberInfo.getRadif().indexOf(".")));
            binding.textViewAccount.setText(memberInfo.getEshterak());
            int ahad = Integer.parseInt(memberInfo.getDomesticUnit()) +
                    Integer.parseInt(memberInfo.getNonDomesticUnit());
            binding.textViewAhad.setText(String.valueOf(ahad));
            binding.textViewBranchRadius.setText(memberInfo.getQotr());
            binding.textViewCapacity.setText(memberInfo.getCapacity());
            binding.textViewDebt.setText(memberInfo.getMande());
            binding.textViewSiphonRadius.setText(memberInfo.getSiphon());
            binding.textViewUser.setText(memberInfo.getKarbari());
            binding.textViewName.setText(memberInfo.getFirstName().trim().concat(" ").
                    concat(memberInfo.getSureName().trim()));
        }
    }

    class GetInfoIncomplete implements ICallbackIncomplete<MemberInfo> {
        @Override
        public void executeIncomplete(Response<MemberInfo> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, context, error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.login),
                    context.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.Yellow, context, error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.login),
                    context.getString(R.string.accepted));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}
