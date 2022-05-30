package ottu.payment.model.SocketData;

public class SendToSocket {

    String client_type;
    String reference_number;
    String merchant_id;

    public SendToSocket(String client_type, String reference_number, String merchant_id) {
        this.client_type = client_type;
        this.reference_number = reference_number;
        this.merchant_id = merchant_id;
    }
}
