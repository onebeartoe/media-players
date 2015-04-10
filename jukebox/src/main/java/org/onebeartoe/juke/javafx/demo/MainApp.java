package org.onebeartoe.juke.javafx.demo;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.onebeartoe.network.ClasspathResourceHttpHandler;


public class MainApp extends Application 
{
    private static MediaPlayer mediaPlayer;
    
    private HttpServer server;

    @Override
    public void start(Stage stage) throws Exception 
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
                        mediaPlayer.stop();
//                        System.exit(0);
                    }
                });
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.println("launching...");
        
        launch(args);
        
        System.out.println("launched!");
    }
    
    public void startServer()
    {
        System.out.println("starting juke server.");
        server.start();
        System.out.println("juke server started");
    }
}
