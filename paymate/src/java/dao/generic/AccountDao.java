package dao.generic;

import entities.Account;
import entities.AccountGroup;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 119848
 */

//Generic interface for the AccountDao
public interface AccountDao {

    public List<Account> getAccounts() throws SQLException;

    public Account getAccount(String email) throws Exception;

    public AccountGroup getAccountRole(String email) throws SQLException;

    public void insertAccount(String email, String hashedPassword,
            String defaultRole, String currency, float balance);

    public void updateBalance(Account account, float amount);

}
