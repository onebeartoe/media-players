
package org.onebeartoe.media;

import java.util.ArrayList;
import java.util.List;
import org.onebeartoe.network.ListHttpHandler;

/**
 * @author Roberto Marquez
 */
public class SongListHttpHander extends ListHttpHandler
{
    @Override
    protected List<String> getList()
    {
        List<String> songs = new ArrayList();
        
        songs.add("song 1");
        songs.add("song b");
        songs.add("song 9");
        songs.add("song TTTT");
        
        return songs;
    }
}
