package ejb.currencies;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author 119848
 */

@Stateless
public class CurrencyStorageServiceBean {
    
    public CurrencyStorageServiceBean(){}
    
    //Get currency rates from paymateRS
    public static String getCurrencies(){
        Client client = ClientBuilder.newClient();
        String currencies = client.target("http://localhost:8080/paymateRS/conversion/all")
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return currencies;
    }
    
    public static String getConvertedAmount(String localCurrency, String foreignCurrency, float value){
        String amountString = Float.toString(value);
        
        Client client = ClientBuilder.newClient();
        String convertedAmount = client.target("http://localhost:8080/paymateRS/conversion/" +
                                 localCurrency + "/" + foreignCurrency + "/" + amountString)
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return convertedAmount;
    }
}
