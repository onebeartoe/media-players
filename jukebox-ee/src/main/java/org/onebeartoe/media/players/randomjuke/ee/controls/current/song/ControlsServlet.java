
package org.onebeartoe.media.players.randomjuke.ee.controls.current.song;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.juke.ui.JukeMain;

/**
 * This class sets up the View for the RandomJuke controls.
 * 
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/controls/*"})
public class ControlsServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        request.setAttribute("att-key", "sring-value");

        String dispatchLocation = "/WEB-INF/jsp/controls/initialize/index.jsp";
        
        if( JukeMain.isInitiiaized() )
        {
            dispatchLocation = "/WEB-INF/jsp/controls/index.jsp";
        }

        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(dispatchLocation);
        rd.forward(request, response);
    }    
}
