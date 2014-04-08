package services;

import com.sun.xml.ws.client.ClientTransportException;
import java.util.Date;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;
import timestamp.TimestampWSService;

/**
 *
 * @author 119848
 */
@Stateless
public class TimestampService {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/paymateWSDL/TimestampWSService.wsdl")
    private TimestampWSService service;

    public TimestampService() {

    }

    //Get timestamp from paymateWS
    public Date getTimestamp() {
        try {
            timestamp.TimestampWS port = service.getTimestampWSPort();
            return port.retrieveTimestamp().toGregorianCalendar().getTime();
        } catch (ClientTransportException exception) {
            //If web service is not available, default to new Date()
            return new Date();
        }
    }
}
