package ejb.interfaces;

import entities.Account;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author 119848
 */

@Remote
public interface AccountStorageService {
    public Boolean checkAccountExists(String email);
    
    public void updateLastLoginDate(String email);
    
    public void insertAccount(String email, String password, String currency);
    
    public Account getAccount(String email);
    
    public Account getAccountRole(String email);
    
    public void deductAmount(String originEmail, float amount);
    
    public void addAmount(String recipient, float amount);
        
    // Admin User Calls    
    public List<Account> getAccounts();
}
