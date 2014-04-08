package jsf.shared;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ejb.interfaces.AccountStorageService;
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

    @EJB
    private AccountStorageService accountStore;

    public FbOauthBean() {
    }

    public String getLoadingMessage() {
        loadingMessage = "Loading, please wait...";
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    //Called when the "Login with Facebook" button is clicked
    //Redirects to the FB authentication page
    public void fbLogin() throws IOException {
        String callbackUri = buildFullUri("oauth_login.xhtml");
        redirectToAuthorizationUrl(callbackUri);
    }

    public void callOnLoginLoaded() throws SQLException, IOException {
        String callbackUri = buildFullUri("oauth_login.xhtml");
        String email = callOnLoaded(callbackUri);

        //If user has an account and is a FB user, login
        if (accountStore.checkAccountExists(email)
                && accountStore.getAccountRole(email).getGroupName().equals("facebook_user")) {
            login(email, getDefaultFbPassword());
        } else {
            //Redirect to the registration page if the user has no account with yet
            System.out.print("Please register an account with PayMate first.");
            redirectToAnotherPage(buildFullUri("registration.xhtml"));
        }
    }

    //Called when the "Register with Facebook" button is clicked
    //Redirects to the FB authentication page
    public void fbRegister() throws IOException {
        String callbackUri = buildFullUri("oauth_registration.xhtml");
        redirectToAuthorizationUrl(callbackUri);
    }

    public void callOnRegistrationLoaded() throws SQLException, IOException {
        String callbackUri = buildFullUri("oauth_registration.xhtml");
        String email = callOnLoaded(callbackUri);

        //If user is already registered and a FB user, login right away
        if (accountStore.checkAccountExists(email)
                && accountStore.getAccountRole(email).getGroupName().equals("facebook_user")) {
            login(email, getDefaultFbPassword());
            return;
        }

        //Insert facebook user account into the DB account table
        accountStore.insertAccount(email, getDefaultFbPassword(), "FB");

        //Manually redirect the user to the registration success page
        redirectToAnotherPage(buildFullUri("registration_success.xhtml"));
    }

    //Helper function to get the authorization url and
    //redirect users to the passed in callback url
    private void redirectToAuthorizationUrl(String callbackUri) throws IOException {
        String authorizationUrl = getService(callbackUri).getAuthorizationUrl(EMPTY_TOKEN);
        redirectToAnotherPage(authorizationUrl);
    }

    private String callOnLoaded(String callbackUri) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest codeRequest = (HttpServletRequest) context.getRequest();
        String code = codeRequest.getParameter("code");
        OAuthService service = getService(callbackUri);
        Verifier verifier = new Verifier(code);

        //Obtain access token for the FB /me call
        Token accessToken = service.getAccessToken(null, verifier);

        //Make request to FB /me passing the accessToken to authenticate
        OAuthRequest request = new OAuthRequest(Verb.GET, FB_ME_URL);
        service.signRequest(accessToken, request);

        //Get response and extract email from response JSON
        Response response = request.send();
        JsonParser parser = new JsonParser();
        JsonObject fbObject = (JsonObject) parser.parse(response.getBody());

        //Return email for authentication 
        return fbObject.get("email").toString().replace("\"", "");
    }

    //Creates the Oauth service object
    //Using the third party library Scribe
    //https://github.com/fernandezpablo85/scribe-java
    private OAuthService getService(String callbackUri) {
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

    //Login to Paymate
    public void login(String email, String password) throws SQLException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

        try {

            //Login
            request.login(email, password);
            //Update last logged in date in the Accounts table
            accountStore.updateLastLoginDate(email);
            //Redirect to the user/notifications page
            redirectToAnotherPage(buildFullUri("user/notifications.xhtml"));

        } catch (ServletException exception) {

            Logger.getLogger(LoginBean.class.getName()).log(Level.WARNING, null, exception);
            redirectToAnotherPage(buildFullUri("login_failure.xhtml"));

        }
    }

    //Helper function building full domain independent url
    private String buildFullUri(String uri) {
        Object oRequest = FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String fullUri = ((HttpServletRequest) oRequest).getRequestURL().toString();
        String[] domain = fullUri.split("/faces/");

        return domain[0] + "/faces/" + uri;
    }

    private String getDefaultFbPassword() {
        return "4_p4ym4te_u5er";
    }

    //Helper function, redirecting to passed in url
    private void redirectToAnotherPage(String url) throws IOException {
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
