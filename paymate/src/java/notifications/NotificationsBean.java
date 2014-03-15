package notifications;

import ejb.payment.PaymentStorageServiceBean;
import entity.Payment;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class NotificationsBean {
    
    private String originEmail;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public NotificationsBean(){
        originEmail = "user2@wa.com";
    }
    
    public List<Payment> getNotifications() {
        return paymentsStore.getNotifications(originEmail);
    }
}
