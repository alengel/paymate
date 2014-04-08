package dao;

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
