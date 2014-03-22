package notifications;

import ejb.account.AccountStorageServiceBean;
import ejb.payment.PaymentStorageServiceBean;
import entity.Payment;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        loggedInUser = request.getRemoteUser();
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    
    public String getCurrentDate(){
        DateFormat originalFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = originalFormat.format(paymentsStore.getTimestamp());
        return currentDate;
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
        
        if(checkBalance(rowPaymentAmount)){
            return;
        }
        
        accountStore.addAmount(rowPayment.getRecipient(), rowPaymentAmount);
        accountStore.deductAmount(rowPayment.getOriginEmail(), rowPaymentAmount);
        paymentsStore.updateStatus(rowPayment.getId(), "accepted");
    }
    
    public void rejectRequest(){
        Payment rowPayment = (Payment) notificationsTable.getRowData();
        paymentsStore.updateStatus(rowPayment.getId(), "rejected");
    }
    
    public Boolean checkBalance(float rowRequestedAmount){
        float currentBalance = accountStore.getAccount(loggedInUser).getBalance();
        float tempBalance = currentBalance - rowRequestedAmount;
        
        if(tempBalance <= 0){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Your funds are too low to accept this request."));
            return true;
        }
        return false;
    }
}
