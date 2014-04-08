package ejb.interfaces;

import java.util.Date;
import javax.ejb.Local;
import javax.ejb.Remote;

/**
 *
 * @author 119848
 */

@Remote
public interface TimestampService {
    public Date getTimestamp();
}
