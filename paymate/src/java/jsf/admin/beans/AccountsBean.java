package jsf.admin.beans;

import ejb.interfaces.AccountStorageService;
import ejb.interfaces.PaymentStorageService;
import entities.Account;
import entities.Payment;
import entities.ScheduledPayment;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.inject.Named;

/**
 *
 * @author 119848
 */
@Named
@SessionScoped
public class AccountsBean implements Serializable {

    private HtmlDataTable accountsTable;
    private HtmlDataTable accountTransactionsTable;
    private HtmlDataTable accountRecurringPaymentsTable;
    private Account selected;

    @EJB
    private AccountStorageService accountStore;

    @EJB
    private PaymentStorageService paymentsStore;

    public AccountsBean() {
    }

    public HtmlDataTable getAccountsTable() {
        return accountsTable;
    }

    public void setAccountsTable(HtmlDataTable accountsTable) {
        this.accountsTable = accountsTable;
    }

    public HtmlDataTable getAccountTransactionsTable() {
        return accountTransactionsTable;
    }

    public void setAccountTransactionsTable(HtmlDataTable accountTransactionsTable) {
        this.accountTransactionsTable = accountTransactionsTable;
    }

    public HtmlDataTable getAccountRecurringPaymentsTable() {
        return accountRecurringPaymentsTable;
    }

    public void setAccountRecurringPaymentsTable(HtmlDataTable accountRecurringPaymentsTable) {
        this.accountRecurringPaymentsTable = accountRecurringPaymentsTable;
    }

    public List<Account> getAccounts() throws SQLException {
        return accountStore.getAccounts();
    }

    public String viewTransactionsByUser() {
        selected = (Account) accountsTable.getRowData();
        return "account_transactions";
    }

    public List<Payment> getAccountTransactions() {
        return paymentsStore.getTransactions(selected);
    }

    public String viewRecurringPaymentsByUser() {
        selected = (Account) accountsTable.getRowData();
        return "account_recurring";
    }

    public List<ScheduledPayment> getAccountRecurringPayments() {
        return paymentsStore.getRecurringPayments(selected);
    }

    public void cancelPayments() {
        ScheduledPayment rowPayment = (ScheduledPayment) accountRecurringPaymentsTable.getRowData();
        paymentsStore.removeScheduledPayment(rowPayment.getId());
    }
}
