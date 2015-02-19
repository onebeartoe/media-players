
package org.onebeartoe.media.piezo.ports.rtttl;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftTone;
import java.util.List;
import static org.onebeartoe.media.piezo.ports.softtone.SoftTonePort.PIEZO_PIN;

/**
 * This class uses the built in RTTTL songs to play ring tones on a piezo buzzer.
 * 
 * @author Roberto Marquez
 */
public class RtttlService
{
   BuiltInSongs builtInSongs = new BuiltInSongs();
   
   public RtttlService()
   {
       Gpio.wiringPiSetup();
 
//       SoftTone.softToneCreate(PIEZO_PIN);
   }
   
   public void playSong(int id) throws InterruptedException
   {
       List<RtttlSong> songs = builtInSongs.getSongs();
       
       RtttlSong song = songs.get(id);
       
       RingToneTextTransferLanguage app = new RingToneTextTransferLanguage();
       String data = song.getData();
  
       SoftTone.softToneCreate(PIEZO_PIN);
       
       try
       {
           app.play_rtttl(data);
       }
       catch(Exception e)
       {
          e.printStackTrace();
       }
       finally
       {
               SoftTone.softToneStop(PIEZO_PIN);
        }
   }
}
