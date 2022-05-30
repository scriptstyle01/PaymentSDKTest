package ottu.payment.model.RedirectUrl;

public class    RespoRedirectUrl
{
    private String status;

    private String redirect_url;

    private String message;

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setRedirect_url(String redirect_url){
        this.redirect_url = redirect_url;
    }
    public String getRedirect_url(){
        return this.redirect_url;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
