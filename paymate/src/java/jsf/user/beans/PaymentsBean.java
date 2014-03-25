package jsf.user.beans;

import ejb.beans.AccountStorageServiceBean;
import ejb.beans.PaymentStorageServiceBean;
import entities.Account;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class PaymentsBean implements Serializable {
    
    private String type;
    private String recipient;
    private String currency;
    private String[] currencies;
    private float amount;
    private Date scheduledDate;
    CurrencyBean currencyBean;
    UtilityBean utility;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public PaymentsBean(){
        utility = new UtilityBean();
        
        currencyBean = new CurrencyBean();
        currencies = new String[]{"GBP", "EUR", "USD"};
    }    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String[] getCurrencies(){
        return currencies;
    }

    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }
    
    public String getCurrency() {
        Account account = accountStore.getAccount(utility.getLoggedInUser());
        String defaultCurrency = account.getCurrency();
        
        return defaultCurrency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public String getDefaultScheduledDate(){
        DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = originalFormat.format(new Date());
        
        return todayDate;
    }
    
    public String getAccountBalance(){
        Account account = accountStore.getAccount(utility.getLoggedInUser());
        float balance = account.getBalance();
        String localCurrency = currencyBean.changeCurrencyStringToSymbol(account.getCurrency());
        
        return localCurrency + balance;
    }
    
    private void setConvertedAmount(){
        String localCurrency = accountStore.getAccount(utility.getLoggedInUser()).getCurrency();
        
        if(!localCurrency.equals(currency)){
            float convertedAmount = currencyBean.calculateAmountInChosenCurrency(localCurrency, 
                currency, amount);
        
            amount = convertedAmount;
        }        
    }
    
    public String makePayment(){
        
        try {
            if(validateFormFields()){
                return null;
            }
            if(checkBalance()){
                return null;
            }

            type = "payment";

            makePayment(utility.getLoggedInUser(), recipient);

            return "payment_success";
        }
        catch (EJBTransactionRolledbackException exception) {
            utility.createErrorMessage("Oops, something went wrong. Please try again.");
            return null;
        }
    }
    
    public String requestFunds(){
        if(validateFormFields()){
            return null;
        }
        
        type = "request";
        
        makePayment(recipient, utility.getLoggedInUser());
        
        return "request_success";
    }
    
    public void makePayment(String originEmail, String recipientEmail){
        Account origin = accountStore.getAccount(originEmail);
        Account recipient2 = accountStore.getAccount(recipientEmail);
        
        setConvertedAmount();
        
        //Insert payment into the DB payments table
        paymentsStore.makePayment(type, origin, recipient2, currency, 
                amount, scheduledDate);
    }

    public Boolean validateFormFields(){
        if(!accountStore.checkAccountExists(recipient)){
            utility.createErrorMessage("The recipient does not have an account with PayMate.");
            return true;
        }
        
        if(utility.getLoggedInUser().equals(recipient)){
            utility.createErrorMessage("You can't send funds to yourself.");
            return true;
        }
        
        if(amount == 0){
            utility.createErrorMessage("Please enter a higher amount than 0");
            return true;
        }
        
        if(scheduledDate == null){
            scheduledDate = new Date();
        }
        
        return false;
    }
    
    public Boolean checkBalance(){
        float currentBalance = accountStore.getAccount(utility.getLoggedInUser()).getBalance();
        float tempBalance = currentBalance - amount;
        
        if(tempBalance <= 0){
            utility.createErrorMessage("Your funds are too low to make this payment.");
            return true;
        }
        
        return false;
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("PaymentsBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("PaymentsBean: PreDestroy");
    }
}
