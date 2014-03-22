package currencies;

import java.util.HashMap;

/**
 *
 * @author 119848
 */

public class Currencies {
    
    private final HashMap<String, HashMap<String, Float>> rates;
    
    public Currencies() {
        //Create a HashMap that stores all the currency rates
        rates = new HashMap<>();
        
        //USD rates
        HashMap usdRates = new HashMap<>();
        usdRates.put("EUR", 0.72f);
        usdRates.put("GBP", 0.60f);
        
        rates.put("USD", usdRates);
        
        // EUR rates        
        HashMap eurRates = new HashMap<>();
        eurRates.put("USD", 1.38f);
        eurRates.put("GBP", 0.83f);
        
        rates.put("EUR", eurRates);
        
        // GBP rates        
        HashMap gbpRates = new HashMap<>();
        gbpRates.put("USD", 1.65f);
        gbpRates.put("EUR", 1.19f);
        
        rates.put("GBP", gbpRates);
    }

    //Return all rates at once
    public HashMap<String, HashMap<String, Float>> getRates() {
        return rates;
    }
    
    //Return an ExchangeRate object with the specifiedRate
    public ExchangeRate getExchangeRate(String from, String to){
        HashMap<String, Float> rate = rates.get(to);
        Float convertedRate = rate.get(from);

        return new ExchangeRate(from, to, convertedRate);
    }
    
    //Return the converted rate
    public Float getConvertedAmount(String from, String to, String value){
        HashMap<String, Float> localRate = rates.get(from);
        Float foreignRate = localRate.get(to);
        Float convertedRate = foreignRate * Float.parseFloat(value);;
                
        return convertedRate;
    }
}
