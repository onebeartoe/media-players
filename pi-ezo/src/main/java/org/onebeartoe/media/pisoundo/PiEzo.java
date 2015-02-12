
package org.onebeartoe.media.pisoundo;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This class is a direct port from C to Java of the program listing found here:
 * 
 *      http://computers.tutsplus.com/articles/creating-a-speaker-for-your-raspberry-pi-using-a-piezo-element--mac-59336
 * 
 * @author Roberto Marquez
 */
public class PiEzo
{
    private HttpServer server;
    
    public PiEzo() throws IOException
    {
        InetSocketAddress anyhost = new InetSocketAddress(211);        
        server = HttpServer.create(anyhost, 0);
        
        HttpHandler dittyHttpHander = new DittyHttpHander();
        server.createContext("/ditty", dittyHttpHander);
    }
    
    public static void main(String[] args) throws InterruptedException, IOException
    {
        PiEzo app = new PiEzo();
        app.startServer();
    }
    
    public void startServer()
    {
        server.start();
    }
}
