package ejb.payment;

import entity.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author 119848
 */

@Stateless
public class PaymentStorageServiceBean {
    
    
    
    @PersistenceContext EntityManager em;
    
    public PaymentStorageServiceBean() {
        
    }
    
    public void insertTransaction(String type, String originEmail, String recipient, String currency, 
                float amount, Date scheduledDate){
        String status;
        //Get timestamp from WSDL
        Date paymentDate = new Date();
        
        if(type.equals("payment")){
            status = "completed";
        } else {
            status = "pending";
        }
        
        Payment payment = new Payment(paymentDate, type, originEmail, recipient, currency, 
                amount, scheduledDate, status);
        
        em.persist(payment);
    }
    
    public synchronized List<Payment> getNotifications(String originEmail) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.originEmail = :originEmail OR c.recipient = :originEmail", Payment.class);
        return query.setParameter("originEmail", originEmail).getResultList();        
    }
    
    public synchronized Payment processPayment(long id){
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.id = :id", Payment.class);
        return query.setParameter("id", id).getSingleResult();
    }
    
    public synchronized void updateStatus(long id, String status){
        Payment payment = processPayment(id);
        
        payment.setStatus(status);
    }
    
}