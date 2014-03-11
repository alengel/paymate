package ejb.account;

import entity.Account;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface AccountStorageService {
    public Boolean checkAccountExists(String email);
    public void insertAccount(String email, String password, String currency, int balance);
    public Account getAccount(String email, String password);
}
