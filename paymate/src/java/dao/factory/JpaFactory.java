package dao.factory;

import dao.generic.AccountDao;
import dao.jpa.JpaAccountDao;
import dao.factory.DAOFactory;
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
}
