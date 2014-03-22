package registration;

import ejb.account.AccountStorageServiceBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

    @EJB
    private AccountStorageServiceBean accountStore;

    public RegistrationBean() {
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
        accountStore.insertAccount(email, password, currency);
        return "success";
    }
    
    public Boolean checkPasswordsMatch(){
        if(password.equals(passwordVerification)){
            return true;
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Passwords don't match."));
            
            return false;
        }        
    }
    
    public Boolean checkIfAccountExists(){
        if(accountStore.checkAccountExists(email)){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Email already exists"));
            
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
