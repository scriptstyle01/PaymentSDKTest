package ottu.payment.model.fetchTxnDetail;

import java.util.ArrayList;

public class RespoFetchTxnDetail{
    public String amount;
    public ApplePayConfig apple_pay_config;
    public ArrayList<Card> cards;
    public String checkout_url;
    public String currency_code;
    public String customer_id;
    public String expiration_time;
    public String language;
    public String mode;
    public String operation;
    public ArrayList<PaymentMethod> payment_methods;
    public ArrayList<String> pg_codes;
    public String redirect_url;
    public Response response;
    public String session_id;
    public String state;
    public String submit_url;
    public String type;
    public boolean apple_pay_available;
}