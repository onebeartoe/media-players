
package org.onebeartoe.juke.javafx.demo;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * @author Roberto Marquez
 */
public class PlayTwoAudioFiles 
{
    private static MediaPlayer mediaPlayer;
    
    public static void main(String [] args)
    {
        String audio1 = args[0];
        final String audio2 = args[1];
        
        System.out.println("Input songs:\n" + audio1 + "\n" + audio2 + "\n");
        
        final Media media = new Media(audio1);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnError(new Runnable() 
        {
            @Override public void run() {
                System.out.println("mediaPlayer.getError() = " + mediaPlayer.getError());
            }
        });
        mediaPlayer.setOnEndOfMedia( new Runnable() 
        {
            @Override
            public void run() 
            {                
                mediaPlayer.stop();
                
                System.out.println("playing the second audio file...");
                Media media = new Media(audio2);
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setOnError(new Runnable() 
                {
                    @Override public void run() 
                    {
                        System.out.println("mediaPlayer.getError() = " + mediaPlayer.getError());
                    }
                });
                mediaPlayer.setOnEndOfMedia( new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        System.out.println("end of second audio fiile reached");
                    }
                });
            }
        });
    }
}
