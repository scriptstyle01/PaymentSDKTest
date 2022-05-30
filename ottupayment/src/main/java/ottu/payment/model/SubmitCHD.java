package ottu.payment.model;

public class SubmitCHD {

    String name;
    String cardNumber;
    String expiryMonth;
    String expiryYear;
    String cvv;
    boolean saveCard;

    public SubmitCHD(String name, String cardNumber, String expiryMonth, String expiryYear, String cvv, boolean saveCard) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.saveCard = saveCard;
    }
}
