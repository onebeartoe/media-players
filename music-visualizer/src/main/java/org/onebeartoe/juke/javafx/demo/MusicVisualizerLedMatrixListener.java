
package org.onebeartoe.juke.javafx.demo;

import ioio.lib.api.RgbLedMatrix;
import org.onebeartoe.pixel.LedMatrixListener;
import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;

/**
 * @author Roberto Marquez
 */
public class MusicVisualizerLedMatrixListener implements LedMatrixListener
{
    private Pixel pixel;
    
    private PixelEnvironment pixelEnvironment;
    
    public MusicVisualizerLedMatrixListener(PixelEnvironment environment)
    {
        pixelEnvironment = environment;
    }

    @Override
    public Pixel getPixel()
    {
        return pixel;
    }
    
    @Override
    public void ledMatrixReady(RgbLedMatrix matrix)
    {        
        pixel = new Pixel(pixelEnvironment.LED_MATRIX, pixelEnvironment.currentResolution);
        pixel.matrix = matrix;
        
        scrollWelcomeMessage();
        
        // delay for a bit
        Sleeper.sleepo(3000);
        
        pixel.stopExistingTimer();
        
        
        
    }
    
    private void scrollWelcomeMessage()
    {
        pixel.setScrollingText("Music Visualizer");
        pixel.scrollText();
    }
}
