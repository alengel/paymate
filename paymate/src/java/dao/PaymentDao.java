package dao;

import entities.Account;
import entities.Payment;
import java.util.Date;
import java.util.List;

/**
 *
 * @author 119848
 */
public interface PaymentDao {

    public void insertTransaction(Date timestamp, String type,
            Account origin, Account recipient, String currency,
            float amount, Date scheduledDate, String status);

    public List<Payment> getTransactionsByAccount(Account origin);

    public List<Payment> getAllTransactions();

    public Payment getTransaction(long id);

    public void updateStatus(long id, String status);
}
