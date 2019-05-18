
package org.onebeartoe.media.players.randomjuke.ee.controls.current.song;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.juke.ui.JukeMain;
import org.onebeartoe.web.PlainTextResponseServlet;

/**
 * This class process requests to play the next song.
 * 
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/controls/song/next"})
public class NextSongServlet extends PlainTextResponseServlet
{
    @Override
    protected String buildText(HttpServletRequest request, HttpServletResponse response)
    {
        return JukeMain.nextSong();
    }
}



