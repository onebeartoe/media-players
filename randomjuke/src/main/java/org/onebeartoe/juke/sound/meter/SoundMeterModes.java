
package org.onebeartoe.juke.sound.meter;

/**
 * @author Roberto Marquez
 */
public enum SoundMeterModes
{
    BLOB,
    BOTTOM_UP,
    CIRCLE,
    RECTANGLE,
    WAVE_GRAPH,
    GROWING_TEXT,
//    HORIZONTAL_MIRRORING,
//    COLOR_MAPPING,
    OFF;
    
    public static SoundMeterModes meterFor(String modeName)
    {
        String name = modeName.toUpperCase();
        name = name.replace("-", "_");
        SoundMeterModes mode = valueOf(name);
        
        return mode;
    }
}
