
package onebeartoe.juke.network;

import javafx.scene.media.MediaPlayer;

/**
 * @author Roberto Marquez
 */
public abstract class JukeClient 
{
    // object reference used to play media
    protected static MediaPlayer mediaPlayer;
    
    public void pausePlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
    }
    
    public void unpausePlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.play();
        }
    }    
}
