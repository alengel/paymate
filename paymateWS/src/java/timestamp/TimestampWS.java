package timestamp;

import java.util.Date;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author 119848
 */

@WebService
@Stateless
public class TimestampWS {
    Date timestamp;
    
    public TimestampWS(){
        timestamp = new Date();
    }
    
    @WebMethod(operationName="retrieveTimestamp")
    public Date retrieveTimestamp(){
        return timestamp;
    }
}
