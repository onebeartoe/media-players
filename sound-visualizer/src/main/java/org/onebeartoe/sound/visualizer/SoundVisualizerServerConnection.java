
package org.onebeartoe.sound.visualizer;

import onebeartoe.juke.network.ServerConnection;

/**
 * @author Roberto Marquez
 */
public class SoundVisualizerServerConnection extends ServerConnection
{
    public SoundVisualizerServerConnection()
    {
        path = "/";
    }

    @Override
    public String getControlsResourcePath()
    {
        return "controls.html";
    }

    @Override
    public void like(String currentSongTitle, String clientAddress)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void nextAction(String currentSongTitle, String clientAddress)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
