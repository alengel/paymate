package ejb.beans;

import entities.Account;
import entities.Payment;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
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
    
    @PersistenceContext(unitName = "paymatePU")
    EntityManager em;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public PaymentStorageServiceBean() {
        
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void makePayment(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate){
        
        String status;
        
        if(type.equals("payment")){
            status = "completed";
            
            addAmount(recipient.getEmail(), amount);
            deductAmount(origin.getEmail(), amount);
        } else {
            status = "pending";
        }
        
        Payment payment = new Payment(getTimestamp(), type, origin, recipient, currency, 
                amount, scheduledDate, status);
        
        em.persist(payment);
        em.flush();
    }
    
    public synchronized void addAmount(String recipient, float amount){
        Account recipientAccount = accountStore.getAccount(recipient);
        
        float balance = recipientAccount.getBalance();
        float newBalance = balance + amount;
        
        recipientAccount.setBalance(newBalance);
    }
    
    public synchronized void deductAmount(String originEmail, float amount){
        Account originAccount = accountStore.getAccount(originEmail);
        
        float balance = originAccount.getBalance();
        float newBalance = balance - amount;
        
        originAccount.setBalance(newBalance);
    }
    
    public synchronized List<Payment> getTransactions(Account origin) {
        TypedQuery<Payment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.origin = :originId OR c.recipient = :originId", Payment.class);
        return query.setParameter("originId", origin).getResultList();        
    }
    
    public synchronized List<Payment> getAllTransactions() {
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p", Payment.class);
        return query.getResultList();      
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