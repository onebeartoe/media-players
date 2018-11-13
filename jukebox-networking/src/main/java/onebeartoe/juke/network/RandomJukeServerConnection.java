
package onebeartoe.juke.network;

import javax.swing.JButton;

import org.onebeartoe.multimedia.juke.services.CurrentSongService;

/**
 * This is a huge hack.  It comes from a CS networking course I took at UTSA.
 * It is here only for personal nostalgic reasons.  A not so hack-ish approach is 
 * use the com.sun.net.httpserver.HttpServer that is available in the JVM.
 * @author Roberto Marquez
 */
public class RandomJukeServerConnection extends ServerConnection
{
    private JButton nextSongButton;

    private int POST_SIZE = 1024;

    private CurrentSongService currentSongService;
    
    public RandomJukeServerConnection()
    {
        path = "/onebeartoe/juke/ui/";
    }
    
    @Override
    public String getControlsResourcePath()
    {
        return "remote-control.html";
    }
    
    @Override
    public void like(String currentSongTitle, String clientAddress)
    {
        try
        {
            currentSongService.like(currentSongTitle, clientAddress);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public String nextAction(String currentSongTitle, String clientAddress)
    {
        try
        {
            boolean next = currentSongService.next(currentSongTitle, clientAddress);
            // ?????? the code would not update with the JAR for some reason
            next = true;
            if (next)
            {
                app.playNextSong();
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return currentSongService.getCurrentSong();
    }

    public void setNextSongButton(JButton nextSongButton)
    {
        this.nextSongButton = nextSongButton;
    }

    public void setCurrentSongService(CurrentSongService currentSongService)
    {
        this.currentSongService = currentSongService;
    }
}
