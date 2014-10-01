package onebeartoe.juke;

import java.io.*;

/**
    File name: Configuration.java
    Written by: Roberto H. Marquez
    @author <a href="mailto:onebeartoe@lycos.com">Roberto H. Marquez</a>
    Last Modified: 
    Created: Feb 17th 2004
    This application is used to store the state of RandomJuke.
*/
public class Configuration {

   private String artistsPath;

   /**
      empty cnstructor for XML encodeing and XML decoding
   */
   public Configuration() {
   }

   public void setArtistsPath(String ap) {
      artistsPath = ap;
   }
   public String getArtistsPath() {
      return artistsPath;
   }

}