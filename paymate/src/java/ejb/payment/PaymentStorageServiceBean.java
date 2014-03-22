package ejb.payment;

import entity.Payment;
import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
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
    
    public synchronized void insertTransaction(String type, String originEmail, String recipient, String currency, 
                float amount, Date scheduledDate){
        String status;
        
        if(type.equals("payment")){
            status = "completed";
        } else {
            status = "pending";
        }
        
        Payment payment = new Payment(getTimestamp(), type, originEmail, recipient, currency, 
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

    //Get timestamp from paymateWS
    public Date getTimestamp() {
        timestamp.TimestampWS port = service.getTimestampWSPort();
        return port.retrieveTimestamp().toGregorianCalendar().getTime();
    }
    
    //Get currency rates from paymateRS
    public String getCurrencies(){
        Client client = ClientBuilder.newClient();
        String currencies = client.target("http://localhost:8080/paymateRS/conversion/all")
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return currencies;
    }
}