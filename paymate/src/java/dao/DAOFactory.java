package dao;

import entities.Account;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 119848
 */

public abstract class DAOFactory {
    
    public static final int JDBC = 1;
    
    public abstract AccountDAO getAccountDAO();
//    public abstract PaymentDAO getPaymentDAO();
    
    public static DAOFactory getDAOFactory(int whichFactory) {
  
    switch (whichFactory) {
      case JDBC: 
          return new JdbcFactory();
      default           : 
          return null;
    }
  }

}
