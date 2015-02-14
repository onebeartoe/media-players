
package org.onebeartoe.media;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.onebeartoe.network.ClasspathResourceHttpHandler;

/**
 * This class starts an HTTP server, listens for requests to manage playing 
 * the piezo, and provides metadata about the application.
 * 
 * @author Roberto Marquez
 */
public class PiEzo
{
    private HttpServer server;
    
    public PiEzo() throws IOException
    {        
        HttpHandler userInterfaceHttpHander = new ClasspathResourceHttpHandler();
        HttpHandler songsHttpHandler = new SongListHttpHander();
        
        InetSocketAddress anyhost = new InetSocketAddress(2110);        
        server = HttpServer.create(anyhost, 0);        
        server.createContext("/", userInterfaceHttpHander);
        server.createContext("/songs", songsHttpHandler);
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
