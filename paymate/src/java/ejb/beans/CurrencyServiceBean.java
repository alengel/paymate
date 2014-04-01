package ejb.beans;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author 119848
 */

@Stateless
public class CurrencyServiceBean {
    
    public CurrencyServiceBean(){}
    
    public static String[] getAvailableCurrencies(){
        Client client = ClientBuilder.newClient();
        String currenciesString = client.target("http://localhost:8080/paymateRS/conversion/available")
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);
        
        String[] currencies = currenciesString.replace("[", "")
                                              .replace("]", "")
                                              .split(", ");
        
        return currencies;
    }

    //Get all currency conversion rates from paymateRS
    public static String getCurrencies(){
        Client client = ClientBuilder.newClient();
        String currencies = client.target("http://localhost:8080/paymateRS/conversion/all")
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return currencies;
    }
    
    //Pass in local and foreign currency, plus value to get the converted amount
    public static Float getConvertedAmount(String localCurrency, String foreignCurrency, float value){
        String amountString = Float.toString(value);
        
        Client client = ClientBuilder.newClient();
        String convertedAmount = client.target("http://localhost:8080/paymateRS/conversion/" +
                                 localCurrency + "/" + foreignCurrency + "/" + amountString)
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return Float.parseFloat(convertedAmount);
    }
}
