package ejb.interfaces;

import entities.Account;
import entities.Payment;
import entities.ScheduledPayment;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author 119848
 */

@Remote
public interface PaymentStorageService {

    public void makePayment(String type, Account origin,
            Account recipient, String currency, float amount, Date scheduledDate)
            throws SQLException;

    public void schedulePayment(Account origin, Account recipient,
            String currency, float amount, Date scheduledDate, String frequency)
            throws SQLException;

    public Payment getTransactionById(long id);

    public List<Payment> getTransactions(Account origin);

    public List<Payment> getAllTransactions();

    public List<ScheduledPayment> getRecurringPayments(Account origin);

    public List<ScheduledPayment> getAllRecurringPayments();

    public void updateStatus(long id, String status);

    public void updateBalances(Payment payment) throws SQLException;

    public void makeScheduledPayment(ScheduledPayment item) throws SQLException;

    public void removeScheduledPayment(long id);

    public String[] getAvailableCurrencies();
}
