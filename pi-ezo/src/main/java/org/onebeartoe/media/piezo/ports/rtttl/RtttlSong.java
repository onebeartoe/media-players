
package org.onebeartoe.media.piezo.ports.rtttl;

/**
 * This class encapsulates a Ring Tone Text Transfer Language object.
 * 
 * @author Roberto Marquez
 */
public class RtttlSong
{
    public static final int TITLE_LENGTH_MAX = 12;

    private String data;

    public RtttlSong(String data)
    {
        this.data = data;
    }
        
    public String getData()
    {
        return data;
    }
   
    public String getTitle()
    {
       int i = data.indexOf(":");
       
       String title = data.substring(0, i);
       
       return title;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
}
