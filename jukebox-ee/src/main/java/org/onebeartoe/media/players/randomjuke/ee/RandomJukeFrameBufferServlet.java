package org.onebeartoe.media.players.randomjuke.ee;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.juke.ui.JukeMain;
import org.onebeartoe.system.Sleeper;

/**
 * This servlet initializes the JavaFX components used by the media player
 * service.
 */
@WebServlet(urlPatterns = {"/frame-buffer-screen"})
public class RandomJukeFrameBufferServlet extends HttpServlet 
{
    private Logger logger;

    @Override
    public void destroy() 
    {
        JukeMain.shutdown();
    }

    @Override
    public void init() throws ServletException 
    {
        super.init();

        logger = Logger.getLogger(getClass().getName());

        // start the secondary initialization first, but with a delay to allow the
        // call to launch() to happen first
        Thread mediaListInitializer = new Thread(()
                -> {
            System.out.println("media list initializer: sleep entry");

            // delay a bit before calling discoverSongLists()
            Sleeper.sleepo(1000 * 20);

            System.out.println("media list initializer: exit sleep");

            String result = JukeMain.discoverSongLists();

            System.out.println("media list initializer: " + result);
        });
        mediaListInitializer.start();

        logger.log(Level.INFO, "loading the randomjuke serverside");

        Application.launch(JukeMain.class);

        // the launch() method blocks and we never actually get here when expected
        logger.log(Level.INFO, "task compleste: load the randomjuke serverside");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("att-key", "sring-value");

        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }
}
