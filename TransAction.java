import java.io.Serializable;
import java.util.Date;

public class TransAction implements Serializable {
    String amount;
    String type;
    Date date;
    String billingID;
    String paymentCode;


    public TransAction(String amount,String type){
        this.amount=amount;
        this.type=type;
        date=new Date();
    }
    public TransAction(String amount,String type,String billingID,String paymentCode){
        this.amount=amount;
        this.type=type;
        date=new Date();
        this.billingID=billingID;
        this.paymentCode=paymentCode;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getBillingID() {
        return billingID;
    }

    public String getPaymentCode() {
        return paymentCode;
    }
}
