package jsf.user.beans;

import jsf.shared.beans.UtilityBean;
import ejb.beans.AccountStorageServiceBean;
import ejb.beans.PaymentStorageServiceBean;
import entities.Account;
import entities.ScheduledPayment;
import java.io.Serializable;
import java.sql.SQLException;
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
public class RecurringBean implements Serializable {
    
    private HtmlDataTable recurringPaymentsTable;
    private final UtilityBean utility;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public RecurringBean(){
        utility = new UtilityBean();
    }

    public HtmlDataTable getRecurringPaymentsTable() {
        return recurringPaymentsTable;
    }

    public void setRecurringPaymentsTable(HtmlDataTable recurringPaymentsTable) {
        this.recurringPaymentsTable = recurringPaymentsTable;
    }

    public List<ScheduledPayment> getRecurringPayments() throws SQLException {
        Account origin = accountStore.getAccount(utility.getLoggedInUser());
        return paymentsStore.getRecurringPayments(origin);
    }
    
    public void cancelPayments(){
        ScheduledPayment rowPayment = (ScheduledPayment) recurringPaymentsTable.getRowData();
        paymentsStore.removeScheduledPayment(rowPayment.getId());
    }
}
