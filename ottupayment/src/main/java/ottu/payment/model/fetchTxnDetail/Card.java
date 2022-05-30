package ottu.payment.model.fetchTxnDetail;

public class Card{
    public String customer_id;
    public String brand;
    public String name_on_card;
    public String number;
    public String expiry_month;
    public String expiry_year;
    public String token;
    public boolean preferred;
    public boolean is_expired;
    public String pg_code;
    public String delete_url;
    public String submit_url;
}