package ejb.beans;

import com.sun.xml.ws.client.ClientTransportException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import jsf.shared.beans.LoginBean;
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
        
        try {
            timestamp.TimestampWS port = service.getTimestampWSPort();
            return port.retrieveTimestamp().toGregorianCalendar().getTime();
        } catch (ClientTransportException exception) {
            //If web service is not available, default to new Date() here.
            Logger.getLogger(TimestampServiceBean.class.getName()).log(Level.SEVERE, null, exception);
            return new Date();
        }
    }
     
}