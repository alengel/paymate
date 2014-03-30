package dao;

import entities.Account;
import entities.AccountGroup;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 119848
 */

public interface AccountDao {
    public List<Account> getAccounts() throws SQLException;
    
    public Account getAccount(String email) throws SQLException;
    
    public AccountGroup getAccountRole(String email) throws SQLException;
    
    public void insertAccount(String email, String hashedPassword, 
            String defaultRole, String currency, float balance);
    
}
