package ejb.interfaces;

import entities.Account;
import entities.Payment;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface PaymentStorageService {
    public void makePayment(String type, Account origin, 
            Account recipient, String currency, String amount, Date scheduledDate);
    
    public List<Payment> getNotifications(long originId);
    
    public List<Payment> getAllTransactions();
    
    public void processPayment(long id);
    
    public void updateStatus(long id, String status);
    
    public void updateBalances(Payment payment) throws SQLException;
}

