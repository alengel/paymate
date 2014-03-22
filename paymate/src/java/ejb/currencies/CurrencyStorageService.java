package ejb.currencies;

import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface CurrencyStorageService {
    public String getCurrencies();
}
