package jsf.shared.beans;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ejb.beans.AccountStorageServiceBean;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
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
public class FbOauthBean implements Serializable {
    
    private static final String FB_ME_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private String loadingMessage;
    private String currency;
    private String email;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public FbOauthBean(){
    }
    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getLoadingMessage() {
        loadingMessage = "Loading, please wait...";
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }
    
    public void fbLogin() throws IOException {
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_login.xhtml";
        String authorizationUrl = getService(callbackUri).getAuthorizationUrl(EMPTY_TOKEN);
        
        redirectToAnotherPage(authorizationUrl);
    }
    
    public void fbRegister() throws IOException {
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_registration.xhtml";
        String authorizationUrl = getService(callbackUri).getAuthorizationUrl(EMPTY_TOKEN);
        
        redirectToAnotherPage(authorizationUrl);
    }
    
    public void callOnLoginLoaded() throws SQLException, IOException{
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_login.xhtml";
        
        callOnLoaded(callbackUri);
        
        if(accountStore.checkAccountExists(email) && 
                accountStore.getAccountRole(email).getGroupName().equals("facebook_user")){
            login(email, getDefaultFbPassword());
        } else {
            System.out.print("Please register an account with PayMate first.");
            redirectToAnotherPage("http://localhost:8080/paymate/faces/registration.xhtml");
        }
    }
    
    public void callOnRegistrationLoaded() throws SQLException, IOException{
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_registration.xhtml";
        
        callOnLoaded(callbackUri);
                
        //Check if user already exists
        if(accountStore.checkAccountExists(email) && 
                accountStore.getAccountRole(email).getGroupName().equals("facebook_user")){
            login(email, getDefaultFbPassword());
        }
        
        //Insert facebook user account into the DB account table
        accountStore.insertAccount(email, getDefaultFbPassword(), "FB");
        
        redirectToAnotherPage("http://localhost:8080/paymate/faces/registration_success.xhtml");
    }
    
    public void callOnLoaded(String callbackUri){
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest codeRequest = (HttpServletRequest) context.getRequest();
        String code = codeRequest.getParameter("code");
        
        OAuthService service = getService(callbackUri);
        
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(null, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, FB_ME_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(response.getBody());
        
        email = o.get("email").toString().replace("\"", "");
    }
    
    private OAuthService getService(String callbackUri){
        String apiKey = "730517613635252";
        String apiSecret = "2462bbabcc2bcc11e40570a0116289aa";
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .scope("email")
                                      .apiSecret(apiSecret)
                                      .callback(callbackUri)
                                      .build();
        
        return service;
    }
    
    public void login(String email, String password) throws SQLException, IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        try {
            
            request.login(email, password);
            accountStore.updateLastLoginDate(email);
            redirectToAnotherPage("http://localhost:8080/paymate/faces/user/notifications.xhtml");
            
        } catch (ServletException exception) {
            
            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            redirectToAnotherPage("http://localhost:8080/paymate/faces/login_failure.xhtml");
            
        }
    }
    
    public String getDefaultFbPassword() {
        return "4_p4ym4te_u5er";
    }
    
    public void redirectToAnotherPage(String url) throws IOException{
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(url);
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("FbOauthBean: PostConstruct");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("FbOauthBean: PreDestroy");
    }
}
