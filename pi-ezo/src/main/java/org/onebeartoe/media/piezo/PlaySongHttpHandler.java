
package org.onebeartoe.media.piezo;

import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.onebeartoe.media.piezo.ports.rtttl.RtttlService;
import org.onebeartoe.network.TextHttpHandler;

/**
 *
 * @author Roberto Marquez
 */
public class PlaySongHttpHandler extends TextHttpHandler
{
    protected RtttlService rtttlService;
    
    private Logger logger;
    
    public PlaySongHttpHandler()
    {
        logger = Logger.getLogger(getClass().getName());
        try 
        {
            System.out.println("Hostname          :  " + NetworkInfo.getHostname());
            
            for (String ipAddress : NetworkInfo.getIPAddresses())
            {
                System.out.println("IP Addresses      :  " + ipAddress);
            }
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(PlaySongHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String osName = SystemInfo.getOsName();
        System.out.println("OS Name           :  " + osName);
        if(osName.contains("Mac") ||
                osName.contains("Windows"))
        {
            System.out.println("The application is NOT running on Raspberry Pi.");
        }
        else
        {
            rtttlService = new RtttlService();
        }
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        
        int i = path.lastIndexOf("/") + 1;
        String s = path.substring(i);
        int id = Integer.valueOf(s);
        
        String response = "play song " + id + ": ";
        try
        {
            if(rtttlService == null)
            {
                System.out.println("rtttlService is null.  Is it instanciated in the constuctor?");
            }
            else
            {
                rtttlService.playSong(id);
                response += "okay";                
            }
        } 
        catch (Exception ex)
        {
            response += "not okay";
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        return response;
    }
    
}
