package ejb;

import entity.Account;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author 119848
 */
@Stateless
public class AccountStorageServiceBean {
    
    @PersistenceContext EntityManager em;
    
    public AccountStorageServiceBean() {
        
    }

    public synchronized Object getAccount(String email) {
         return em.createNamedQuery("findAccount").getSingleResult();
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
