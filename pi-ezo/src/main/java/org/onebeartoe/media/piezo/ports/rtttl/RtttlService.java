
package org.onebeartoe.media.piezo.ports.rtttl;

import java.util.List;

/**
 * This class uses the built in RTTTL songs to play ring tones on a piezo buzzer.
 * 
 * @author Roberto Marquez
 */
public class RtttlService
{
   BuiltInSongs builtInSongs = new BuiltInSongs();
   
   public void playSong(int id) throws InterruptedException
   {
       List<RtttlSong> songs = builtInSongs.getSongs();
       
       RtttlSong song = songs.get(id);
       
       RingToneTextTransferLanguage app = new RingToneTextTransferLanguage();
       String data = song.getData();
       
       app.play_rtttl(data);
   }
}
