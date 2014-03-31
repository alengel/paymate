package dao;

import entities.Account;
import entities.AccountGroup;
import entities.Payment;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author 119848
 */

@Stateless
@LocalBean
public class JdbcPaymentDao implements PaymentDao {
    
    @PersistenceContext(unitName = "paymatePU")
    private EntityManager em;
    
    public JdbcPaymentDao() {
    }
    
    @Override
    public void insertTransaction(Date timestamp, String type, 
                Account origin, Account recipient, String currency, 
                float amount, Date scheduledDate, String status){
        
        Payment payment = new Payment(timestamp, type, origin, recipient, 
                currency, amount, scheduledDate, status);
        
        em.persist(payment);
        em.flush();
    }
    
    @Override
    public List<Payment> getTransactionsByAccount(Account origin) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.origin = :originId OR c.recipient = :originId",
                Payment.class);
        
        return query.setParameter("originId", origin).getResultList();        
    }
    
    @Override
    public List<Payment> getAllTransactions() {
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p", Payment.class);
        return query.getResultList();      
    }
    
    @Override
    public Payment getTransaction(long id){
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.id = :id", Payment.class);
        return query.setParameter("id", id).getSingleResult();
    }
    
    @Override
    public void updateStatus(long id, String status){
        Payment payment = getTransaction(id);
        payment.setStatus(status);
    }
}
