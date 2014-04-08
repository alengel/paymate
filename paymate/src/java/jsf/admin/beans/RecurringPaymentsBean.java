package jsf.admin.beans;

import ejb.interfaces.PaymentStorageService;
import entities.ScheduledPayment;
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
public class RecurringPaymentsBean {

    private HtmlDataTable recurringPaymentsTable;

    @EJB
    private PaymentStorageService paymentsStore;

    public RecurringPaymentsBean() {
    }

    public HtmlDataTable getRecurringPaymentsTable() {
        return recurringPaymentsTable;
    }

    public void setRecurringPaymentsTable(HtmlDataTable recurringPaymentsTable) {
        this.recurringPaymentsTable = recurringPaymentsTable;
    }

    public List<ScheduledPayment> getAllRecurringPayments() {
        return paymentsStore.getAllRecurringPayments();
    }

    public void cancelPayments() {
        ScheduledPayment rowPayment = (ScheduledPayment) recurringPaymentsTable.getRowData();
        paymentsStore.removeScheduledPayment(rowPayment.getId());
    }
}
