
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
        
        logger = Logger.getLogger( getClass().getName() );
   
        logger.log(Level.INFO, "loading the scoreboard");

        Application.launch(JukeMain.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        request.setAttribute("att-key", "sring-value");
                
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }
}
