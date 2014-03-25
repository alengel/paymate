package jsf.user.beans;

import ejb.beans.AccountStorageServiceBean;
import ejb.beans.CurrencyStorageServiceBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class RegistrationBean implements Serializable {
    
    private String email;
    private String password;
    private String passwordVerification;
    private String currency;
    private final UtilityBean utility;

    @EJB
    private AccountStorageServiceBean accountStore;

    public RegistrationBean() {
        utility = new UtilityBean();
    }
   
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordVerification() {
        return passwordVerification;
    }

    public void setPasswordVerification(String passwordVerification) {
        this.passwordVerification = passwordVerification;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public float getBalanceInChosenCurrency(){
        float gbpBalance = 1000000;
        
        if(currency.equals("GBP")){
            return gbpBalance;
        }
        
        String convertedBalance = CurrencyStorageServiceBean.getConvertedAmount("GBP", currency, gbpBalance);
        return Float.parseFloat(convertedBalance);
    }
    
    public String register() {
        //Check if user already exists
        if(checkIfAccountExists()){
            return null;
        }
        
        //Check if passwords match
        if(!checkPasswordsMatch()){
            return null;
        }
        
        //Insert user account into the DB account table
        accountStore.insertAccount(email, password, currency, getBalanceInChosenCurrency());
        return "success";
    }
    
    public Boolean checkPasswordsMatch(){
        if(password.equals(passwordVerification)){
            return true;
        } else {
            utility.createErrorMessage("Passwords don't match.");
            return false;
        }        
    }
    
    public Boolean checkIfAccountExists(){
        if(accountStore.checkAccountExists(email)){
            utility.createErrorMessage("Email already exists");
            return true;
        }
        
        return false;
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("RegistrationBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("RegistrationBean: PreDestroy");
    }
}
