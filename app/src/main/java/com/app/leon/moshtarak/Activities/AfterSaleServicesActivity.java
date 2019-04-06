package com.app.leon.moshtarak.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.app.leon.moshtarak.BaseItems.BaseActivity;
import com.app.leon.moshtarak.Infrastructure.IAbfaService;
import com.app.leon.moshtarak.Models.DbTables.RegisterAsDto;
import com.app.leon.moshtarak.Models.DbTables.Service;
import com.app.leon.moshtarak.Models.Enums.DialogType;
import com.app.leon.moshtarak.Models.Enums.ProgressType;
import com.app.leon.moshtarak.Models.InterCommunation.SimpleMessage;
import com.app.leon.moshtarak.Models.ViewModels.UiElementInActivity;
import com.app.leon.moshtarak.R;
import com.app.leon.moshtarak.Utils.CustomDialog;
import com.app.leon.moshtarak.Utils.HttpClientWrapper;
import com.app.leon.moshtarak.Utils.ICallback;
import com.app.leon.moshtarak.Utils.NetworkHelper;
import com.app.leon.moshtarak.Utils.SharedPreference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AfterSaleServicesActivity extends BaseActivity {

    @BindView(R.id.listViewService)
    ListView listViewService;
    @BindView(R.id.buttonSubmit)
    Button buttonSubmit;
    @BindView(R.id.editTextMobile)
    EditText editTextMobile;
    Context context;
    String billId;
    private ArrayList<String> servicesTitle = new ArrayList<>();
    private ArrayList<String> servicesId = new ArrayList<>();
    private ArrayList<String> requestServices = new ArrayList<>();

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.after_sale_services_activity);
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        context = this;
        accessData();
        getServices();
        setOnButtonSubmitClickListener();
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

    void setupListViewReport(ArrayList<Service> services) {
        if (services.size() > 0) {
            for (Service service : services) {
                servicesTitle.add(service.getTitle());
                servicesId.add(service.getId());
//                switch (service.getTitle()) {
//                    case "واگذاری انشعاب فاضلاب":
//                        if (service.getId().equals("2")) {
//                            servicesTitle.add(service.getTitle());
//                            servicesId.add(service.getId());
//                        }
//                        break;
//                    case "آماده سازی آب":
//                        if (service.getId().equals("43")) {
//                            servicesTitle.add(service.getTitle());
//                            servicesId.add(service.getId());
//                        }
//                        break;
//                    case "آماده سازی فاضلاب":
//                        if (service.getId().equals("44")) {
//                            servicesTitle.add(service.getTitle());
//                            servicesId.add(service.getId());
//                        }
//                        break;
//                    default:
//                        servicesTitle.add(service.getTitle());
//                        servicesId.add(service.getId());
//                        break;
//                }
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, servicesTitle) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CheckedTextView textView = view.findViewById(android.R.id.text1);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/BYekan_3.ttf");
                textView.setTypeface(typeface);
                textView.setChecked(true);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        listViewService.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewService.setAdapter(arrayAdapter);
        setListViewServiceClickListener();
    }

    private void setListViewServiceClickListener() {
        listViewService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listViewService.isItemChecked(i)) {
                    requestServices.add(servicesId.get(i));
                } else {
                    requestServices.add(servicesId.remove(i));
                }
            }
        });
    }

    void setOnButtonSubmitClickListener() {
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewFocus;
                if (editTextMobile.getText().length() < 9) {
                    viewFocus = editTextMobile;
                    viewFocus.requestFocus();
                } else if (servicesId.size() < 1) {
                    new CustomDialog(DialogType.Green, context, context.getString(R.string.select), context.getString(R.string.dear_user),
                            context.getString(R.string.support), context.getString(R.string.accepted));
                } else {
                    sendAfterSaleServiceRequest("09".concat(editTextMobile.getText().toString()));
                }
            }
        });
    }

    void sendAfterSaleServiceRequest(String mobileNumber) {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService sendSupportRequest = retrofit.create(IAbfaService.class);
        RegisterAsDto registerAsDto = new RegisterAsDto(billId, requestServices, mobileNumber);
        Call<SimpleMessage> call = sendSupportRequest.registerAS(registerAsDto);
        SendRequest sendRequest = new SendRequest();
        HttpClientWrapper.callHttpAsync(call, sendRequest, context, ProgressType.SHOW.getValue());
    }

    void getServices() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService getServices = retrofit.create(IAbfaService.class);
        Call<ArrayList<Service>> call = getServices.getDictionary();
        GetServices getServices1 = new GetServices();
        HttpClientWrapper.callHttpAsync(call, getServices1, context, ProgressType.SHOW.getValue());
    }

    class GetServices implements ICallback<ArrayList<Service>> {
        @Override
        public void execute(ArrayList<Service> services) {
            setupListViewReport(services);
        }
    }

    class SendRequest implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            new CustomDialog(DialogType.Green, context, simpleMessage.getMessage(), context.getString(R.string.dear_user),
                    context.getString(R.string.support), context.getString(R.string.accepted));
        }
    }
}