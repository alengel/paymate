package notifications;

import ejb.account.AccountStorageServiceBean;
import ejb.payment.PaymentStorageServiceBean;
import entity.Payment;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@ManagedBean
@RequestScoped
public class NotificationsBean implements Serializable {
    
    private String loggedInUser;
    private HtmlDataTable notificationsTable;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public NotificationsBean(){
        loggedInUser = "alena@wa.com";
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public HtmlDataTable getNotificationsTable() {
        return notificationsTable;
    }

    public void setNotificationsTable(HtmlDataTable notificationsTable) {
        this.notificationsTable = notificationsTable;
    }
    
    public List<Payment> getNotifications() {
        return paymentsStore.getNotifications(loggedInUser);
    }

    public void acceptRequest(){
        Payment rowPayment = (Payment) notificationsTable.getRowData();
        float rowPaymentAmount = rowPayment.getAmount();
        
        accountStore.addAmount(rowPayment.getOriginEmail(), rowPaymentAmount);
        accountStore.deductAmount(rowPayment.getRecipient(), rowPaymentAmount);
        paymentsStore.updateStatus(rowPayment.getId(), "accepted");
    }
    
    public void rejectRequest(){
        Payment rowPayment = (Payment) notificationsTable.getRowData();
        paymentsStore.updateStatus(rowPayment.getId(), "rejected");
    }
}
