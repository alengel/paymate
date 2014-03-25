package jsf.admin.beans;

import ejb.beans.AccountStorageServiceBean;
import ejb.beans.PaymentStorageServiceBean;
import entities.Account;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlDataTable;
import javax.inject.Named;
import jsf.user.beans.UtilityBean;

/**
 *
 * @author 119848
 */

@Named
@ManagedBean
@RequestScoped
public class AccountsBean implements Serializable {
    
    private HtmlDataTable accountsTable;
    private final UtilityBean utility;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public AccountsBean(){
        utility = new UtilityBean();
    }

    public HtmlDataTable getAccountsTable() {
        return accountsTable;
    }

    public void setAccountsTable(HtmlDataTable accountsTable) {
        this.accountsTable = accountsTable;
    }
    
    public List<Account> getAccounts() {
        return accountStore.getAccounts();
    }
}
