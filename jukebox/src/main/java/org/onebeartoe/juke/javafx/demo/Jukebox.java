

















//TODO: THE CLASS NAMED MainApp HAS THE WORKING CODE!!!
package org.onebeartoe.juke.javafx.demo;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.onebeartoe.network.ClasspathResourceHttpHandler;

/**
 * @author Roberto Marquez
 */
public class Jukebox
{
    private static MediaPlayer mediaPlayer;
    
    private HttpServer server;

    
    public Jukebox(String [] args) throws IOException
    {
        final String audio2 = "file:///C:/home/world/sounds/Field-cricket-species-Spanish.mp3";
        final String audio1 = "file:///C:/home/world/sounds/Price-Is-Right-loser-sound.mp3";
        
        System.out.println("Input songs:\n" + audio1 + "\n" + audio2 + "\n");
        
        InetSocketAddress anyhost = new InetSocketAddress(2110);        
        server = HttpServer.create(anyhost, 0);

        HttpHandler userInterfaceHttpHander = new ClasspathResourceHttpHandler();
        server.createContext("/", userInterfaceHttpHander);
        startServer();
        
        final Media media = new Media(audio1);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnError(new Runnable() 
        {
            @Override public void run() {
                System.out.println("mediaPlayer.getError() = " + mediaPlayer.getError());
            }
        });
        mediaPlayer.setOnEndOfMedia( new Runnable() 
        {
            @Override
            public void run() 
            {                
                mediaPlayer.stop();
                
                System.out.println("playing the second audio file...");
                Media media = new Media(audio2);
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setOnError(new Runnable() 
                {
                    @Override public void run() 
                    {
                        System.out.println("mediaPlayer.getError() = " + mediaPlayer.getError());
                    }
                });
                mediaPlayer.setOnEndOfMedia( new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        System.out.println("end of second audio fiile reached");
                    }
                });
            }
        });
    }
    
    public static void main(String [] args) //throws IOException
    {
        try        
        {
            Jukebox app = new Jukebox(args);
//        app.startServer();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Jukebox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startServer()
    {
        System.out.println("starting juke server...");
        server.start();
        System.out.println("juke server started");
    }
}
