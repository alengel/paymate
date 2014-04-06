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
    
    private static final String NETWORK_NAME = "Facebook";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private final UtilityBean utility;
    private String loadingMessage = "Logging you in...";
    private String currency;
    private String email;
    
    @EJB
    private AccountStorageServiceBean accountStore;
    
    public FbOauthBean(){
        utility = new UtilityBean();
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
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }
    
    public void fbLogin() throws IOException {
        String callbackUri = "http://localhost:8080/paymate/faces/oauth.xhtml";
        String authorizationUrl = getService(callbackUri).getAuthorizationUrl(EMPTY_TOKEN);
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(authorizationUrl);
    }
    
    public void fbRegister() throws IOException {
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_registration.xhtml";
        String authorizationUrl = getService(callbackUri).getAuthorizationUrl(EMPTY_TOKEN);
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(authorizationUrl);
    }
    
    public void callOnLoaded() throws SQLException, IOException{
        String callbackUri = "http://localhost:8080/paymate/faces/oauth.xhtml";
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request1 = (HttpServletRequest) context.getRequest();
        String code = request1.getParameter("code");
        
        OAuthService service = getService(callbackUri);
        
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(null, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(response.getBody());
        
        email = o.get("email").toString().replace("\"", "");
        
        if(accountStore.checkAccountExists(email) && 
                accountStore.getAccountRole(email).getGroupName().equals("facebook_user")){
            login(email, "4_p4ym4te_u5er");
        } else {
            //ToDo: you don't have a login, please register first.
            sendToRegistration();
        }
    }
    
    public void callOnLoadedRegistration() throws SQLException, IOException{
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_registration.xhtml";
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request1 = (HttpServletRequest) context.getRequest();
        String code = request1.getParameter("code");
        
        OAuthService service = getService(callbackUri);
        
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(null, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(response.getBody());
        
        email = o.get("email").toString().replace("\"", "");
        
        System.out.print(email);
        
        //Insert facebook user account into the DB account table
        accountStore.insertAccount(email, "4_p4ym4te_u5er", "FB");
        
        context.redirect("http://localhost:8080/paymate/faces/registration_success.xhtml");
        
//        if(accountStore.checkAccountExists(email)){
//            String pw = accountStore.getAccount(email).getPassword();
//            login(email, pw);
//        } else {
//            sendToRegistration();
//        }
    }
    
    public String register() throws SQLException {
        String callbackUri = "http://localhost:8080/paymate/faces/oauth_registration.xhtml";
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request1 = (HttpServletRequest) context.getRequest();
        String code = request1.getParameter("code");
        
        OAuthService service = getService(callbackUri);
        
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(null, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(response.getBody());
        
        email = o.get("email").toString().replace("\"", "");
        
        System.out.print(email);
        
        //Insert facebook user account into the DB account table
        accountStore.insertAccount(email, null, currency);
        return "success";
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
    
    public String login(String email, String password) throws SQLException, IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        try {
            System.out.print("attempting login");
            request.login(email, password);
            accountStore.updateLastLoginDate(email);
            
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect("http://localhost:8080/paymate/faces/user/notifications.xhtml");
        
            return "user";
            
        } catch (ServletException exception) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            System.out.print("user does not exist");
            sendToRegistration();
            return null;
        }
    }
    
    public void sendToRegistration() throws IOException{
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect("http://localhost:8080/paymate/faces/registration.xhtml");
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
