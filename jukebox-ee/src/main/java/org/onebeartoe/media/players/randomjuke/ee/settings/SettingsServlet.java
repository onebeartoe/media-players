
package org.onebeartoe.media.players.randomjuke.ee.settings;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class sets up the View for the RandomJuke controls.
 * 
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/settings/*"})
public class SettingsServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        request.setAttribute("att-key", "sring-value");
                
        String dispatchLocation = 
        
        "/WEB-INF/jsp/settings/index.jsp";
                
                
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(dispatchLocation);
        rd.forward(request, response);
    }    
}
