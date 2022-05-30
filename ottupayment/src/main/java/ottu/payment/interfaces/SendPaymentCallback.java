package ottu.payment.interfaces;

public class SendPaymentCallback {
    OttuPaymentCallback paymentCallback;

    public void setSendPaymentCallback(OttuPaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
    }
    public SendPaymentCallback( ) {
    }

    public void sendSuccess(String massage){
        paymentCallback.onSuccess(massage);
    }
    public void sendFail(String massage){
        paymentCallback.onFail(massage);
    }
    public OttuPaymentCallback getOttuPaymentCallBack(){
        return paymentCallback;
    }
}
