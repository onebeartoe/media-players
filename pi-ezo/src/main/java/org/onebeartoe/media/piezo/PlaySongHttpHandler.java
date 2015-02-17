
package org.onebeartoe.media.piezo;

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
    private RtttlService rtttlService = new RtttlService();
    
    private Logger logger;
    
    public PlaySongHttpHandler()
    {
        logger = Logger.getLogger(getClass().getName());
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
            rtttlService.playSong(id);
            response += "okay";
        } 
        catch (InterruptedException ex)
        {
            response += "not okay";
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        return response;
    }
    
}
