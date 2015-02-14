
package org.onebeartoe.media.piezo.ports.softtone;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftTone;

/**
 * This class is a direct port from C to Java of the program listing found here:
 * 
 *      http://computers.tutsplus.com/articles/creating-a-speaker-for-your-raspberry-pi-using-a-piezo-element--mac-59336
 * 
 * This seemed to work, but only had a few other song examples.  See the RTTTL implementation.
 * 
 * The C code has been commented out just below the Java equivalent.
 * @author Roberto Marquez
 */
public class SoftTonePort
{
    public static final int PIEZO_PIN = 3;
//      #define PIEZO_PIN 3
        
    public static void main(String[] args) throws InterruptedException
    {
        Gpio.wiringPiSetup();
//      wiringPiSetup () ;

        int [] scale
                =
                {
                    659, 659, 0, 659, 0, 523, 659, 0, 784, 0, 0, 0, 392, 0, 0, 0, 523, 0, 0, 392, 0, 0, 330
                };
//      int scale [23] = { 659, 659, 0, 659, 0, 523, 659, 0, 784, 0,0,0, 392, 0,0,0, 523, 0,0, 392, 0,0,330 } ;
        
        int i;

        SoftTone.softToneCreate(PIEZO_PIN);
//      softToneCreate (PIEZO_PIN) ;
        
        for (i = 0; i < 23; ++i)
        {
            System.out.printf("%3d\n", i);

            SoftTone.softToneWrite(PIEZO_PIN, scale[i]);
//          softToneWrite (PIEZO_PIN, scale [i]) ;

            Thread.sleep(200);
//          delay (200) ;
        }
        
        SoftTone.softToneStop(PIEZO_PIN);
    }
}
