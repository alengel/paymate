package jsf.beans;

import ejb.beans.AccountStorageServiceBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author 119848
 */

@ManagedBean
@RequestScoped
public class LoginBean implements Serializable {
    
    private String email;
    private String password;
    private String originalURL;
    
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        try {
            request.login(this.email, this.password);
            return "success";
        } catch (ServletException e) {
            facesContext.addMessage(null, new FacesMessage("Login failed."));
            return null;
        }
    }
    
    public String logout(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            //this method will disassociate the principal from the session (effectively logging him/her out)
            request.logout();
            return "login";
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed."));
            return null;
        }
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
