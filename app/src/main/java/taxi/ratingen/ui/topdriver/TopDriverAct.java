package taxi.ratingen.ui.topdriver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import taxi.ratingen.R;
import taxi.ratingen.retro.base.BaseResponse;
import taxi.ratingen.retro.GitHubService;
import taxi.ratingen.retro.responsemodel.TranslationModel;
import taxi.ratingen.ui.drawerscreen.DrawerAct;
import taxi.ratingen.utilz.CommonUtils;
import taxi.ratingen.utilz.Constants;
import taxi.ratingen.utilz.SharedPrefence;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopDriverAct extends AppCompatActivity {
    RecyclerView DriverRecycle;
    TopDriverAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    HashMap<String, String> map;
    HashMap<String, String> hashmap;
    TextView no_item_found;
    List<TopDriverModel> driverList = new ArrayList<>();
    String reqId = "";
    String id = "";
    String token = "", typeId = "", lati = "", longi = "";
    Button skip;
    RelativeLayout progressBar;
    TextView add_charges, topdriver_msg;
    public TranslationModel translationModel;
    SharedPrefence sharedPrefence;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_driver);
        DriverRecycle = findViewById(R.id.recylerview);
        no_item_found = findViewById(R.id.no_item_found);
        topdriver_msg = findViewById(R.id.want_toprated_driver);
        confirm = findViewById(R.id.confirm);
        skip = findViewById(R.id.skip);
        progressBar = findViewById(R.id.progressBar_driver);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        add_charges = findViewById(R.id.add_charges_applicable);

        sharedPrefence = new SharedPrefence(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit());


        if (!CommonUtils.IsEmpty(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE))) {
            translationModel = new Gson().fromJson(sharedPrefence.Getvalue(sharedPrefence.Getvalue(SharedPrefence.LANGUANGE)), TranslationModel.class);
            Log.e("valuess==", "valuess=" + translationModel.add_charges);
        }

        skip.setText(translationModel.txt_skip);
        confirm.setText(translationModel.text_confirm);


        //  progressBar.setVisibility(View.GONE);
        map = new HashMap<>();
        hashmap = new HashMap<>();


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(translationModel.txt_captains);
        }

        reqId = getIntent().getStringExtra("req_id");
        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");
        typeId = getIntent().getStringExtra("type_id");
        lati = getIntent().getStringExtra("lati");
        longi = getIntent().getStringExtra("longi");

        map.put(Constants.NetworkParameters.vehicle_type, typeId);
        map.put(Constants.NetworkParameters.PICK_LAT, lati);
        map.put(Constants.NetworkParameters.PICK_LNG, longi);
        map.put(Constants.NetworkParameters.request_id, reqId);

        skip.setOnClickListener(v -> openConfirmedAlert(translationModel.txt_req_sent));
        confirm.setOnClickListener(v -> confirmClicked());

        /*if (getIntent() != null) {
            linearLayoutManager = new LinearLayoutManager(this);
            DriverRecycle.setLayoutManager(linearLayoutManager);
            adapter = new TopDriverAdapter(TopDriverAct.this, driverList);
            DriverRecycle.setAdapter(adapter);
        }*/


    }

    /**
     * Triggers this receiver from push to handle the scehdule push rejects.
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("message")) {
                String msg = intent.getExtras().getString("message");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TopDriverAct.this, msg, Toast.LENGTH_SHORT).show();

                if (msg != null && msg.length() > 0 && msg.equalsIgnoreCase("Ride Has been scheduled"))
                    onBackPressed();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.pushRejected));

        TopDriverList(map);
    }

    /**
     * @param map is a parameter of Geting top20 DriverList Api.
     */
    public void TopDriverList(HashMap<String, String> map) {
        Retrofit retrofit = null;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.URL.BaseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<BaseResponse> call = service.getTopDriverList(map);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().successMessage != null && response.body().successMessage.equalsIgnoreCase("top_rated_driver_list")) {
                    if (response.body().getDrivers() != null && response.body().getDrivers().size() > 0) {
                        linearLayoutManager = new LinearLayoutManager(TopDriverAct.this);
                        DriverRecycle.setLayoutManager(linearLayoutManager);
                        adapter = new TopDriverAdapter(TopDriverAct.this, response.body().getDrivers());
                        DriverRecycle.setAdapter(adapter);

                        add_charges.setVisibility((response.body().DriverAddCharges <= 0) ? View.GONE : View.VISIBLE);
                        add_charges.setText(translationModel.add_charges + " " + response.body().currency + " " + CommonUtils.doubleDecimalFromat(Double.valueOf(response.body().DriverAddCharges)) + " " + translationModel.applicable);
                        topdriver_msg.setText(translationModel.do_you_want_top_driver);
                    } else {
                        add_charges.setVisibility(View.GONE);
                        no_item_found.setVisibility(View.VISIBLE);
                        DriverRecycle.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(TopDriverAct.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Api call to confirming the schedule trip for top20 drivers.
     */
    public void confirmClicked() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = null;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.URL.BaseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hashmap.put(Constants.NetworkParameters.client_id, sharedPrefence.getCompanyID());
        hashmap.put(Constants.NetworkParameters.client_token, sharedPrefence.getCompanyToken());
        hashmap.put(Constants.NetworkParameters.id, this.id);
        hashmap.put(Constants.NetworkParameters.token, token);
        hashmap.put(Constants.NetworkParameters.request_id, reqId);
        //   hashmap.put(Constants.NetworkParameters.driver_id, "" + id);

        GitHubService service = retrofit.create(GitHubService.class);
        //   Call<BaseResponse> call = service.driverSingle(hashmap);
        Call<BaseResponse> call = service.topratedConfirm(hashmap);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().successMessage.equalsIgnoreCase("top_twenty_drivers_confirmed")) {
                    //  redirection();
                    openConfirmedAlert(translationModel.top_twenty_drivers_confirmed);
                }
            }


            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TopDriverAct.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        openConfirmedAlert(translationModel.txt_req_sent);
        return super.onSupportNavigateUp();
    }


    /**
     * @param message defining the ride later schedule alert message.
     */
    private void openConfirmedAlert(String message) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(TopDriverAct.this).inflate(R.layout.ride_latrer_alert, viewGroup, false);
        TextView content = dialogView.findViewById(R.id.alert_content);
        Button okButt = dialogView.findViewById(R.id.submit);
        okButt.setText(translationModel.text_ok);
        content.setText(message);
        AlertDialog.Builder builder = new AlertDialog.Builder(TopDriverAct.this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        okButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(TopDriverAct.this, DrawerAct.class));
            }
        });
        alertDialog.show();

    }
}
