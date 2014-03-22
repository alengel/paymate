package converter;

import currencies.Currencies;
import currencies.ExchangeRate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author 119848
 */

@Path("conversion")
public class CurrencyConverter {
    
    Currencies currencies;
    
    public CurrencyConverter() {
        currencies = new Currencies();
    }
    
    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllCurrencies(){
        //Return all currency rates as a string
        return currencies.getRates().toString();
    }
    
    @GET
    @Path("{currencyOne}/{currencyTwo}")
    @Produces({MediaType.APPLICATION_JSON})
    public ExchangeRate getForeignCurrency(@PathParam("currencyOne") String currencyOne, 
                                         @PathParam("currencyTwo") String currencyTwo){
        
        //Return an exchange rate for the two passed in currencies
        return currencies.getExchangeRate(currencyOne, currencyTwo);
    }
    
}