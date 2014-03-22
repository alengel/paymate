package ejb.currencies;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author 119848
 */

@Stateless
public class CurrencyStorageServiceBean {
    
    //Get currency rates from paymateRS
    public String getCurrencies(){
        Client client = ClientBuilder.newClient();
        String currencies = client.target("http://localhost:8080/paymateRS/conversion/all")
          .request(MediaType.APPLICATION_JSON)
          .get(String.class);

        return currencies;
    }
}
