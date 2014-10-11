
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
