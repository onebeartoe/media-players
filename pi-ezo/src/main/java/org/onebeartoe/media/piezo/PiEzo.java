
package org.onebeartoe.media.piezo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.onebeartoe.network.ClasspathResourceHttpHandler;
import org.onebeartoe.network.TextHttpHandler;

//TODO: rename this class to PiEzoWebApp
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
        InetSocketAddress anyhost = new InetSocketAddress(2110);        
        server = HttpServer.create(anyhost, 0);
        
        HttpHandler userInterfaceHttpHander = new ClasspathResourceHttpHandler();
        HttpHandler songsHttpHandler = new SongListHttpHander();
        HttpHandler playSongHttpHandler = new PlaySongHttpHandler();
        HttpHandler quitHttpHandler = new TextHttpHandler() 
        {
            @Override
            protected String getHttpText(HttpExchange he) 
            {
                try
                {
                    System.out.println("stopping the piezo server");
                    // wait time is in seconds
                    int maxWaitTime = 5; 
                    server.stop(maxWaitTime);
                    
                    System.out.println("piezo server stopped");
                    
                    Thread.sleep(maxWaitTime + 1000);
                    
                    System.exit(0);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    return "end of run approaching...";    
                }
            }
        };
        HttpHandler rtttlHttpHandler = new RtttlHttpHandler();
        
                
        server.createContext("/", userInterfaceHttpHander);
        server.createContext("/play/", playSongHttpHandler);
        server.createContext("/quit", quitHttpHandler);
        server.createContext("/rtttl", rtttlHttpHandler);
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
