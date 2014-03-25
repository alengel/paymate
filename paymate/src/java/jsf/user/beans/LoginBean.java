package jsf.user.beans;

import ejb.beans.AccountStorageServiceBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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
    private final UtilityBean utility;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public LoginBean(){
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
        
    public String login(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        try {
            request.login(this.email, this.password);
            
            accountStore.updateLastLoginDate(email);
            String accountRole = accountStore.getAccountRole(email).getGroupName();
            
            if (accountRole.equals("admin")){
                return "admin";
            } else {
                return "user";
            }
            
        } catch (ServletException e) {
            utility.createErrorMessage("Username or password are incorrect.");
            return null;
        }
    }
    
    public String logout(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            return "login";
        } catch (ServletException e) {
            utility.createErrorMessage("Something went wrong. You were NOT logged out.");
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
