package jsf.shared.beans;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ejb.beans.AccountStorageServiceBean;
import entities.Account;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class FbLoginBean implements Serializable {
    
    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private String loadingMessage = "Logging you in...";
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public FbLoginBean(){
        
    }
    
    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }
    
    public void fbLogin() throws IOException
    {
        String authorizationUrl = getService().getAuthorizationUrl(EMPTY_TOKEN);
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(authorizationUrl);
    }
    
    public void callOnLoaded() throws SQLException{
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request1 = (HttpServletRequest) context.getRequest();
        String code = request1.getParameter("code");
        OAuthService service = getService();
        
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(null, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(response.getBody());
        
        String email = o.get("email").toString().replace("\"", "");
        
        if(accountStore.checkAccountExists(email)){
            String pw = accountStore.getAccount(email).getPassword();
            login(email, pw);
        } else {
            sendToRegistration();
        }
    }
    
    private OAuthService getService(){
        String apiKey = "730517613635252";
        String apiSecret = "2462bbabcc2bcc11e40570a0116289aa";
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .scope("email")
                                      .apiSecret(apiSecret)
                                      .callback("http://localhost:8080/paymate/faces/oauth.xhtml")
                                      .build();
        
        return service;
    }
    
    public String login(String email, String password) throws SQLException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        try {
            System.out.print("attempting login");
            request.login(email, password);
            
//            accountStore.updateLastLoginDate(email);
//            String accountRole = accountStore.getAccountRole(email).getGroupName();
//
//            if (accountRole.equals("admin")){
//                return "admin";
//            } else {
                return "user";
//            }
        } catch (ServletException exception) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            System.out.print("user does not exist");
            return sendToRegistration();
        }
    }
    
    public String sendToRegistration(){
        return "registration";
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("FBLoginBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("FBLoginBean: PreDestroy");
    }
}
