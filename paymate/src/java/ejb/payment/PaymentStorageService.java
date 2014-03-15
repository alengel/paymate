package ejb.payment;

import entity.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface PaymentStorageService {
    public void insertTransaction(String type, String originEmail, 
            String recipient, String currency, String amount, Date scheduledDate);
    
    public List<Payment> getNotifications(String originEmail);
    
    public void processPayment(long id);
    
    public void updateStatus(long id, String status);
}

