package dao.factory;

import dao.generic.AccountDao;
import dao.jpa.JpaAccountDao;
import dao.generic.PaymentDao;
import dao.generic.ScheduledPaymentDao;
import dao.jpa.JpaPaymentDao;
import dao.jpa.JpaScheduledPaymentDao;
import javax.ejb.Stateless;

/**
 *
 * @author 119848
 */
@Stateless
public class JpaFactory extends DAOFactory {

    @Override
    public AccountDao getAccountDAO() {
        return new JpaAccountDao();
    }

    @Override
    public PaymentDao getPaymentDAO() {
        return new JpaPaymentDao();
    }

    @Override
    public ScheduledPaymentDao getScheduledPaymentDAO() {
        return new JpaScheduledPaymentDao();
    }
}
