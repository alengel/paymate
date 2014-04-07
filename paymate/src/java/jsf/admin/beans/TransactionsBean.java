package jsf.admin.beans;

import ejb.interfaces.PaymentStorageService;
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
    private PaymentStorageService paymentsStore;

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
