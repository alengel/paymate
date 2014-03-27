package ejb.interfaces;

import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface CurrencyService {
    public String getCurrencies();
}
