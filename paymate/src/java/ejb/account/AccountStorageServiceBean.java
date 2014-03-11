package ejb.account;

import entity.Account;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author 119848
 */

@Stateless
public class AccountStorageServiceBean {
    
    @PersistenceContext EntityManager em;
    
    public AccountStorageServiceBean() {
        
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
    
    public synchronized void insertAccount(String email, String password, String currency, float balance) {
        Account account = new Account(email, password, currency, balance);
        
        em.persist(account);
    }
    
    public synchronized void addAmount(String recipient, float amount){
        Account recipientAccount = getAccount(recipient);
        
        float balance = recipientAccount.getBalance();
        float newBalance = balance + amount;
        
        recipientAccount.setBalance(newBalance);
    }
    
    public synchronized void deductAmount(String originEmail, float amount){
        Account originAccount = getAccount(originEmail);
        
        float balance = originAccount.getBalance();
        float newBalance = balance - amount;
        
        originAccount.setBalance(newBalance);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("AccountStore: PostConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("AccountStore: PreDestroy");
    }
}
