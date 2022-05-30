package ottu.payment.model.RedirectUrl;

public class CreateRedirectUrl
{
    private String pg_code;

    private String channel;

    public CreateRedirectUrl(String pg_code, String channel) {
        this.pg_code = pg_code;
        this.channel = channel;
    }

    public void setPg_code(String pg_code){
        this.pg_code = pg_code;
    }
    public String getPg_code(){
        return this.pg_code;
    }
    public void setChannel(String channel){
        this.channel = channel;
    }
    public String getChannel(){
        return this.channel;
    }
}