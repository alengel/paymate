package jsf.beans;

import com.google.gson.Gson;
import ejb.beans.CurrencyStorageServiceBean;
import java.io.Serializable;
import java.util.HashMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@SessionScoped
public class CurrencyBean implements Serializable{
    
    public CurrencyBean(){}
      
    public HashMap<String,HashMap<String, Float>> getCurrencies(){
        Gson gson = new Gson(); 
        String currencies = CurrencyStorageServiceBean.getCurrencies();
        HashMap<String,HashMap<String, Float>> currenciesMap = new HashMap<String,HashMap<String, Float>>();
        currenciesMap = (HashMap<String,HashMap<String, Float>>) gson.fromJson(currencies, currenciesMap.getClass());
        return currenciesMap;
    }
    
    public String changeCurrencyStringToSymbol(String currencyString){
        String currencySymbol;
        
        switch (currencyString) {
            case "GBP":  currencySymbol = "£";
                     break;
            case "USD":  currencySymbol = "$";
                     break;
            case "EUR":  currencySymbol = "€";
                     break;
            default: currencySymbol = "Invalid currency";
                     break;            
        }
        return currencySymbol;
    }
    
    public float calculateAmountInChosenCurrency(String localCurrency, String foreignCurrency, float amount){
        String convertedAmount = CurrencyStorageServiceBean.getConvertedAmount(localCurrency, foreignCurrency, amount);
        return Float.parseFloat(convertedAmount);
    }
    
}
