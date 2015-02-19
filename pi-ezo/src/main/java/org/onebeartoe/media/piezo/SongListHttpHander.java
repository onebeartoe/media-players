
package org.onebeartoe.media.piezo;

import java.util.ArrayList;
import java.util.List;
import org.onebeartoe.media.piezo.ports.rtttl.BuiltInSongs;
import org.onebeartoe.media.piezo.ports.rtttl.RtttlSong;
import org.onebeartoe.network.ListHttpHandler;

/**
 * @author Roberto Marquez
 */
public class SongListHttpHander extends ListHttpHandler
{
    private BuiltInSongs songService;
    
    public SongListHttpHander()
    {
        songService = new BuiltInSongs();
    }
            
    @Override
    protected List<String> getList()
    {
        List<String> songList = new ArrayList();
        
        List<RtttlSong> songs = songService.getSongs();
        
        int i = 0;
        for(RtttlSong song : songs)
        {
            String entry = i + ":" + song.getTitle();
            songList.add(entry);
            
            i++;
        }
        
        return songList;
    }
}
