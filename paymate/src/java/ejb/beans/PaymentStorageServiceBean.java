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

/**
 *
 * @author 119848
 */

@Stateless
public class PaymentStorageServiceBean {
    
    @PersistenceContext(unitName = "paymatePU")
    EntityManager em;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private TimestampServiceBean timestampService;
    
    @EJB
    private CurrencyServiceBean currencyService;
    
    public PaymentStorageServiceBean() {
        
    }
    
    @TransactionAttribute(REQUIRED)
    public synchronized void makePayment(String type, Account origin, 
            Account recipient, String currency, float amount, Date scheduledDate){
        
        String status;
        
        if(type.equals("payment")){
            status = "completed";
            float convertedAmount = convertAmountIntoLocalCurrency(origin.getEmail(), currency, amount);
            
            addAmount(recipient.getEmail(), convertedAmount);
            deductAmount(origin.getEmail(), convertedAmount);
        } else {
            status = "pending";
        }
        
        Payment payment = new Payment(timestampService.getTimestamp(), type, 
                origin, recipient, currency, amount, scheduledDate, status);
        
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
    
    private float convertAmountIntoLocalCurrency(String origin, String currency, float amount){
        String localCurrency = accountStore.getAccount(origin).getCurrency();
        
        if(!localCurrency.equals(currency)){
            float convertedAmount = CurrencyServiceBean.getConvertedAmount(localCurrency, 
                currency, amount);
        
            amount = convertedAmount;
        }
        
        return amount;
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
    
}