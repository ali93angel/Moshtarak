package com.app.leon.moshtarak.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.leon.moshtarak.Infrastructure.ICallback;
import com.app.leon.moshtarak.Models.Enums.DialogType;
import com.app.leon.moshtarak.Models.Enums.ErrorHandlerType;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.R;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpClientWrapper {
    private HttpClientWrapper() {
    }


    public static <T> void callHttpAsync(Call<T> call, final ICallback callback, final Context context, int dialogType) {
        callHttpAsync(call, callback, context, dialogType, ErrorHandlerType.ordinary);
    }

    public static <T> void callHttpAsync(Call<T> call, final ICallback callback, final Context context, int dialogType, final ErrorHandlerType errorHandlerType) {
        CustomProgressBar progressBar = new CustomProgressBar();
        if (dialogType == ProgressType.SHOW.getValue() || dialogType == ProgressType.SHOW_CANCELABLE.getValue()) {
            progressBar.show(context, "لطفا صبر کنید...", true);
        }
        if (isOnline(context)) {
            final String[] error = new String[1];
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    try {
                        if (response.isSuccessful()) {
                            T responseT = response.body();
                            callback.execute(responseT);
                            progressBar.getDialog().dismiss();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                error[0] = jsonObject.getString(context.getString(R.string.message));
                            } catch (Exception e) {
                                CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                                error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                            }
                            new CustomDialog(DialogType.Yellow, context, error[0], context.getString(R.string.dear_user),
                                    context.getString(R.string.error), context.getString(R.string.accepted));
                            progressBar.getDialog().dismiss();
                        }
                    } catch (Exception e) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            error[0] = jsonObject.getString(context.getString(R.string.message));
                        } catch (Exception e1) {
                            CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                            error[0] = customErrorHandling.getErrorMessage(response.code(), errorHandlerType);
                        }
                        new CustomDialog(DialogType.Yellow, context, error[0], context.getString(R.string.dear_user),
                                context.getString(R.string.error), context.getString(R.string.accepted));
                        progressBar.getDialog().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    Log.e("error", t.getMessage());
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {
                        CustomErrorHandling customErrorHandling = new CustomErrorHandling(context);
                        error[0] = customErrorHandling.getErrorMessageTotal(t);
                        new CustomDialog(DialogType.Red, context, error[0], context.getString(R.string.dear_user),
                                context.getString(R.string.error), context.getString(R.string.accepted));
                    }
                    progressBar.getDialog().dismiss();
                }
            });
        } else {
            progressBar.getDialog().dismiss();
            Toast.makeText(context, "اینترنت وصل نیست، اینترنت وایفای یا همراه خود را روشن کنید.", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
