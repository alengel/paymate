package ejb.payment;

import entity.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        
        //Get timestamp from WSDL
        Date paymentDate = new Date();
        String status = "completed";
        
        Payment payment = new Payment(paymentDate, type, originEmail, recipient, currency, 
                amount, scheduledDate, status);
        
        em.persist(payment);
    }
    
    public synchronized List<Payment> getNotifications(String email) {
         return em.createNamedQuery("findAllPayments").getResultList();
    }
    
}