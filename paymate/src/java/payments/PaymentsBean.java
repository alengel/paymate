package payments;

import ejb.account.AccountStorageServiceBean;
import ejb.payment.PaymentStorageServiceBean;
import entity.Account;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
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
    private String originEmail;
    private String recipient;
    private String currency;
    private float amount;
    private Date scheduledDate;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    @EJB
    private PaymentStorageServiceBean paymentsStore;
    
    public PaymentsBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        originEmail = request.getRemoteUser();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginEmail() {
        return originEmail;
    }

    public void setOriginEmail(String originEmail) {
        this.originEmail = originEmail;
    }
    
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getCurrency() {
        return currency;
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
        Account account = accountStore.getAccount(originEmail);
        float balance = account.getBalance();
        String localCurrency = changeCurrencyStringToSymbol(account.getCurrency());
        
        return localCurrency + balance;
    }
    
    private String changeCurrencyStringToSymbol(String currencyString){
        String currencySymbol;
        
        switch (currencyString) {
            case "GBP":  currencySymbol = "£";
                     break;
            case "USD":  currencySymbol = "$";
                     break;
            case "EUR":  currencySymbol = "€";
                     break;
            default: currencySymbol = "Invalid currency";
                     break;            
        }
        return currencySymbol;
    }
    
    public String makePayment(){
        if(validateFormFields()){
            return null;
        }
        if(checkBalance()){
            return null;
        }
        
        type = "payment";
        
        //Insert payment into the DB payments table
        insertTransaction(originEmail, recipient);
        
        //Deduct amount from origin account balance
        deductAmountFromOrigin();
        
        //Add amount to recipient account balance
        addAmountToRecipient();
        
        return "payment_success";
    }
    
    public String requestFunds(){
        if(validateFormFields()){
            return null;
        }
        
        type = "request";
        
        //Insert payment into the DB payments table
        insertTransaction(recipient, originEmail);
        
        return "request_success";
    }
    
    public void insertTransaction(String originEmail, String recipient){
        paymentsStore.insertTransaction(type, originEmail, recipient, currency, 
                amount, scheduledDate);
    }
    
    public void deductAmountFromOrigin(){
        accountStore.deductAmount(originEmail, amount);
    }
    
    public void addAmountToRecipient(){
        accountStore.addAmount(recipient, amount);
    }

    public Boolean validateFormFields(){
        if(!accountStore.checkAccountExists(recipient)){
            createErrorMessage("The recipient does not have an account with PayMate.");
            return true;
        }
        
        if(originEmail.equals(recipient)){
            createErrorMessage("You can't send funds to yourself.");
            return true;
        }
        
        if(amount == 0){
            createErrorMessage("Please enter a higher amount than 0");
            return true;
        }
        
        if(scheduledDate == null){
            scheduledDate = new Date();
        }
        
        return false;
    }
    
    public Boolean checkBalance(){
        float currentBalance = accountStore.getAccount(originEmail).getBalance();
        float tempBalance = currentBalance - amount;
        
        if(tempBalance <= 0){
            createErrorMessage("Your funds are too low to make this payment.");
            return true;
        }
        return false;
    }
    
    public void createErrorMessage(String errorMessage){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(errorMessage));
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
