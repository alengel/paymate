package payments;

import com.google.gson.Gson;
import ejb.currencies.CurrencyStorageServiceBean;
import ejb.payment.PaymentStorageServiceBean;
import java.io.Serializable;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author 119848
 */

@Named
@RequestScoped
public class CurrencyBean implements Serializable{
    
    @EJB
    private CurrencyStorageServiceBean currencyStore;
    
    public HashMap<String,HashMap<String, Float>> getCurrencies(){
        Gson gson = new Gson(); 
        String json = currencyStore.getCurrencies();
        System.out.print(json);
        HashMap<String,HashMap<String, Float>> map = new HashMap<String,HashMap<String, Float>>();
        map = (HashMap<String,HashMap<String, Float>>) gson.fromJson(json, map.getClass());
        System.out.print(map);
        return map;
    }
    
}
