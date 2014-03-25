package jsf.admin.beans;

import ejb.beans.PaymentStorageServiceBean;
import entities.Payment;
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
public class TransactionsBean {
    private HtmlDataTable transactionsTable;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;

    public TransactionsBean() {
    }

    public HtmlDataTable getTransactionsTable() {
        return transactionsTable;
    }

    public void setTransactionsTable(HtmlDataTable transactionsTable) {
        this.transactionsTable = transactionsTable;
    }
    
    public List<Payment> getAllTransactions() {
        return paymentsStore.getAllTransactions();
    }
}
