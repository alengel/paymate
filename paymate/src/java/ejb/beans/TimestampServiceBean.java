package ejb.beans;

import java.util.Date;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import timestamp.TimestampWSService;

/**
 *
 * @author 119848
 */

@Stateless
public class TimestampServiceBean {
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/TimestampWSService/TimestampWS.wsdl")
    private TimestampWSService service;
    
    public TimestampServiceBean() {
        
    }
    
    //Get timestamp from paymateWS
    public Date getTimestamp() {
        timestamp.TimestampWS port = service.getTimestampWSPort();
        return port.retrieveTimestamp().toGregorianCalendar().getTime();
    }
    
}