package jsf.shared;

import services.TimestampService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
//Utility class shared across the JSF views
@Named
@RequestScoped
public class UtilityBean {

    String loggedInUser;

    @EJB
    private TimestampService timestampService;

    public UtilityBean() {

    }

    //Return the currently logged in user
    public String getLoggedInUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        return request.getRemoteUser();
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    //Create an error message and append it to the class "global-error"
    public void createErrorMessage(String errorMessage) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(errorMessage));
    }

    //Get the current date to display on page
    public String getCurrentDate() {
        DateFormat originalFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = originalFormat.format(timestampService.getTimestamp());
        return currentDate;
    }
}
