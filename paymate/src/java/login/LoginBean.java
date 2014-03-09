package login;

import ejb.AccountStorageServiceBean;
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
public class LoginBean implements Serializable {
    
    private String email;
    private String password;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public LoginBean(){
        
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
    
    public String login(){
        //Check if email exists and password is correct
        if(checkEmail()){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Email or password are wrong."));

            return null;
        }
        
        return "success";
    }
    
    public Boolean checkEmail(){
        return false;
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("LoginBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("LoginBean: PreDestroy");
    }
}
