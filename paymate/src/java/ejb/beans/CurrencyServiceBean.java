package ejb.beans;

import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author 119848
 */

@Stateless
public class CurrencyServiceBean {
    
    public CurrencyServiceBean(){}
    
    private static String makeRequest(String uri){
        WebTarget target = ClientBuilder.newClient().target(uri);
        Response response = target.request().get();

        if (response.getStatus() == 200) {
            return target.request(MediaType.APPLICATION_JSON)
                         .get(String.class);
        } else {
            return "failed";
        }
    }
    
    public static String[] getAvailableCurrencies(){
        String uri = "http://localhost:8080/paymateRS/conversion/available";
        
        String result = makeRequest(uri);
        if (result.equals("failed")) {
            return null;
        } else {
            String[] currencies = result.replace("[", "")
                                        .replace("]", "")
                                        .split(", ");

            return currencies;
        }
    }

    //Get all currency conversion rates from paymateRS
    public static String getCurrencies(){
        String uri = "http://localhost:8080/paymateRS/conversion/all";
        
        String result = makeRequest(uri);
        
        if (result.equals("failed")) {
            return null;
        } else {
            return result;
        }
    }
    
    //Pass in local and foreign currency, plus value to get the converted amount
    public static Float getConvertedAmount(String localCurrency, String foreignCurrency, float value){
        String amountString = Float.toString(value);
        String uri = "http://localhost:8080/paymateRS/conversion/" +
                                 localCurrency + "/" + foreignCurrency + "/" + amountString;
        
        String result = makeRequest(uri);
        
        if (result.equals("failed")) {
            return null;
        } else {
            return Float.parseFloat(result);
        }
    }
}
