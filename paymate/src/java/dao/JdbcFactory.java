package dao;

import javax.ejb.Stateless;

/**
 *
 * @author 119848
 */

@Stateless
public class JdbcFactory extends DAOFactory {

    @Override
    public AccountDao getAccountDAO() {
        return new JdbcAccountDao();
    }
}
