package dao;

import entities.Account;
import entities.ScheduledPayment;
import java.util.Date;
import java.util.List;

/**
 *
 * @author 119848
 */
public interface ScheduledPaymentDao {

    public void insertScheduledPayment(Account origin, Account recipient,
            String currency, float amount, Date nextScheduledDate,
            Date startDate, String frequency);

    public List getScheduledPaymentsByDate(Date date);

    public void remove(long id);

    public List<ScheduledPayment> getRecurringPayments(Account origin);

    public List<ScheduledPayment> getAllRecurringPayments();
}
