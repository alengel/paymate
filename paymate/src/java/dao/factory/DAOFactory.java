package dao.factory;

import dao.generic.AccountDao;
import dao.generic.PaymentDao;
import dao.generic.ScheduledPaymentDao;

/**
 *
 * @author 119848
 */

public abstract class DAOFactory {

    public static final int JPA = 1;

    public abstract AccountDao getAccountDAO();
    public abstract PaymentDao getPaymentDAO();
    public abstract ScheduledPaymentDao getScheduledPaymentDAO();

    //Return correct dao factory
    public static DAOFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
            case JPA:
                return new JpaFactory();
            default:
                return null;
        }
    }

}
