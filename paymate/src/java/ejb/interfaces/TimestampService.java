package ejb.interfaces;

import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author 119848
 */

@Local
public interface TimestampService {
    public Date getTimestamp();
}
