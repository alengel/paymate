package jsf.user.beans;

import jsf.shared.beans.UtilityBean;
import ejb.beans.AccountStorageServiceBean;
import ejb.beans.PaymentStorageServiceBean;
import entities.Account;
import entities.Payment;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class NotificationsBean implements Serializable {
    
    private HtmlDataTable notificationsTable;
    private final UtilityBean utility;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public NotificationsBean(){
        utility = new UtilityBean();
    }

    public HtmlDataTable getNotificationsTable() {
        return notificationsTable;
    }

    public void setNotificationsTable(HtmlDataTable notificationsTable) {
        this.notificationsTable = notificationsTable;
    }
    
    public List<Payment> getNotifications() {
        Account origin = accountStore.getAccount(utility.getLoggedInUser());
        return paymentsStore.getTransactions(origin);
    }

    public void acceptRequest(){
        Payment rowPayment = (Payment) notificationsTable.getRowData();
        float rowPaymentAmount = rowPayment.getAmount();
        
        if(checkBalance(rowPaymentAmount)){
            return;
        }
        
        paymentsStore.updateStatus(rowPayment.getId(), "accepted");
    }
    
    public void rejectRequest(){
        Payment rowPayment = (Payment) notificationsTable.getRowData();
        paymentsStore.updateStatus(rowPayment.getId(), "rejected");
    }
    
    public Boolean checkBalance(float rowRequestedAmount){
        float currentBalance = accountStore.getAccount(utility.getLoggedInUser()).getBalance();
        float tempBalance = currentBalance - rowRequestedAmount;
        
        if(tempBalance <= 0){
            utility.createErrorMessage("Your funds are too low to accept this request.");
            return true;
        }
        return false;
    }

}
