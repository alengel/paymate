package ejb.interfaces;

import entities.Account;
import entities.AccountGroup;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author 119848
 */

@Remote
public interface AccountStorageService {

    public Boolean checkAccountExists(String email) throws SQLException;

    public Account getAccount(String email) throws SQLException;

    public List<Account> getAccounts() throws SQLException;

    public AccountGroup getAccountRole(String email) throws SQLException;

    public void insertAccount(String email, String password, String currency);

    public void updateLastLoginDate(String email) throws SQLException;
}
