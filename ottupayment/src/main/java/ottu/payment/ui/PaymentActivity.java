package ottu.payment.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import ottu.payment.R;
import ottu.payment.adapter.PaymentMethodAdapter;
import ottu.payment.adapter.SavedCardAdapter;
import ottu.payment.databinding.ActivityPaymentBinding;
import ottu.payment.model.DeleteCard.SendDeleteCard;
import ottu.payment.model.RedirectUrl.CreateRedirectUrl;
import ottu.payment.model.RedirectUrl.RespoRedirectUrl;
import ottu.payment.model.SocketData.SocketRespo;
import ottu.payment.model.fetchTxnDetail.Card;
import ottu.payment.model.fetchTxnDetail.PaymentMethod;
import ottu.payment.model.fetchTxnDetail.RespoFetchTxnDetail;
import ottu.payment.model.submitCHD.Card_SubmitCHD;
import ottu.payment.model.submitCHD.SubmitCHDToOttoPG;
import ottu.payment.network.GetDataService;
import ottu.payment.network.RetrofitClientInstance;
import ottu.payment.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ottu.payment.network.RetrofitClientInstance.getRetrofitInstance;
import static ottu.payment.network.RetrofitClientInstance.getRetrofitInstancePg;
import static ottu.payment.util.Constant.Amount;
import static ottu.payment.util.Constant.AmountCurrencyCode;
import static ottu.payment.util.Constant.ApiId;
import static ottu.payment.util.Constant.LocalLan;
import static ottu.payment.util.Constant.MerchantId;
import static ottu.payment.util.Constant.OttuPaymentResult;
import static ottu.payment.util.Constant.SessionId;
import static ottu.payment.util.Constant.SubmitUrlCard;
import static ottu.payment.util.Constant.SubmitUrlRedirect;
import static ottu.payment.util.Constant.savedCardSelected;
import static ottu.payment.util.Constant.selectedCardPos;
import static ottu.payment.util.Constant.selectedCardPosision;
import static ottu.payment.util.Constant.sessionId;
import static ottu.payment.util.Util.isDeviceRooted;
import static ottu.payment.util.Util.isNetworkAvailable;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding binding;
    private PaymentMethodAdapter adapterPaymentMethod;
    private SavedCardAdapter adapterSavedCard;
    public ArrayList<PaymentMethod> listPaymentMethods;
    private List<String> pg_codes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        view();
        getTrnDetail();

    }


    private void view() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.gradiunt_blue));

        if (isDeviceRooted()){
            Toast.makeText(this, "Device is rooted", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.rvSavedCards.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPaymentMethod.setLayoutManager(new LinearLayoutManager(this));
        binding.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savedCardSelected){
                    SubmitCHDToOttoPG cardDetail = adapterSavedCard.getCardDetail();
                    payNow(cardDetail);
                }else {
                    if (selectedCardPos == 0) {
                        Card_SubmitCHD submitCHD = adapterPaymentMethod.getCardData();
                        if (submitCHD == null) {
                            Toast.makeText(PaymentActivity.this, getResources().getString(R.string.enter_carddetail), Toast.LENGTH_SHORT).show();
                        } else {
                            if (sessionId.equals("")) {
                                Toast.makeText(PaymentActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                return;
                            }

//                        CreatePaymentTransaction paymentTransaction = adapterPaymentMethod.getPaymentTrn(selectedCardPos);
//                        createTrx(paymentTransaction,paymentTransaction.getPg_codes().get(selectedCardPos));
                            SubmitCHDToOttoPG submitCHDToPG = new SubmitCHDToOttoPG(MerchantId, SessionId, "card", submitCHD);
                            payNow(submitCHDToPG);
                        }
                    } else if (selectedCardPos == 1) {
//                    CreatePaymentTransaction paymentTransaction = adapterPaymentMethod.getPaymentTrn(selectedCardPos);
//                    createTrx(paymentTransaction,paymentTransaction.getPg_codes().get(selectedCardPos));


                        CreateRedirectUrl redirectUrl = new CreateRedirectUrl(pg_codes.get(selectedCardPos), "mobile_sdk");
                        createRedirectUrl(redirectUrl, SessionId);
                    } else if (selectedCardPos == 2) {

                        CreateRedirectUrl redirectUrl = new CreateRedirectUrl(pg_codes.get(selectedCardPos), "mobile_sdk");
                        createRedirectUrl(redirectUrl, SessionId);
                    }
                }
            }
        });


    }

    public void setPayEnable(boolean isenble){
        binding.payNow.setBackground(getResources().getDrawable(R.drawable.payenable));
        binding.payNow.setEnabled(isenble);
        if (isenble){
            binding.payNow.setBackground(getResources().getDrawable(R.drawable.payenable));
            binding.payNow.setTextColor(getResources().getColor(R.color.white));
        }else {
            binding.payNow.setBackground(getResources().getDrawable(R.drawable.paydisable));
            binding.payNow.setTextColor(getResources().getColor(R.color.text_gray2));
        }
    }

    private void payNow(SubmitCHDToOttoPG submitCHDToPG) {
        if (isNetworkAvailable(PaymentActivity.this)) {
            showButtonLoader(true);
            GetDataService apiendPoint = getRetrofitInstancePg();
            Call<ResponseBody> register = apiendPoint.respoSubmitCHD(SubmitUrlCard,submitCHDToPG);
            register.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    showButtonLoader(false);


                    if (response.isSuccessful()) {

                        try {
                            String aa = response.body().string();
                            JSONObject jsonObject = new JSONObject(aa);


                            if (jsonObject.has("status")) {
                                // got success
                                String status = jsonObject.getString("status");
                                if (status.equals("success")){

                                    Toast.makeText(PaymentActivity.this, "Payment Successfull", Toast.LENGTH_SHORT).show();
                                }else if (status.equals("failed")){
                                    Toast.makeText(PaymentActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                                }else if (status.equals("error")){
                                    Toast.makeText(PaymentActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }else if (status.equals("3DS")){
                                    startActivityForResult(new Intent(PaymentActivity.this,WebPaymentActivity.class)
                                            .putExtra("is3DS",true)
                                    .putExtra("html",jsonObject.getString("html"))
                                    .putExtra("reference_number",jsonObject.getString("reference_number"))
                                            .putExtra("ws_url",jsonObject.getString("ws_url")),OttuPaymentResult);

                                }

                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {

                        try {
                            JSONObject errorBody = new JSONObject(response.errorBody().string());
                            JSONObject cardFieldError = null;
                            JSONArray cardGlobleError = null,nonFieldErrors= null,merchantId= null,payment_method = null;
                            if (errorBody.has("card")) {

                                String st = String.valueOf(errorBody.toString().trim().charAt(8));
                                if (st.equals("[") ) {
                                    cardGlobleError = errorBody.getJSONArray("card");
                                }else {
                                    cardFieldError = errorBody.getJSONObject("card");
                                }
                            }

                            if (errorBody.has("non_field_errors")) {
                                 nonFieldErrors = errorBody.getJSONArray("non_field_errors");
                            }
                            if (errorBody.has("merchant_id")) {
                                 merchantId = errorBody.getJSONArray("merchant_id");
                            }
                            if (errorBody.has("payment_method")) {
                                 payment_method = errorBody.getJSONArray("payment_method");
                            }
                            if (cardFieldError != null){
                                JSONArray numberEr = null, dateEr = null, cvvEr = null;
                                JSONArray nameEr = cardFieldError.getJSONArray("name_on_card");
                                if (cardFieldError.has("number")) {
                                    numberEr = cardFieldError.getJSONArray("number");
                                }else if (cardFieldError.has("expiry_year")) {
                                    dateEr = cardFieldError.getJSONArray("expiry_year");
                                }else if (cardFieldError.has("cvv")) {
                                    cvvEr = cardFieldError.getJSONArray("cvv");
                                }

                                if (nameEr != null){
                                    Toast.makeText(PaymentActivity.this, ""+nameEr.get(0), Toast.LENGTH_SHORT).show();
                                }else if (numberEr != null){
                                    Toast.makeText(PaymentActivity.this, ""+numberEr.get(0), Toast.LENGTH_SHORT).show();
                                }else if (dateEr != null){
                                    Toast.makeText(PaymentActivity.this, ""+dateEr.get(0), Toast.LENGTH_SHORT).show();
                                }else if (cvvEr != null){
                                    Toast.makeText(PaymentActivity.this, ""+cvvEr.get(0), Toast.LENGTH_SHORT).show();
                                }

                            }else if (cardGlobleError != null){
                                Toast.makeText(PaymentActivity.this, ""+cardGlobleError.get(0), Toast.LENGTH_SHORT).show();
                            }else if (nonFieldErrors != null){
                                Toast.makeText(PaymentActivity.this, nonFieldErrors.getString(0), Toast.LENGTH_SHORT).show();
                            }else if (merchantId != null){
                                Toast.makeText(PaymentActivity.this, merchantId.getString(0), Toast.LENGTH_SHORT).show();
                            }else if (payment_method != null){
                                Toast.makeText(PaymentActivity.this, payment_method.getString(0), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {

                            Log.i("JSONException ",e.getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showButtonLoader(false);
                    Toast.makeText(PaymentActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getTrnDetail() {
        String apiId = null;
        String amount = null;
        if (getIntent().hasExtra("SessionId")) {
             apiId = getIntent().getStringExtra("ApiId");
            MerchantId = getIntent().getStringExtra("MerchantId");
            LocalLan = getIntent().getStringExtra("LocalLan");
            setLocal(LocalLan);
             ApiId = apiId;

        }else {
            Toast.makeText(this, "No sessionid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        if (isNetworkAvailable(PaymentActivity.this)) {

            GetDataService apiendPoint = new RetrofitClientInstance().getRetrofitInstance();
            Call<RespoFetchTxnDetail> register = apiendPoint.fetchTxnDetail(apiId,true);
            register.enqueue(new Callback<RespoFetchTxnDetail>() {
                @Override
                public void onResponse(Call<RespoFetchTxnDetail> call, Response<RespoFetchTxnDetail> response) {
//                    dialog.dismiss();


                    if (response.isSuccessful() && response.body() != null) {
                        showData(response.body());
                        sessionId = response.body().session_id;
                        pg_codes = response.body().pg_codes;
                        SubmitUrlCard = response.body().payment_methods.get(0).submit_url;
                        SubmitUrlRedirect = response.body().submit_url;
                        listPaymentMethods = response.body().payment_methods;
                    }else {
//                        Toast.makeText(PaymentActivity.this,, "Please try again!" , Toast.LENGTH_SHORT).show();
                        SocketRespo finalResponse = new SocketRespo();
                        finalResponse.setStatus("failed");
                        finalResponse.setSession_id("");
                        finalResponse.setOrder_no("");
                        finalResponse.setOperation("");
                        finalResponse.setReference_number("");
                        finalResponse.setRedirect_url("");
                        finalResponse.setMerchant_id(MerchantId);
                        Intent intent = new Intent();
                        intent.putExtra("paymentResult",finalResponse);
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<RespoFetchTxnDetail> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    public void showData(RespoFetchTxnDetail body) {
        if (body != null){

            binding.layoutContaint.setVisibility(View.VISIBLE);
            binding.shimmerViewContainer.stopShimmerAnimation();
            binding.shimmerViewContainer.setVisibility(View.GONE);

            Amount = body.amount;
            SessionId = body.session_id;
            AmountCurrencyCode = body.currency_code;
            binding.finalAmountTxt.setText(Amount);
            binding.finalAmountTitle.setText(getResources().getString(R.string.total_bill));
            binding.titleSavedCard.setText(getResources().getString(R.string.saved_card));
            binding.subTitleSavedCard.setText(getResources().getString(R.string.list_card_saved));
            binding.txtpaymentMethod.setText(getResources().getString(R.string.payment_method));
            binding.txtpaymentMethodsub.setText(getResources().getString(R.string.list_gatway));

            binding.payNow.setText(Html.fromHtml("<b>" + getResources().getString(R.string.paynow) + "</b>"  ));
            binding.finalAmountCurrencyCode.setText(body.currency_code);
            if (body.cards != null) {
                if (body.cards.size() < 1){
                    binding.layoutSavedListTitle.setVisibility(View.GONE);
                }
                adapterSavedCard = new SavedCardAdapter(PaymentActivity.this,body.cards );
                binding.rvSavedCards.setAdapter(adapterSavedCard);
            }else {
                binding.layoutSavedListTitle.setVisibility(View.GONE);
            }
            if (body.payment_methods != null) {
               adapterPaymentMethod =  new PaymentMethodAdapter(this,body );
                binding.rvPaymentMethod.setAdapter(adapterPaymentMethod);
            }

        }
    }

    private void createRedirectUrl(CreateRedirectUrl redirectUrl, String session_id) {

        if (isNetworkAvailable(PaymentActivity.this)) {
            showButtonLoader(true);
            GetDataService apiendPoint = getRetrofitInstance();
            Call<RespoRedirectUrl> register = apiendPoint.createRedirectUrl(SubmitUrlRedirect,redirectUrl);
            register.enqueue(new Callback<RespoRedirectUrl>() {
                @Override
                public void onResponse(Call<RespoRedirectUrl> call, Response<RespoRedirectUrl> response) {

                    showButtonLoader(false);

                    if (response.isSuccessful() && response.body() != null) {

                        if (response.body().getRedirect_url() != null){
                            startActivityForResult(new Intent(PaymentActivity.this,WebPaymentActivity.class)
                            .putExtra("RedirectUrl",response.body().getRedirect_url()),OttuPaymentResult);

                        }else {
                            Toast.makeText(PaymentActivity.this, response.body().getMessage() , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("paymentResult","Payment Fail");
                            setResult(OttuPaymentResult,intent);
                            finish();
                        }
                    }else {
                        Toast.makeText(PaymentActivity.this, "Please try again!" , Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<RespoRedirectUrl> call, Throwable t) {
                    showButtonLoader(false);
                    Toast.makeText(PaymentActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    public void notifySavedCardAdapter(){
        if (adapterSavedCard != null){
            selectedCardPosision = -1;
            adapterSavedCard.notifyDataSetChanged();
        }
    }
    public void notifyPaymentMethodAdapter(){

        if (adapterPaymentMethod != null){
//            binding.rvPaymentMethod.setAdapter(adapterPaymentMethod);
            selectedCardPos = -1;
            adapterPaymentMethod.notifyDataSetChanged();
        }
    }

    public void manageKeyboard(InputConnection ic, int visible){
        if (visible == View.VISIBLE) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
        binding.keyboard.setInputConnection(ic);
        binding.keyboard.setVisibility(visible);
    }
    public void deleteCard(SendDeleteCard deleteCard, String token, int position, ArrayList<Card> listCards) {

        if (isNetworkAvailable(PaymentActivity.this)) {
            showLoader(true);
            GetDataService apiendPoint = getRetrofitInstance();
            Call<ResponseBody> register = apiendPoint.deleteCard1(token);
            register.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    showLoader(false);

                    if (response.isSuccessful()) {
                        Toast.makeText(PaymentActivity.this, "Card Deleted", Toast.LENGTH_SHORT).show();
                        listCards.remove(position);
                        adapterSavedCard = new SavedCardAdapter(PaymentActivity.this,listCards );
                        binding.rvSavedCards.setAdapter(adapterSavedCard);
                        setFee(false,"","","");
                        setPayEnable(false);
                    }else {
                        Toast.makeText(PaymentActivity.this, "Card not deleted", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showLoader(false);
                }
            });
        }

    }

    public void showLoader(boolean visibility){
//        Glide.with(this).load(R.raw.loader).into(binding.loader);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                Drawable drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(getResources(), R.drawable.loader));
                binding.loader.setImageDrawable(drawable);

                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (visibility) {
            binding.progressLayout.setVisibility(View.VISIBLE);
        }else {
            binding.progressLayout.setVisibility(View.GONE);
        }
    }

    public void showButtonLoader(boolean visibility){
        if (visibility) {
            binding.btnProgress.setVisibility(View.VISIBLE);
            binding.payNow.setText("");
        }else {
            binding.btnProgress.setVisibility(View.GONE);
            binding.payNow.setText(Html.fromHtml("<b>" + getResources().getString(R.string.paynow) + "</b>"  ));
        }
    }
    public void setLocal(String local1){
        Locale locale = new Locale(local1);
        Locale.setDefault(locale);
        Configuration conf = getResources().getConfiguration();
        conf.setLocale(locale);
        conf.setLayoutDirection(locale);
        createConfigurationContext(conf);

        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());




    }

    public void setFee(boolean visibility,String amount,String amountCurrency,String feeAmount){
        if (visibility){
            binding.layoutFeeAmount.setVisibility(View.VISIBLE);
            binding.amountTxt.setText(Amount);
            binding.amountCurrencyCode.setText(AmountCurrencyCode);
            binding.feeTxt.setText(feeAmount);
            binding.feecurrencyCode.setText(amountCurrency);
            binding.finalAmountTxt.setText(amount);
            binding.finalAmountCurrencyCode.setText(amountCurrency);

            Amount = amount;
            AmountCurrencyCode = amountCurrency;

        }else {
            binding.layoutFeeAmount.setVisibility(View.GONE);
            binding.finalAmountTxt.setText(Amount);
            binding.finalAmountCurrencyCode.setText(AmountCurrencyCode);
        }

    }
    public void setSavedCardFee( String pg_code) {

        for (int i = 0; i < listPaymentMethods.size(); i++) {
            if (listPaymentMethods.get(i).code.equals(pg_code)){
                setFee(true,listPaymentMethods.get(i).amount,listPaymentMethods.get(i).currency_code,listPaymentMethods.get(i).fee);
            }
        }

    }
    @Override
    public void onBackPressed() {

        if (binding.keyboard.getVisibility() == View.VISIBLE){
            binding.keyboard.setVisibility(View.GONE);
            return;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedCardPos = -1;
        selectedCardPosision = -1;
        notifySavedCardAdapter();
        notifyPaymentMethodAdapter();
        binding.shimmerViewContainer.startShimmerAnimation();
    }
    @Override
    protected void onPause() {
        binding.shimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ){
            if (requestCode == OttuPaymentResult ){
               setResult(RESULT_OK,data);
               finish();
            }

        }
    }


}