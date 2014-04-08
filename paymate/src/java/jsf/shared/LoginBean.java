package jsf.shared;

import ejb.interfaces.AccountStorageService;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author 119848
 */
@Named
@RequestScoped
public class LoginBean implements Serializable {

    private String email;
    private String password;
    private final UtilityBean utility;

    @EJB
    private AccountStorageService accountStore;

    public LoginBean() {
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

    //Main login function for non-Oauth users
    public String login() throws SQLException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        try {
            request.login(this.email, this.password);
            return redirectUser(this.email);
        } catch (ServletException exception) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            utility.createErrorMessage("Username or password are incorrect.");
            return null;
        }
    }

    //Main logout function for all users
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
            return "login";

        } catch (ServletException exception) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            utility.createErrorMessage("Something went wrong. You were NOT logged out.");
            return null;
        }
    }

    //Redirect user to correct page based on their account role
    private String redirectUser(String email) throws SQLException {
        accountStore.updateLastLoginDate(email);
        String accountRole = accountStore.getAccountRole(email).getGroupName();

        if (accountRole.equals("admin")) {
            return "admin";
        } else {
            return "user";
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
