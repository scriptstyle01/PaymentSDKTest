package ottu.payment.model.GenerateToken;

import java.util.List;

public class CreatePaymentTransaction
{
    private String type;

    private List<String> pg_codes;

    private String amount;

    private String currency_code;

    private String disclosure_url;

    private String redirect_url;

    private String customer_id;

    private String expiration_time;

    public CreatePaymentTransaction(String type, List<String> pg_codes, String amount, String currency_code, String disclosure_url, String redirect_url, String customer_id, String expiration_time) {
        this.type = type;
        this.pg_codes = pg_codes;
        this.amount = amount;
        this.currency_code = currency_code;
        this.disclosure_url = disclosure_url;
        this.redirect_url = redirect_url;
        this.customer_id = customer_id;
        this.expiration_time = expiration_time;
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setPg_codes(List<String> pg_codes){
        this.pg_codes = pg_codes;
    }
    public List<String> getPg_codes(){
        return this.pg_codes;
    }
    public void setAmount(String amount){
        this.amount = amount;
    }
    public String getAmount(){
        return this.amount;
    }
    public void setCurrency_code(String currency_code){
        this.currency_code = currency_code;
    }
    public String getCurrency_code(){
        return this.currency_code;
    }
    public void setDisclosure_url(String disclosure_url){
        this.disclosure_url = disclosure_url;
    }
    public String getDisclosure_url(){
        return this.disclosure_url;
    }
    public void setRedirect_url(String redirect_url){
        this.redirect_url = redirect_url;
    }
    public String getRedirect_url(){
        return this.redirect_url;
    }
    public void setCustomer_id(String customer_id){
        this.customer_id = customer_id;
    }
    public String getCustomer_id(){
        return this.customer_id;
    }
    public void setExpiration_time(String expiration_time){
        this.expiration_time = expiration_time;
    }
    public String getExpiration_time(){
        return this.expiration_time;
    }
}