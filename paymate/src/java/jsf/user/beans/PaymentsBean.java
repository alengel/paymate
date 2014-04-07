package jsf.user.beans;

import jsf.shared.beans.UtilityBean;
import ejb.interfaces.AccountStorageService;
import ejb.interfaces.PaymentStorageService;
import entities.Account;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
    private String frequency;
    private String frequencyType;
    private final CurrencyBean currencyBean;
    private final UtilityBean utility;
    
    @EJB
    private AccountStorageService accountStore;
    
    @EJB
    private PaymentStorageService paymentsStore;
    
    public PaymentsBean(){
        utility = new UtilityBean();
        currencyBean = new CurrencyBean();
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
        currencies = paymentsStore.getAvailableCurrencies();
        
        //If Rest Currency Service is not available, redirect
        if(currencies == null){
            redirectToServicesDownPage();
            return null;
        }
        
        return currencies;
    }

    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }
    
    public String getCurrency() throws SQLException {
        //Get user's default currency to select the currency by default
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyType() {
        //Set default frequency type to once
        frequencyType = "once";
        
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }
    
    public String getDefaultScheduledDate(){
        //Set the default scheduled date to today
        DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = originalFormat.format(new Date());
        
        return todayDate;
    }
    
    public String getAccountBalance() throws SQLException{
        //Get the logged in user's account balance and display 
        //it with their chosen currency symbol
        Account account = accountStore.getAccount(utility.getLoggedInUser());
        float balance = account.getBalance();
        String localCurrency = currencyBean.changeCurrencyStringToSymbol(account.getCurrency());
        
        return localCurrency + balance;
    }
    
    //Main function to handle payments
    public String makePayment() throws SQLException{
        
        try {
            //If form validation fails, display errors and return null
            if(validateFormFields()){
                return null;
            }
            //Check balance is sufficient
            if(checkBalance()){
                return null;
            }
            
            //Set payment type
            type = "payment";
            
            //If frequency type is recurring, schedule payment
            if(frequencyType.equals("recurring")){
                schedulePayment(utility.getLoggedInUser(), recipient);
            //Otherwise it is a one-off payment, so make instant payment
            } else {
                makePayment(utility.getLoggedInUser(), recipient);
            }
            
            return "payments_success";
        }
        catch (EJBTransactionRolledbackException exception) {
            Logger.getLogger(PaymentsBean.class.getName()).log(Level.SEVERE, null, exception);
            return "payments_failure";
        }
    }
    
    //Main function to handle requests
    public String requestFunds() throws SQLException{
        //If form validation fails, display errors and return null
        if(validateFormFields()){
            return null;
        }
        
        //Set payment type
        type = "request";
        
        //Make instant payment 
        makePayment(recipient, utility.getLoggedInUser());
        
        return "requests_success";
    }
    
    //Insert payment into the DB payments table
    public void makePayment(String originEmail, String recipientEmail) throws SQLException{
        Account originAccount = accountStore.getAccount(originEmail);
        Account recipientAccount = accountStore.getAccount(recipientEmail);
                
        paymentsStore.makePayment(type, originAccount, recipientAccount, currency, 
                amount, scheduledDate);
    }
    
    //Insert scheduled payment into the DB scheduled payments table
    public void schedulePayment(String originEmail, String recipientEmail) throws SQLException{
        Account originAccount = accountStore.getAccount(originEmail);
        Account recipientAccount = accountStore.getAccount(recipientEmail);
                
        paymentsStore.schedulePayment(originAccount, recipientAccount, currency, 
                amount, scheduledDate, frequency);
    }

    public Boolean validateFormFields() throws SQLException{
        //Check if account exists before making payment/request
        if(!accountStore.checkAccountExists(recipient)){
            utility.createErrorMessage("The recipient does not have an account with PayMate.");
            return true;
        }
        
        //Check the user is not sending funds to themselves
        if(utility.getLoggedInUser().equals(recipient)){
            utility.createErrorMessage("You can't send funds to yourself.");
            return true;
        }
        
        //Check the amount is more than zero
        if(amount == 0){
            utility.createErrorMessage("Please enter a higher amount than 0.");
            return true;
        }
        
        //If the user has not chosen a date, use today as default
        if(scheduledDate == null){
            scheduledDate = new Date();
        }
        
        //If the user has chosen a date, check it is not in the past
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        if(scheduledDate.before(getYesterdaysDate())){
            utility.createErrorMessage("Please enter a date from today onwards.");
            return true;
        }
        
        return false;
    }
    
    //Get yesterdays date
    private Date getYesterdaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);    
        return calendar.getTime();
    }
    
    //Get user's balance and check there are sufficient funds for the transaction
    public Boolean checkBalance() throws SQLException{
        float currentBalance = accountStore.getAccount(utility.getLoggedInUser()).getBalance();
        float tempBalance = currentBalance - amount;
        
        //Display error message if the funds are insufficient
        if(tempBalance <= 0){
            utility.createErrorMessage("Your funds are too low to make this payment.");
            return true;
        }
        
        return false;
    }
    
    //Redirect user to services_down page
    private void redirectToServicesDownPage() {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "/faces/services_down.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(PaymentsBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
