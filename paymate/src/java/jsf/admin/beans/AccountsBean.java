package jsf.admin.beans;

import ejb.beans.AccountStorageServiceBean;
import ejb.beans.PaymentStorageServiceBean;
import entities.Account;
import entities.Payment;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.inject.Named;
import jsf.shared.beans.UtilityBean;

/**
 *
 * @author 119848
 */

@Named
@SessionScoped
public class AccountsBean implements Serializable {
    
    private HtmlDataTable accountsTable;
    private HtmlDataTable accountTransactionsTable;
    private Account selected;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public AccountsBean(){
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
    
    public List<Account> getAccounts() {
        return accountStore.getAccounts();
    }

    public String viewTransactionsByUser(){
        selected = (Account) accountsTable.getRowData();
        return "account_transactions";
    }
    
    public List<Payment> getAccountTransactions() {
        return paymentsStore.getTransactions(selected);
    }
}
