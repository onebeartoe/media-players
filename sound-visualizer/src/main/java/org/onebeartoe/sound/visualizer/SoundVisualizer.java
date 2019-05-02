
package org.onebeartoe.sound.visualizer;

import ioio.lib.api.AnalogInput;

import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import onebeartoe.juke.network.ThreadedServer;
import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.pixel.sound.meter.AllOffSoundMeter;
import org.onebeartoe.pixel.sound.meter.BlobSoundMeter;
import org.onebeartoe.pixel.sound.meter.BottomUpSoundMeter;
import org.onebeartoe.pixel.sound.meter.CircleSoundMeter;
import org.onebeartoe.pixel.sound.meter.RectangularSoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundReading;
import org.onebeartoe.pixel.sound.meter.WaveSoundMeter;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import onebeartoe.juke.network.JukeClient;
import onebeartoe.juke.network.ServerConnection;
import org.onebeartoe.pixel.sound.meter.SoundMeterModes;
import static org.onebeartoe.pixel.sound.meter.SoundMeterModes.BLOB;
import static org.onebeartoe.pixel.sound.meter.SoundMeterModes.BOTTOM_UP;
import static org.onebeartoe.pixel.sound.meter.SoundMeterModes.CIRCLE;
import static org.onebeartoe.pixel.sound.meter.SoundMeterModes.RECTANGLE;
import static org.onebeartoe.pixel.sound.meter.SoundMeterModes.WAVE_GRAPH;

/**
 * @author Roberto Marquez
 */
public class SoundVisualizer extends JukeClient 
{
    private static final long serialVersionUID = 178947923L;

    private ThreadedServer server;
    
    private static PixelIntegration pixelIntegration;
    
    private static IOIO ioiO;

    private static PixelEnvironment pixelEnvironment;

    private static int offscreenImageHeight;
    
    private static int offscreenImageWidth;
    
    private static Pixel pixel;
    
    private static AnalogInput microphoneSensor;
    
    private static volatile List<SoundReading> microphoneValues;
    
    private static int SAMPLE_BUFFER_SIZE = 50;
    
    public static SoundMeter soundMeter;
        
    /**
     * Setup the application.
     */
    public SoundVisualizer()
    {        
        microphoneValues = new ArrayList();

        ServerConnection serverConnection = new SoundVisualizerServerConnection();
        serverConnection.setApp(this);
        
        server = new ThreadedServer();
        server.setPort(2014);
        server.setConnection(serverConnection);
        server.start();
    }
    
    public void setSoundMeter(SoundMeterModes meterMode)
    {
        switch(meterMode)
        {
            case BLOB:
            {
                soundMeter = new BlobSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case BOTTOM_UP:
            {
                soundMeter = new BottomUpSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case CIRCLE:
            {
                soundMeter = new CircleSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case RECTANGLE:
            {
                soundMeter = new RectangularSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            case WAVE_GRAPH:
            {
                soundMeter = new WaveSoundMeter(offscreenImageWidth, offscreenImageHeight);
                break;
            }
            default:
            {
                // off
                soundMeter = new AllOffSoundMeter(offscreenImageWidth, offscreenImageHeight);
            }
        }
    }

    /**
     * main program to call the JFrame to the screen
     */
    public static void main(String args[])
    {
        System.out.println("Application starting up...");
                
        final SoundVisualizer app = new SoundVisualizer();
        
        try
        {
            pixelIntegration = new PixelIntegration();
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void playNextSong()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class PixelIntegration extends IOIOConsoleApp
    {
        public PixelIntegration()
        {
            try
            {
                Date now = new Date();
                Timer timer = new Timer();
                TimerTask task = new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        if(soundMeter == null)
                        {

                        }
                        else
                        {
                            List<SoundReading> values = new ArrayList();
                            values.addAll(microphoneValues);
                            soundMeter.displaySoundData(pixel, values);
                        }
                    }
                };
                        
                // this is how often the image will be sent to the Pixel
                long refreshRate = 200;
                refreshRate = 1;
                
                timer.schedule(task, now, refreshRate);
                System.out.println("SCHEDULED DISPLAY TASK");
                
                System.out.println("CALLING GO");
                go(null);
            } 
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        @Override
        protected void run(String[] args) throws IOException 
        {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(isr);
            boolean abort = false;
            String line;
            while (!abort && (line = reader.readLine()) != null) 
            {
                if (line.equals("t")) 
                {
                    //ledOn_ = !ledOn_;
                } 
                else if (line.equals("q")) {
                    abort = true;
                    System.exit(1);
                } 
                else 
                {
                    System.out.println("Unknown input. q=quit.");
                }
            }
        }

        @Override
        public IOIOLooper createIOIOLooper(String connectionType, Object extra)
        {
            IOIOLooper looper = new BaseIOIOLooper() 
            {
            
                @Override
                public void disconnected() 
                {
                    String message = "PIXEL was Disconnected";
                    System.out.println(message);
                }

                @Override
                public void incompatible() 
                {
                    String message = "Incompatible Firmware Detected";
                    System.out.println(message);
                }

                /**
                 * Pixel types:
                 * 1: 32x16 from Sparkfun - ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16
                 * 3: 32x32 from Adafruit (original one) - ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //the current version of PIXEL 32x32
                 * @throws ConnectionLostException
                 * @throws InterruptedException 
                 */
                @Override
                protected void setup() throws ConnectionLostException,
                        InterruptedException 
                {
                    ioiO = ioio_;
                    
                    int pixleType = 3;
                    pixelEnvironment = new PixelEnvironment(pixleType);
                    
                    offscreenImageHeight = pixelEnvironment.KIND.height * 2;
                    offscreenImageWidth = pixelEnvironment.KIND.width * 2;
                    RgbLedMatrix ledMatrix = ioio_.openRgbLedMatrix(pixelEnvironment.KIND);                    
                    pixel = new Pixel(pixelEnvironment.KIND, pixelEnvironment.currentResolution);
                    pixel.matrix = ledMatrix;
                    pixel.ioiO = ioio_;
                    microphoneSensor = Pixel.getAnalogInput1();
                    microphoneSensor.setBuffer(SAMPLE_BUFFER_SIZE);
  
                    // start off with this sound meter mode
                    soundMeter = new AllOffSoundMeter(offscreenImageWidth, offscreenImageHeight);
                    soundMeter = new RectangularSoundMeter(offscreenImageWidth, offscreenImageHeight);
                            
                    System.out.println("Found PIXEL: " + pixel.matrix + "\n");                     
                }

                @Override
                public void loop() throws ConnectionLostException,
                        InterruptedException 
                {
                    float p = microphoneSensor.readBuffered();

                    float ratio = offscreenImageHeight * p;
                    int height = (int) ratio;

                    Color c = BottomUpSoundMeter.randomcolor();
                    
                    SoundReading reading = new SoundReading();
                    reading.height = height;
                    reading.color = c;
                    
                    microphoneValues.add(reading);
                    
                    if(microphoneValues.size() > SAMPLE_BUFFER_SIZE)
                    {
                        microphoneValues.remove(0);
                    }
                }
            };
                    
            return looper;
        }        
    }
}
