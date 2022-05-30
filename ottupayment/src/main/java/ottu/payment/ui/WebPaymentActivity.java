package ottu.payment.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;


import ottu.payment.R;
import ottu.payment.databinding.ActivityWebPaymentBinding;
import ottu.payment.databinding.DialogResultBinding;
import ottu.payment.model.SocketData.SendToSocket;
import ottu.payment.model.SocketData.SocketRespo;
import ottu.payment.model.fetchTxnDetail.RespoFetchTxnDetail;
import ottu.payment.network.GetDataService;
import ottu.payment.network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ottu.payment.util.Constant.Amount;
import static ottu.payment.util.Constant.AmountCurrencyCode;
import static ottu.payment.util.Constant.ApiId;
import static ottu.payment.util.Constant.MerchantId;
import static ottu.payment.util.Constant.OttuPaymentResult;
import static ottu.payment.util.Util.isNetworkAvailable;

public class WebPaymentActivity extends AppCompatActivity {

    private ActivityWebPaymentBinding binding;
    private WebSocketClient mWebSocketClient;
    private String referenceNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewOverrideUrl());
        webView.setWebChromeClient(new MyWebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        String url = null;
        if (getIntent().hasExtra("RedirectUrl")) {
            url = getIntent().getStringExtra("RedirectUrl");
            webView.loadUrl(url);
        } else if (getIntent().hasExtra("is3DS")) {
            if (getIntent().hasExtra("is3DS")) {
                url = getIntent().getStringExtra("html");
                webView.loadData(url, "text/html; charset=utf-8", "UTF-8");
                String socketUrl = getIntent().getStringExtra("ws_url");
                connectWebSocket(socketUrl);


            }
        }


    }

    private class WebViewOverrideUrl extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            proceedUrl(view, Uri.parse(url));
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            proceedUrl(view, request.getUrl());
            if (request.getUrl().toString().contains("mobile-sdk-redirect")) {


                getTrnDetail();
            }
            return true;
        }

        private void proceedUrl(WebView view, Uri uri) {
            try {
                view.loadUrl(uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress >= 100) {

            }
            super.onProgressChanged(view, newProgress);
        }
    }


    private void getTrnDetail() {


        if (isNetworkAvailable(WebPaymentActivity.this)) {
            showLoader(true);
            GetDataService apiendPoint = new RetrofitClientInstance().getRetrofitInstance();
            Call<RespoFetchTxnDetail> register = apiendPoint.fetchTxnDetail(ApiId, false);
            register.enqueue(new Callback<RespoFetchTxnDetail>() {
                @Override
                public void onResponse(Call<RespoFetchTxnDetail> call, Response<RespoFetchTxnDetail> response) {
                    showLoader(false);

                    if (response.isSuccessful() && response.body() != null) {
                        SocketRespo finalResponse = new SocketRespo();
                        finalResponse.setStatus(response.body().response.status);
                        finalResponse.setSession_id(response.body().session_id);
                        finalResponse.setOrder_no(String.valueOf(response.body().response.order_no));
                        finalResponse.setOperation(response.body().operation);
                        finalResponse.setReference_number(response.body().response.reference_number);
                        finalResponse.setRedirect_url(response.body().redirect_url);
                        finalResponse.setMessage(response.body().response.message);
                        finalResponse.setMerchant_id(MerchantId);

                        String state = response.body().response.status;
                        if (state.equals("success")) {
//                            showSuccessDialog(finalResponse);
                            sendResult(finalResponse);

                        } else if(state.equals("error")){
                            sendResult(finalResponse);
                        } else {
//                            showFailDialog(finalResponse);
                            sendResult(finalResponse);
                        }

                    } else {
                        Toast.makeText(WebPaymentActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<RespoFetchTxnDetail> call, Throwable t) {
                    showLoader(false);
                    Toast.makeText(WebPaymentActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    public void showSuccessDialog(SocketRespo finalResponse){
        DialogResultBinding dialogBinding = DialogResultBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.detailText.setText(getResources().getString(R.string.transaction_successfull));
        dialogBinding.resultText.setText(getResources().getString(R.string.thankyou));
        dialogBinding.resultText.setTextColor(getResources().getColor(R.color.green));
        dialogBinding.paymentAmount.setVisibility(View.VISIBLE);
        dialogBinding.paymentAmount.setText(Amount+" "+AmountCurrencyCode +" "+ getResources().getString(R.string.is_paid));
        dialogBinding.resultImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_success));

        dialogBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("paymentResult",finalResponse);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("paymentResult",finalResponse);
                setResult(RESULT_OK,intent);
                finish();
            }
        }, 5000);

        dialog.show();
    }

    public void showFailDialog(SocketRespo finalResponse){
        DialogResultBinding dialogBinding = DialogResultBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.detailText.setText(getResources().getString(R.string.transaction_fail));
        dialogBinding.resultText.setTextColor(getResources().getColor(R.color.red));
        dialogBinding.resultText.setText(getResources().getString(R.string.payment_fail));
        dialogBinding.paymentAmount.setVisibility(View.GONE);
        dialogBinding.resultImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_fail));

        dialogBinding.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("paymentResult",finalResponse);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("paymentResult",finalResponse);
                setResult(RESULT_OK,intent);
                finish();
            }
        }, 5000);

        dialog.show();
    }

    public void sendResult(SocketRespo finalResponse){
        Intent intent = new Intent();
        intent.putExtra("paymentResult",finalResponse);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void connectWebSocket(String socketUrl) {
        URI uri;
        try {
            uri = new URI(socketUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                String jsonString = new com.google.gson.Gson().toJson(
                        new SendToSocket("sdk"
                                , getIntent().getStringExtra("reference_number")
                                , MerchantId));
                mWebSocketClient.send(jsonString);

            }

            @Override
            public void onMessage(String s) {
                SocketRespo response = null;

                response = new Gson().fromJson(s, SocketRespo.class);


                SocketRespo finalResponse = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalResponse != null) {

                            String state = finalResponse.getStatus();
                             if (state.equals("success")) {
//                                showSuccessDialog(finalResponse);

                                 sendResult(finalResponse);
                            } else {
//                                showFailDialog(finalResponse);
                                 sendResult(finalResponse);
                            }

//                            referenceNo = finalResponse.getReference_number();
//                            getTrnDetail();

                        } else {
                            SocketRespo finalResponse = new SocketRespo();
                            finalResponse.setStatus("fail");
                            finalResponse.setSession_id("");
                            finalResponse.setOrder_no("");
                            finalResponse.setOperation("");
                            finalResponse.setReference_number("");
                            finalResponse.setRedirect_url("");
                            finalResponse.setMerchant_id(MerchantId);
                            sendResult(finalResponse);

                        }

                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
            }

            @Override
            public void onError(Exception e) {
            }
        };
        mWebSocketClient.connect();
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


    @Override
    public void onBackPressed() {
        finish();
    }
}