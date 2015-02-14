
package org.onebeartoe.media.piezo.ports.therandombit;

import com.pi4j.wiringpi.Gpio;
import static com.pi4j.wiringpi.Gpio.delay;
import static com.pi4j.wiringpi.Gpio.delayMicroseconds;
import com.pi4j.wiringpi.SoftTone;
import static org.onebeartoe.media.piezo.ports.softtone.SoftTonePort.PIEZO_PIN;

/**
 * No dice on this one.  The port I implemented does not sound like what Becky 
 * Stern has going on with the Gemma
 * This class is a direct port from Arduino C to Java from the program listing
 * found here:
 *
 * !!!! PLACE THE LINK TO THE ADAFRUIT TUTORIAL !!!!
 *
 * The Arduino C code has been commented out just below the Java equivalent.
 *
 * @author Roberto Marquez
 */
public class Tocar
{

    //Definiçao dos periodos para cada nota (*0.0000001)
private int C = 1911;
private int C1 = 1804;
private int D = 1703;
private int Eb = 1607;
private int E = 1517;
private int F = 1432;
private int F1 = 1352;
private int G = 1276;
private int Ab = 1204;
private int A = 1136;
private int Bb = 1073;
private int B = 1012;
private int c =    955;
private int c1 =   902;
private int d =    851;
private int eb =   803;
private int e =    758;
private int f =    716;
private int f1 =   676;
private int g =    638;
private int ab =   602;
private int a =    568;
private int bb =   536;
private int b =    506;
 
private int p    =   0 ; //pausa
 
//int speaker = 6;    //porta do arduino
long vel = 20000;


int peergynt_m[] = {G, E, D, C, D, E, G, E, D, C, D, E, D, E,G, E, G, A, E, A, G, E, D, C};
int peergynt_r[] = {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 8, 16};
 
int smoke_m[] = {E, G, A, E, G, Bb, A, E, G, A, G, E};
int smoke_r[] = {12, 12, 18, 12, 12, 6, 18, 12, 12, 18, 12, 24};
 
int natal_m[] = {G, A, G, E, G, A, G, E, c, c, A, B, B, G, A, G, A, c, B, A, G, A, G, E};
int natal_r[] = {12, 4, 8, 16, 12, 4, 8, 16, 12, 4, 16, 12, 4, 16, 12, 4, 8, 8, 8, 8, 12, 4, 8, 16};
 
int LTS_m[] = {Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, C, G, Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, F, D, F, D, G, F, D, C, Bb, G, Bb, C, C1, C, Bb, F, D, Bb, G, F, D, C, Bb, D, C, Bb, G} ;
int LTS_r[] = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
 
int melod[] = {e, e, e, c, e, g, G, c, G, E, A, B, Bb, A, G, e, g, a, f, g, e, c, d, B, c};
int ritmo[] = {6, 12, 12, 6, 12, 24, 24, 18, 18, 18, 12, 12, 6, 12, 8, 8, 8, 12, 6, 12, 12, 6, 6, 6, 12};

public Tocar()
{

}
void loop() 
{
// TODO: FIX THE 42
    for (int i=0; i<melod.length; i++) 
    {
        int tom = melod[i];
        int tempo = ritmo[i];
 
        long tvalue = tempo * vel;
 
        tocar(tom, tvalue);
 
        delayMicroseconds(1000); //pausa entre notas!
    }
    delay(1000);
}

public static void main(String[] args) throws InterruptedException
    {
        Gpio.wiringPiSetup();

        SoftTone.softToneCreate(PIEZO_PIN);

        Tocar app = new Tocar();
        app.loop();
        System.out.println("Done.");


        SoftTone.softToneStop(PIEZO_PIN);
    }

void tocar(int tom, long tempo_value) 
{
     long tempo_gasto = 0;
     while (tempo_gasto < tempo_value) 
     {
        SoftTone.softToneWrite(PIEZO_PIN, 1);
        delayMicroseconds(tom / 2);
 
        SoftTone.softToneWrite(PIEZO_PIN, 0);
        delayMicroseconds(tom/2);
 
        tempo_gasto += tom;
    }
}


    

}
