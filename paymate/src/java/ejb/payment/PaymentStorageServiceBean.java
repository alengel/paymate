package ejb.payment;

import entity.Account;
import static entity.Account_.id;
import entity.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.ws.WebServiceRef;
import timestamp.TimestampWSService;

/**
 *
 * @author 119848
 */

@Stateless
public class PaymentStorageServiceBean {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/TimestampWSService/TimestampWS.wsdl")
    private TimestampWSService service;
    
    @PersistenceContext EntityManager em;
    
    public PaymentStorageServiceBean() {
        
    }
    
    public synchronized void insertTransaction(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate){
        
        String status;
        
        if(type.equals("payment")){
            status = "completed";
        } else {
            status = "pending";
        }
        
        Payment payment = new Payment(getTimestamp(), type, origin, recipient, currency, 
                amount, scheduledDate, status);
        
        em.persist(payment);
    }
    
    public synchronized List<Payment> getNotifications(Account origin) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.origin = :originId OR c.recipient = :originId", Payment.class);
        return query.setParameter("originId", origin).getResultList();        
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

    //Get timestamp from paymateWS
    public Date getTimestamp() {
        timestamp.TimestampWS port = service.getTimestampWSPort();
        return port.retrieveTimestamp().toGregorianCalendar().getTime();
    }
    
}