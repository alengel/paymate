/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    Timestamp timestamp;
    
    public TimestampWS(){
        timestamp = new Timestamp();
    }
    
    @WebMethod(operationName="retrieveTimestamp")
    public Timestamp retrieveTimestamp(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        timestamp = dateFormat.format(timestamp);
        
        System.out.println(timestamp);
        return timestamp;
    }
}
