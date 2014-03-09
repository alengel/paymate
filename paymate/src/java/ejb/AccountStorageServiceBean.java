package ejb;

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

    public synchronized Account getAccount(String email, String password) {
        TypedQuery<Account> query = em.createQuery(
            "SELECT c FROM Account c WHERE c.email = :email", Account.class);
        return query.setParameter("email", email).getSingleResult(); 
        
//        return em.createNamedQuery("getAccount").getSingleResult();
    }
    
    public synchronized void insertAccount(String email, String password, String currency, int balance) {
        Account account = new Account(email, password, currency, balance);
        
        em.persist(account);
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
