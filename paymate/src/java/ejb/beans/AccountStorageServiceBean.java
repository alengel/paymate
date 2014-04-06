package ejb.beans;

import dao.JpaAccountDao;
import entities.Account;
import entities.AccountGroup;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import jsf.shared.beans.UtilityBean;

/**
 *
 * @author 119848
 */

@Stateless
public class AccountStorageServiceBean {
    
    private final UtilityBean utility;
    
    @EJB
    private JpaAccountDao accountDao;
    
    public AccountStorageServiceBean() {
        utility = new UtilityBean();  
    }
    
    public synchronized Boolean checkAccountExists(String email) throws SQLException{    
        return accountDao.getAccount(email) != null;
    }   

    public synchronized Account getAccount(String email) throws SQLException {
        return accountDao.getAccount(email);
    }
    
    public synchronized List<Account> getAccounts() throws SQLException{
        return accountDao.getAccounts();
    }
    
    public synchronized AccountGroup getAccountRole(String email) throws SQLException {
        return accountDao.getAccountRole(email);
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void insertAccount(String email, String password, String currency) {
        
        float balance;
        String defaultRole;
        String hashedPassword = hashPassword(password);
        
        if(utility.getLoggedInUser() != null){
            //Admin user defaults
            defaultRole = "admin";
            balance = 0;
        } else {
            //Regular user defaults
            defaultRole = "user";
            balance = getBalanceInChosenCurrency(currency);
        }
        
        accountDao.insertAccount(email, hashedPassword, defaultRole, currency, balance);
    }
    
    
    public synchronized void updateLastLoginDate(String email) throws SQLException {
        accountDao.getAccount(email).setLastLoggedIn(new Date());
    }
    
    public String hashPassword(String password) {
        try {
            //Password Hash Code from Lab Class - Week 8 by George Parisis
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwd = password;
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String paswdToStoreInDB = bigInt.toString(16);

            return paswdToStoreInDB;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public float getBalanceInChosenCurrency(String currency){
        float gbpBalance = 1000000;
        String defaultCurrency = "GBP";
        
        if(currency.equals(defaultCurrency)){
            return gbpBalance;
        }
        
        return CurrencyServiceBean.getConvertedAmount(defaultCurrency, currency, gbpBalance);
    }
}
