package dao;

import entities.Account;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 119848
 */

public interface AccountDAO {
    public List<Account> getAccounts() throws SQLException;
}
