package login;

import ejb.account.AccountStorageServiceBean;
import entity.Account;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.RequestDispatcher;

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

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        originalURL = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);

        if (originalURL == null) {
            originalURL = externalContext.getRequestContextPath() + "/home.xhtml";
        } else {
            String originalQuery = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_QUERY_STRING);

            if (originalQuery != null) {
                originalURL += "?" + originalQuery;
            }
        }
    }
    
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
    
//    public void login() throws IOException {
//        FacesContext context = FacesContext.getCurrentInstance();
//        ExternalContext externalContext = context.getExternalContext();
//        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
//
//        try {
//            request.login(email, password);
//            Account account = accountStore.getAccount(email, password);
//            externalContext.getSessionMap().put("account", account);
//            externalContext.redirect(originalURL);
//        } catch (ServletException e) {
//            // Handle unknown username/password in request.login().
//            context.addMessage(null, new FacesMessage("Unknown login"));
//        }
//    }
//
//    public void logout() throws IOException {
//        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//        externalContext.invalidateSession();
//        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
//    }
    
    public String login(){
        //Check if email exists and password is correct
        if(!checkEmail()){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage(null, new FacesMessage("Email or password are wrong."));

            return null;
        }
        
        return "success";
    }
    
    public Boolean checkEmail(){
        Account account;
        
        try
        {
            account = accountStore.getAccount(email);
            return account.getPassword().equals(password);
        }
        catch(Exception e)
        {
            
        }
            
        return false;
    }
    
//    @PostConstruct
//    public void postConstruct() {
//        System.out.println("LoginBean: PostConstruct");
//    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("LoginBean: PreDestroy");
    }
}
