package ejb.payment;

import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface PaymentStorageService {
    public void insertTransaction(String type, String originEmail, 
            String recipient, String currency, String amount, Date scheduledDate);
}

