package dao;

/**
 *
 * @author 119848
 */
public abstract class DAOFactory {

    public static final int JDBC = 1;

    public abstract AccountDao getAccountDAO();
//    public abstract PaymentDAO getPaymentDAO();

    public static DAOFactory getDAOFactory(int whichFactory) {

        switch (whichFactory) {
            case JDBC:
                return new JpaFactory();
            default:
                return null;
        }
    }

}
