package dao;

import entities.Account;
import entities.ScheduledPayment;
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
public class JpaScheduledPaymentDao implements ScheduledPaymentDao {
    
    @PersistenceContext(unitName = "paymatePU")
    private EntityManager em;
    
    public JpaScheduledPaymentDao() {
    }
    
    @Override
    public void insertScheduledPayment(Account origin, Account recipient, 
            String currency, float amount, Date nextScheduledDate, 
            Date startDate, String frequency){
        
        ScheduledPayment payment = new ScheduledPayment(origin, recipient, 
                currency, amount, nextScheduledDate, startDate, frequency);
        
        em.persist(payment);
        em.flush();
    }
    
    @Override
    public List<ScheduledPayment> getScheduledPaymentsByDate(Date date) {
        TypedQuery<ScheduledPayment> query = em.createQuery(
            "SELECT s FROM ScheduledPayment s WHERE s.nextScheduledDate = :date",
                ScheduledPayment.class);
        
        return query.setParameter("date", date).getResultList();        
    }
    
    @Override
    public List<ScheduledPayment> getScheduledPayment(long id){
        TypedQuery<ScheduledPayment> query = em.createQuery(
            "SELECT c FROM Payment c WHERE c.id = :id", ScheduledPayment.class);
        return query.setParameter("id", id).getResultList();
    }
    
    @Override
    public void updateScheduledDate(long id, Date updatedDate){
        ScheduledPayment payment = getScheduledPayment(id).get(0);
        payment.setNextScheduledDate(updatedDate);
    }
}
