package ejb.beans;

import dao.DAOFactory;
import dao.JdbcAccountDAO;
import entities.Account;
import entities.AccountGroup;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import jsf.shared.beans.UtilityBean;

/**
 *
 * @author 119848
 */

@Stateless
public class AccountStorageServiceBean {
    
    @PersistenceContext(unitName = "paymatePU")
    EntityManager em;
    
    private final UtilityBean utility;
    
    @EJB
    private JdbcAccountDAO dao;
    
    public AccountStorageServiceBean() {
        utility = new UtilityBean();
    }
    
    public synchronized Boolean checkAccountExists(String email){
        Query result = em.createNamedQuery("getAccountWithEmail")
          .setParameter("email", email);
        
        return (long)result.getSingleResult() != 0;
    }

    public synchronized Account getAccount(String email) {
        TypedQuery<Account> query = em.createQuery(
            "SELECT c FROM Account c WHERE c.email = :email", Account.class);
        return query.setParameter("email", email).getSingleResult();         
    }
    
    public synchronized AccountGroup getAccountRole(String email) {
        TypedQuery<AccountGroup> query = em.createQuery(
            "SELECT c FROM AccountGroup c WHERE c.email = :email", AccountGroup.class);
        return query.setParameter("email", email).getSingleResult();         
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
        
        Account account = new Account(email, hashedPassword, currency, balance, defaultRole, new Date());
        AccountGroup accountGroup = new AccountGroup(email, defaultRole);
        
        em.persist(account);
        em.persist(accountGroup);
        em.flush();
    }
    
    public synchronized List<Account> getAccounts() throws SQLException{
        return dao.getAccounts();
//        TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
//        return query.getResultList();
    }
    
    public synchronized void updateLastLoginDate(String email){
        TypedQuery<Account> query = em.createQuery(
            "SELECT a FROM Account a WHERE a.email = :email", Account.class);
        
        query.setParameter("email", email).getSingleResult().setLastLoggedIn(new Date());
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
