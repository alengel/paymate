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
    
    public void insertAccount(String email, String password, String currency, float balance);
    
    public Account getAccount(String email);
    
    public void deductAmount(String originEmail, float amount);
    
    public void addAmount(String recipient, float amount);
}
