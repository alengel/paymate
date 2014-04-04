package dao;

import entities.Account;
import entities.AccountGroup;
import entities.Payment;
import entities.ScheduledPayment;
import java.sql.SQLException;
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
    
    public List<ScheduledPayment> getScheduledPayment(long id);
    
    public void updateScheduledDate(long id, Date updatedDate);
    
}
