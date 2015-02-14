
package org.onebeartoe.media.piezo.ports.rtttl;

/**
 * This class encapsulates a Ring Tone Text Transfer Language object.
 * 
 * @author Roberto Marquez
 */
public class RtttlSong
{
   private String data;
   
   public RtttlSong(String data)
   {
       this.data = data;
   }
   
   public String getTitle()
   {
       int i = data.indexOf(":");
       
       String title = data.substring(0, i);
       
       return title;
   }
}
