/*
 */
package org.onebeartoe.media.piezo.ports.rtttl;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import static org.onebeartoe.media.piezo.ports.rtttl.RtttlSong.TITLE_LENGTH_MAX;

/**
 *
 * @author Roberto Marquez
 */
public class BuiltInSongsTest
{
    private static BuiltInSongs builtInSongs;

    public BuiltInSongsTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        builtInSongs = new BuiltInSongs();
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getSongs method, of class BuiltInSongs.
     */
    @org.junit.Test
    public void testGetSongs()
    {
        List<RtttlSong> result = builtInSongs.getSongs();
        assertNotNull(result);
        
        int size = result.size();
        assertTrue(size > 0);
    }
    
    @org.junit.Test
    public void testTitleLengths()
    {
        List<RtttlSong> result = builtInSongs.getSongs();
        
        for(RtttlSong song : result)
        {
            String title = song.getTitle();
            assertNotNull(title);

            int length = title.length();
            assertTrue(length != 0);
            
            assertTrue(length <= TITLE_LENGTH_MAX);
        }
    }
}
