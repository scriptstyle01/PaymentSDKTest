package ottu.payment.model.SocketData;

import java.io.Serializable;

public class SocketRespo implements Serializable {
    private String status;

    private String message;

    private String session_id;

    private String order_no;

    private String operation;

    private String reference_number;

    private String redirect_url;

    private String merchant_id;

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setSession_id(String session_id){
        this.session_id = session_id;
    }
    public String getSession_id(){
        return this.session_id;
    }
    public void setOrder_no(String order_no){
        this.order_no = order_no;
    }
    public String getOrder_no(){
        return this.order_no;
    }
    public void setOperation(String operation){
        this.operation = operation;
    }
    public String getOperation(){
        return this.operation;
    }
    public void setReference_number(String reference_number){
        this.reference_number = reference_number;
    }
    public String getReference_number(){
        return this.reference_number;
    }
    public void setRedirect_url(String redirect_url){
        this.redirect_url = redirect_url;
    }
    public String getRedirect_url(){
        return this.redirect_url;
    }
    public void setMerchant_id(String merchant_id){
        this.merchant_id = merchant_id;
    }
    public String getMerchant_id(){
        return this.merchant_id;
    }
}
