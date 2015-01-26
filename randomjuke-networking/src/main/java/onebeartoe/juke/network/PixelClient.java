
package onebeartoe.juke.network;

import javax.media.Player;
import org.onebeartoe.pixel.sound.meter.AllOffSoundMeter;
import org.onebeartoe.pixel.sound.meter.BlobSoundMeter;
import org.onebeartoe.pixel.sound.meter.BottomUpSoundMeter;
import org.onebeartoe.pixel.sound.meter.CircleSoundMeter;
import org.onebeartoe.pixel.sound.meter.RectangularSoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundMeterModes;
import org.onebeartoe.pixel.sound.meter.WaveSoundMeter;

/**
 * @author Roberto Marquez
 */
public abstract class PixelClient 
{
    public static String currentSongTitle;
//    protected static String currentSongTitle;
    
    protected static int offscreenImageWidth;
    
    protected static int offscreenImageHeight;
    
    protected static SoundMeter soundMeter;
    
    // object reference used to play media
    protected static Player player;

    public void pausePlayer()
    {
        if (player != null)
        {
            player.stop();
        }
    }        

    public void setSoundMeter(SoundMeterModes meterMode)
    {
        switch(meterMode)
        {
            case BLOB:
            {
                soundMeter = new BlobSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case BOTTOM_UP:
            {
                soundMeter = new BottomUpSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case CIRCLE:
            {
                soundMeter = new CircleSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case RECTANGLE:
            {
                soundMeter = new RectangularSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case WAVE_GRAPH:
            {
                soundMeter = new WaveSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            default:
            {
                // off
                soundMeter = new AllOffSoundMeter(offscreenImageWidth, offscreenImageHeight);
            }
        }
    }    
    
    public void unpausePlayer()
    {
        if (player != null)
        {
            player.start();
        }
    }    
}
