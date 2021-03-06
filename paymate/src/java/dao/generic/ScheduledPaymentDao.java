package dao.generic;

import entities.Account;
import entities.ScheduledPayment;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */
//Generic interface for the ScheduledPaymentDao
@Local
public interface ScheduledPaymentDao {

    public void insertScheduledPayment(Account origin, Account recipient,
            String currency, float amount, Date nextScheduledDate,
            Date startDate, String frequency);

    public List getScheduledPaymentsByDate(Date date);

    public void remove(long id);

    public List<ScheduledPayment> getRecurringPayments(Account origin);

    public List<ScheduledPayment> getAllRecurringPayments();
}
