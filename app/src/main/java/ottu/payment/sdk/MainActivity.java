package ottu.payment.sdk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ottu.payment.interfaces.OttuPaymentCallback;
import ottu.payment.interfaces.SendPaymentCallback;
import ottu.payment.model.GenerateToken.CreatePaymentTransaction;
import ottu.payment.model.SocketData.SocketRespo;
import ottu.payment.model.fetchTxnDetail.RespoFetchTxnDetail;
import ottu.payment.sdk.network.GetDataService;
import ottu.payment.ui.OttoPaymentSdk;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ottu.payment.sdk.network.RetrofitClientInstance.getRetrofitInstance;
import static ottu.payment.util.Constant.OttuPaymentResult;


public class MainActivity extends AppCompatActivity implements OttuPaymentCallback {

    private EditText etLocalLan;
    ArrayList<String> listpg = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();
        EditText text = findViewById(R.id.etAmount);
        EditText localLan = findViewById(R.id.localLan);
        RadioButton rbOttupg = findViewById(R.id.rbOttupg);
        RadioButton rbKnet = findViewById(R.id.rbKnet);
        RadioButton rbMpgs = findViewById(R.id.rbMpgs);
        AppCompatButton pay = findViewById(R.id.pay);
        etLocalLan = findViewById(R.id.localLan);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = text.getText().toString().trim();
                String language = localLan.getText().toString().trim();

                if (rbOttupg.isChecked()){
                    listpg.add("ottu_pg_kwd_tkn");
                }
                if (rbKnet.isChecked()){
                    listpg.add("knet-test");
                }
                if (rbMpgs.isChecked()){
                    listpg.add("mpgs");
                }
                if (listpg.size() < 1){
                    listpg.add("ottu_pg_kwd_tkn");
                }

                if (language.equals("en") || language.equals("ar")){
                    createTrx(Float.parseFloat(amount));
                    rbOttupg.setChecked(false);
                    rbKnet.setChecked(false);
                    rbMpgs.setChecked(false);
                }else {
                    Toast.makeText(MainActivity.this, "Enter supported launguage", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void createTrx(float amount) {
//        String[] listpg  = {"ottu_pg_kwd_tkn", "knet-test", "mpgs"};
        CreatePaymentTransaction paymentTransaction = new CreatePaymentTransaction("e_commerce"
                , listpg
                ,String.valueOf(amount)
                ,"KWD"
                ,"https://postapp.knpay.net/disclose_ok/"
                ,"https://postapp.knpay.net/redirected/"
                ,"mani"
                ,"300");
        SendPaymentCallback paymentCallback = new SendPaymentCallback();
        paymentCallback.setSendPaymentCallback(this);


        if (isNetworkAvailable(MainActivity.this)) {
            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait for a moment. Fetching data.");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            GetDataService apiendPoint = getRetrofitInstance();
            Call<RespoFetchTxnDetail> register = apiendPoint.createPaymentTxn(paymentTransaction);
            register.enqueue(new Callback<RespoFetchTxnDetail>() {
                @Override
                public void onResponse(Call<RespoFetchTxnDetail> call, Response<RespoFetchTxnDetail> response) {
                    dialog.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        OttoPaymentSdk ottuPaymentSdk = new OttoPaymentSdk(MainActivity.this);
                        ottuPaymentSdk.setApiId(response.body().session_id);
                        ottuPaymentSdk.setMerchantId("ksa.ottu.dev");
                        ottuPaymentSdk.setSessionId(response.body().session_id);
                        ottuPaymentSdk.setAmount(response.body().amount);
                        ottuPaymentSdk.setLocal(etLocalLan.getText().toString().trim());
                        ottuPaymentSdk.build();



                    }else {
                        Log.e("========",response.errorBody().toString());
//                        Toast.makeText(MainActivity.this, "Please try again!" +response.errorBody().toString(), Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(MainActivity.this, jObjError.getJSONArray("pg_codes").get(0).toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException | IOException e) {


                        }

                    }

                }

                @Override
                public void onFailure(Call<RespoFetchTxnDetail> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }


    @Override
    public void onSuccess(String callback) {

        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(String callback) {
        Toast.makeText(this, "Payment Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ){
            if (requestCode == OttuPaymentResult ){
                SocketRespo paymentResult = (SocketRespo) data.getSerializableExtra("paymentResult");
                Toast.makeText(this, ""+paymentResult.getStatus(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}