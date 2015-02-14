
package org.onebeartoe.media.piezo.ports.rtttl;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftTone;

import static org.onebeartoe.media.piezo.ports.rtttl.Pitches.*;
//#include "pitches.h"

import static org.onebeartoe.media.piezo.ports.softtone.SoftTonePort.PIEZO_PIN;

/**
 * This class is a direct port from Arduino C to Java from the program listing
 * found here:
 *
 * http://electronics.flosscience.com/Home_LE/unit-9---lets-make-some-noise/rtttl
 *
 * The Arduino C code has been commented out just below the Java equivalent.
 *
 * @author Roberto Marquez
 */
public class RingToneTextTransferLanguage
{
// a static import bring in the piezo pin number
//int tonePin=8;   // Use pin 8 for your speaker
    private final int OCTAVE_OFFSET = 0;
//  #define OCTAVE_OFFSET 0

// the Arduino code stayed the same for the notes array    
    int notes[] =
    {
        0,
        NOTE_C4, NOTE_CS4, NOTE_D4, NOTE_DS4, NOTE_E4, NOTE_F4, NOTE_FS4, NOTE_G4, NOTE_GS4, NOTE_A4, NOTE_AS4, NOTE_B4,
        NOTE_C5, NOTE_CS5, NOTE_D5, NOTE_DS5, NOTE_E5, NOTE_F5, NOTE_FS5, NOTE_G5, NOTE_GS5, NOTE_A5, NOTE_AS5, NOTE_B5,
        NOTE_C6, NOTE_CS6, NOTE_D6, NOTE_DS6, NOTE_E6, NOTE_F6, NOTE_FS6, NOTE_G6, NOTE_GS6, NOTE_A6, NOTE_AS6, NOTE_B6,
        NOTE_C7, NOTE_CS7, NOTE_D7, NOTE_DS7, NOTE_E7, NOTE_F7, NOTE_FS7, NOTE_G7, NOTE_GS7, NOTE_A7, NOTE_AS7, NOTE_B7
    };

    private static String song = "A-Team:d=8,o=5,b=125:4d#6,a#,2d#6,16p,g#,4a#,4d#.,p,16g,16a#,d#6,a#,f6,2d#6,16p,c#.6,16c6,16a#,g#.,2a#";
//char *song = "A-Team:d=8,o=5,b=125:4d#6,a#,2d#6,16p,g#,4a#,4d#.,p,16g,16a#,d#6,a#,f6,2d#6,16p,c#.6,16c6,16a#,g#.,2a#";

    public static void main(String[] args) throws InterruptedException
    {
        Gpio.wiringPiSetup();

        SoftTone.softToneCreate(PIEZO_PIN);

        RingToneTextTransferLanguage app = new RingToneTextTransferLanguage();
        app.play_rtttl(song);
        System.out.println("Done.");

// get rid of this comment if the tone stops at the end of play_rtttl()
//        SoftTone.softToneStop(PIEZO_PIN);
    }

    /**
     * Absolutely no error checking in here
     *
     * @param songData
     */
    void play_rtttl(String songData) throws InterruptedException
//void play_rtttl(char *p)
    {
        int default_dur = 4;
//  byte default_dur = 4;

        int default_oct = 6;
//  byte default_oct = 6;

        int bpm = 63;
        int num;
        long wholenote;
        long duration;
        int note;

        int scale;
//  byte scale;

  // format: d=N,o=N,b=NNN:
        // find the start (skip name, etc)
  // In the Arduino function signature, p was a pointer to a character string.
        // This code port to Java makes use of C pointer arithmetic and uses p as a Sting index, instead.
        int p = 0;

        // ignore name
        while (songData.charAt(p) != ':')
        {
            p++;
        }
        p++;                     // skip ':'  
//  while(*p != ':') p++;    // ignore name
//  p++;                     // skip ':'

        // get default duration
        if (songData.charAt(p) == 'd')
//  if(*p == 'd')
        {
            p++;
            p++;              // skip "d="
            num = 0;

            while (Character.isDigit(songData.charAt(p)))
//    while(isdigit(*p))
            {
                num = (num * 10) + Integer.valueOf(songData.charAt(p++) - '0');
//WAS THE [- '0'] SUPPOSED TO SAY?      
//      num = (num * 10) + (*p++ - '0');
            }

            if (num > 0)
            {
                default_dur = num;
            }

            p++;                   // skip comma
        }

        System.out.println("ddur: " + default_dur);
//  Serial.print("ddur: "); Serial.println(default_dur, 10);

        // get default octave 
        if (songData.charAt(p) == 'o')
//  if(*p == 'o')
        {
            p++;
            p++;              // skip "o="

            num = Integer.valueOf(songData.charAt(p++) - '0');
// was the - 0 needed?    
//    num = *p++ - '0';

            if (num >= 3 && num <= 7)
            {
                default_oct = num;
            }

            p++;                   // skip comma
        }

        System.out.println("doct: " + default_oct);
//  Serial.print("doct: "); Serial.println(default_oct, 10);

        // get BPM
        if (songData.charAt(p) == 'b')
//    if(*p == 'b')
        {
            p++;
            p++;              // skip "b="

            num = 0;
            while (Character.isDigit(songData.charAt(p)))
//    while(isdigit(*p))
            {
                num = (num * 10) + Integer.valueOf(songData.charAt(p++) - '0');
//        num = (num * 10) + (*p++ - '0');
            }
            bpm = num;

            p++;                   // skip colon
        }

        System.out.println("bpm: " + bpm);

        // BPM usually expresses the number of quarter notes per minute
        wholenote = (60 * 1000L / bpm) * 4;  // this is the time for whole note (in milliseconds)

        System.out.println("wn: " + wholenote);

        // now begin note loop
        while (p < songData.length())
//  while(*p)
        {
            // first, get note duration, if available
            num = 0;
            while (Character.isDigit(songData.charAt(p)))
//    while(isdigit(*p))
            {
                num = (num * 10) + Integer.valueOf(songData.charAt(p++) - '0');
//      num = (num * 10) + (*p++ - '0');
            }

            // stay focused, the next to statemetns are single line if/else blocks WITH NO CURLY BRACES
            if (num != 0)
            {
                duration = wholenote / num;
            }
//    if(num) duration = wholenote / num;
            else
            {
                duration = wholenote / default_dur;  // we will need to check if we are a dotted note after
            }
            // now get the note
            note = 0;

            switch (songData.charAt(p))
//    switch(*p)
            {
                case 'c':
                    note = 1;
                    break;
                case 'd':
                    note = 3;
                    break;
                case 'e':
                    note = 5;
                    break;
                case 'f':
                    note = 6;
                    break;
                case 'g':
                    note = 8;
                    break;
                case 'a':
                    note = 10;
                    break;
                case 'b':
                    note = 12;
                    break;
                case 'p':
                default:
                    note = 0;
            }
            p++;

            // now, get optional '#' sharp
            if (songData.charAt(p) == '#')
//    if(*p == '#')
            {
                note++;
                p++;
            }

            // now, get optional '.' dotted note
            if (songData.charAt(p) == '.')
//    if(*p == '.')
            {
                duration += duration / 2;
                p++;
            }

            // now, get scale    
            if (Character.isDigit(songData.charAt(p)))
//    if(isdigit(*p))
            {

                scale = Integer.valueOf(songData.charAt(p) - '0');
//      scale = *p - '0';
                p++;
            }
            else
            {
                scale = default_oct;
            }

            scale += OCTAVE_OFFSET;

            if (songData.charAt(p) == ',')
//    if(*p == ',')
            {
                p++;       // skip comma for next note (or we may be at the end)
            }
            // now play the note

//duration = 400;

            if (note != 0)
//    if(note)
            {
                String message = "Playing: " + scale + " " + note + " (" + 
                                 notes[(scale - 4) * 12 + note] + ") " + duration;
                System.out.println(message);

                int n = notes[(scale - 4) * 12 + note];
                SoftTone.softToneWrite(PIEZO_PIN, n);
//        tone(tonePin,notes[(scale - 4) * 12 + note]);

                
                Thread.sleep(duration);
//        delay(duration);

//                SoftTone.softToneStop(PIEZO_PIN);
//        noTone(8);
            }
            else
            {
                System.out.println("Pausing: " + duration);

                Thread.sleep(duration);
//      delay(duration);
            }
        }
    }
}
