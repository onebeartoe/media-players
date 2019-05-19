
package org.onebeartoe.juke.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import onebeartoe.juke.network.EmptyMediaListException;

/**
 * This class provides an entry point for the JavaFX media APIs.
 * 
 * The static methods wrap calls to the actual RandomJuke object.
 * 
 * @author Robert Marquez
 */
public class JukeMain extends Application 
{
    private static RandomJuke randomJuke;

    public static boolean isInitiiaized() 
    {
        // at this point, so long as the randomjuke object is not null, then it 
        // is considered initialized
        
        return randomJuke != null;
    }

    public static String discoverSongLists() 
    {
        String result;
        
        if(randomJuke == null)
        {
            result = "cannot discover media lists while randomJuke is null";
        }
        else
        {
            List<String> songListUrls = new ArrayList();
            songListUrls.add("file:///Users/lando/Versioning/world/betoland-world/music/Unorganized/");
            
            randomJuke.setSongListUrls(songListUrls);
            
            result = "song lists discovered";
        }
        
        return result;
    }
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        Parameters parameters = getParameters();
        List<String> raw = parameters.getRaw();
        String [] args = raw.toArray( new String[0] );
        
        randomJuke = new RandomJuke(args);
        
        randomJuke.printStartDescription();
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
    
    public static String nextSong()
    {
        String message;
        
        try 
        {
            randomJuke.playNextSong();
            
            message = randomJuke.currentSongTitle;
        } 
        catch (EmptyMediaListException ex) 
        {
            Logger.getLogger(JukeMain.class.getName()).log(Level.SEVERE, null, ex);
            
            message = "ERROR-21720: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();
        }
        
        return message;
    }

    public static void shutdown()
    {
//TODO: is there anything to do here?

//        controller.stopThreads();
    }        
}
