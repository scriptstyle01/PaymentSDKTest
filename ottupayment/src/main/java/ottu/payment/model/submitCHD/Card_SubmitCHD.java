package ottu.payment.model.submitCHD;

public class Card_SubmitCHD
{
    private String name_on_card;

    private String number;

    private String expiry_year;

    private String expiry_month;

    private String cvv;

    private boolean save_card;

    public Card_SubmitCHD(String name_on_card, String number, String expiry_month, String  expiry_year, String cvv, boolean save_card) {
        this.name_on_card = name_on_card;
        this.number = number;
        this.expiry_year = expiry_year;
        this.expiry_month = expiry_month;
        this.cvv = cvv;
        this.save_card = save_card;
    }

    public void setName_on_card(String name_on_card){
        this.name_on_card = name_on_card;
    }
    public String getName_on_card(){
        return this.name_on_card;
    }
    public void setNumber(String number){
        this.number = number;
    }
    public String getNumber(){
        return this.number;
    }
    public void setExpiry_year(String expiry_year){
        this.expiry_year = expiry_year;
    }
    public String getExpiry_year(){
        return this.expiry_year;
    }
    public void setExpiry_month(String expiry_month){
        this.expiry_month = expiry_month;
    }
    public String getExpiry_month(){
        return this.expiry_month;
    }
    public void setCvv(String cvv){
        this.cvv = cvv;
    }
    public String getCvv(){
        return this.cvv;
    }
    public void setSave_card(boolean save_card){
        this.save_card = save_card;
    }
    public boolean getSave_card(){
        return this.save_card;
    }
}