package ottu.payment.interfaces;

public interface OttuPaymentCallback {

    public void onSuccess(String callback);
    public void onFail(String callback);

}
