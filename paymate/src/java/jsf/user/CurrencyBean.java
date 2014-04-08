package jsf.user;

import com.google.gson.Gson;
import services.CurrencyService;
import java.io.Serializable;
import java.util.HashMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author 119848
 */
//Utility class shared across the JSF views to display currency related information
@Named
@SessionScoped
public class CurrencyBean implements Serializable {

    public CurrencyBean() {
    }

    //Return all currencies in a hashmap to fill the currency table
    public HashMap<String, HashMap<String, Float>> getCurrencies() {
        Gson gson = new Gson();
        String currencies = CurrencyService.getCurrencies();
        HashMap<String, HashMap<String, Float>> currenciesMap = new HashMap<String, HashMap<String, Float>>();
        currenciesMap = (HashMap<String, HashMap<String, Float>>) gson.fromJson(currencies, currenciesMap.getClass());
        return currenciesMap;
    }

    //Get currency symbol for the passed in currency
    public String changeCurrencyStringToSymbol(String currencyString) {
        String currencySymbol;

        switch (currencyString) {
            case "GBP":
                currencySymbol = "£";
                break;
            case "USD":
                currencySymbol = "$";
                break;
            case "EUR":
                currencySymbol = "€";
                break;
            default:
                currencySymbol = "Invalid currency";
                break;
        }
        return currencySymbol;
    }

}
