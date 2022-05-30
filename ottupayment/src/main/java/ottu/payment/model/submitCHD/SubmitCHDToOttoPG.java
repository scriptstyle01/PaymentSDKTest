package ottu.payment.model.submitCHD;

public class SubmitCHDToOttoPG
{
    private String merchant_id;

    private String session_id;

    private String payment_method;
    private String token;

    private Card_SubmitCHD card;

    public SubmitCHDToOttoPG(String merchant_id, String session_id, String payment_method, Card_SubmitCHD card) {
        this.merchant_id = merchant_id;
        this.session_id = session_id;
        this.payment_method = payment_method;
        this.card = card;
    }

    public SubmitCHDToOttoPG(String merchant_id, String session_id, String payment_method, String token) {
        this.merchant_id = merchant_id;
        this.session_id = session_id;
        this.payment_method = payment_method;
        this.token = token;
    }

    public void setMerchant_id(String merchant_id){
        this.merchant_id = merchant_id;
    }
    public String getMerchant_id(){
        return this.merchant_id;
    }
    public void setSession_id(String session_id){
        this.session_id = session_id;
    }
    public String getSession_id(){
        return this.session_id;
    }
    public void setPayment_method(String payment_method){
        this.payment_method = payment_method;
    }
    public String getPayment_method(){
        return this.payment_method;
    }
    public void setCard(Card_SubmitCHD card){
        this.card = card;
    }
    public Card_SubmitCHD getCard(){
        return this.card;
    }
}