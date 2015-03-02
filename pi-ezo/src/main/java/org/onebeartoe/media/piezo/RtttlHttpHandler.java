
package org.onebeartoe.media.piezo;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;

/**
 * @author Roberto Marquez
 */
public class RtttlHttpHandler extends PlaySongHttpHandler
{
    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        URI requestURI = exchange.getRequestURI();

        String rtttl = requestURI.getQuery();
        
        System.out.println("playing: " + rtttl);
        
        rtttlService.playSong(rtttl);
        
        String response = "song played: " + rtttl;
        
        System.out.println(response);
        
        return response;
    }
}
