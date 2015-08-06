
package org.onebeartoe.juke.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import onebeartoe.juke.network.JukeClient;

import onebeartoe.juke.network.RandomJukeServerConnection;

import org.onebeartoe.application.ApplicationMode;
import org.onebeartoe.application.ui.GUITools;
import org.onebeartoe.application.ui.GraphicalUserInterfaceServices;
import org.onebeartoe.application.ui.LookAndFeelButton;
import org.onebeartoe.application.ui.SwingServices;
import org.onebeartoe.io.ObjectRetriever;
import org.onebeartoe.multimedia.juke.JukeConfig;
import org.onebeartoe.multimedia.juke.SongList;
import org.onebeartoe.multimedia.juke.gui.SwingSongListPathPanel;
import org.onebeartoe.multimedia.juke.services.CurrentSongService;
import org.onebeartoe.multimedia.juke.services.NoPersistenceSongsPlayedService;
import org.onebeartoe.multimedia.juke.services.RegularCurrentSongService;
import org.onebeartoe.multimedia.juke.services.SongsPlayedService;
import org.onebeartoe.multimedia.juke.songs.JavaxNetworkSearchingSongManager;
import org.onebeartoe.multimedia.juke.songs.NetworkAndFilesystemSearchingSongManager;
import org.onebeartoe.multimedia.juke.songs.SongListManager;
import onebeartoe.juke.network.ThreadedServer;

public class RandomJuke extends JukeClient
{
    private static final long serialVersionUID = 178947923L;

    static SongListManager songListManager;

    public static SongsPlayedService songsPlayedService;

    static CurrentSongService currentSongSerice;

    private static JukeConfig configuration;

    private static File configurationFile;

    private static SwingSongListPathPanel songListPathPanel;

    private JPanel settingPanel;

    private static GraphicalUserInterfaceServices uiService;

    private static URL currentSong;

    private LookAndFeelButton lookButton;

    private JPanel ControlPanel;

    // the current song playing
    private static JLabel currentSongLabel;

    private static int duplicateThreshold;

    private ThreadedServer nextSongServer;

    private JLabel ipLabel;

    private JPanel mediaPanel;

    private JPanel bottomPanel;

    private Container c;
    
    private static ApplicationMode mode;
    
    private static int SAMPLE_BUFFER_SIZE = 50;
    
    private static Random random;
    
    Random randomTitle;
    
    public static String currentSongTitle;
            
    /**
     * Setup the application.
     */
    public RandomJuke(String [] args)
    {
        System.out.println("it begins");
        songListManager = new NetworkAndFilesystemSearchingSongManager();
        ((NetworkAndFilesystemSearchingSongManager) songListManager).setNetworkSongManager(new JavaxNetworkSearchingSongManager());
        final String configurationFilename = "RandomJukeConfig.xml";

        configurationFile = new File(configurationFilename);
        System.out.println("loading configuration from: " + configurationFile.getAbsolutePath());
        String err_msg = null;

        // start off with a blank config
        configuration = new JukeConfig();
                
        mode = ApplicationMode.COMMAND_LINE;

        if(args.length > 0)
        {
            String arg = args[0];
            if( arg.equals("--gui") )
            {
                mode = ApplicationMode.GUI;               
            }
        }        
        
        if (configurationFile.exists())
        {
            System.out.println(configurationFilename + " exists");
            try
            {
                configuration = (JukeConfig) ObjectRetriever.decodeObject(configurationFile);
            } 
            catch (Exception e)
            {
                e.printStackTrace();
                err_msg = "<html>Problem decoding the configuration object from the XML file.";
                err_msg += "<br>Please choose a directory with songs on the next screen.";
                badConfigFileRecover(err_msg);
            }
        } 
        else
        {
            err_msg = "<html>No configuration file was found.";
            err_msg += "<br>Please choose a directory with songs on the next screen.";
            badConfigFileRecover(err_msg);
        }

        loadSongLists();
        
        randomTitle = new Random();
        
        songsPlayedService = new NoPersistenceSongsPlayedService();

        currentSongSerice = new RegularCurrentSongService();

        uiService = new SwingServices();

        duplicateThreshold = 100;
          
        random = new Random();


        try
        {
            songsPlayedService.retrieveSongsPlayed();
        } 
        catch (Exception e2)
        {
            e2.printStackTrace();
        }

        switch(mode)
        {
            case GUI:
            {
                setupSwingUi();
                break;
            }
            default:
            {
                // command line
                String message = "To start the application with a GUI, pass '--gui'" +
                                 " as an arguement to the command.";
                System.out.println(message);
            }
        }

        RandomJukeServerConnection randomJukeServerConnection = new RandomJukeServerConnection();
        randomJukeServerConnection.setCurrentSongService(currentSongSerice);
        randomJukeServerConnection.setApp(this);
        nextSongServer = new ThreadedServer();
        nextSongServer.setConnection(randomJukeServerConnection);
        nextSongServer.start();
    }

    public void addSongListUrl(String songsListUrl)
    {
        String songsDirPath = songsListUrl.toString();
        System.out.println("adding songs path: " + songsListUrl);

        configuration.addSongTitlePath(songsDirPath);

        String encodedUrl = null;
        URL songsPathURL;
        try
        {
            songsPathURL = new URL(songsListUrl);
            songListManager.discoverSongLists(songsPathURL);
        } 
        catch (MalformedURLException e)
        {
            String msg = "couldn't add song list source: " + encodedUrl;
            System.out.println(msg);
            e.printStackTrace();
            GUITools.infoMessage(msg);
        }
    }

    private void badConfigFileRecover(String err_msg)
    {
        if(mode == ApplicationMode.GUI)
        {
            GUITools.infoMessage(err_msg);
            System.out.println(err_msg);
            File file = GUITools.selectDirectory("Choose a directory containing song/audio files:");
            String url = "file:" + file.toString();
            addSongListUrl(url);
        }
        else
        {
            System.err.println("We need a way to specify an initial directory for headless mode");
        }        
    }   

    /**
     * These methods play the media. The ControllerUpdate() is listens for a
     * song to finish then plays the next song in Vector PlayList
     */
    public void playSong()
    {
        String filename = currentSongTitle;

        try
        {
            URL url = new URL(filename);
            
            if (mediaPlayer != null)
            {
                mediaPlayer.stop();            
            }
            
            String source = url.toString();
                                            
            System.out.println("url: " + url.toString() );

            source = source.replace(" ", "%20");

            System.out.println("uri: " + source);                            
            
            final Media media = new Media(source);

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setOnError(new Runnable()
            {
                @Override
                public void run()
                {
                    System.out.println("mediaPlayer.getError() = " + mediaPlayer.getError());
                }
            });
            mediaPlayer.setOnEndOfMedia( new Runnable()
            {

                @Override
                public void run()
                {
                    playNextSong();
                }
            });
            mediaPlayer.setOnReady( new Runnable()
            {

                @Override
                public void run()
                {
                    // do we need to update anything here?
                }
            });
            
            try
            {
                String uri = url.toString();
                songsPlayedService.addPlayedSong(uri);
            }
            catch (Exception e)
            {
                e.printStackTrace();

                Fatal("Error: " + e);
            }
        } 
        catch (MalformedURLException e)
        {
            Fatal("Error:" + e);
        }
    }

    public void setSongListUrls(List<String> songListUrls)
    {
        songListManager.clearSongLists();
        configuration.clearSongTitlePaths();

        for (String url : songListUrls)
        {
            addSongListUrl(url);
        }
    }
    
    private void setupSwingUi()
    {
        songListPathPanel = new SwingSongListPathPanel();

        String title = "Look and Feel";
        Color color = Color.BLUE;
        JPanel lookPanel = new JPanel();
        lookPanel.add(lookButton);
        lookPanel.setBorder(GUITools.factoryLineBorder(title, color));

        ipLabel = new JLabel();
        title = "Remote Control URL:";
        JPanel ipPanel = new JPanel();
        ipPanel.add(ipLabel);

        ChangePathButtonHandler changeSongListUrlsListeners = new ChangePathButtonHandler();

        title = "Song Paths";
        JPanel pathOptionsPanel = new JPanel();

        pathOptionsPanel.setBorder(GUITools.factoryLineBorder(title, color));

        settingPanel = new JPanel();
        settingPanel.setLayout(new GridLayout(3, 1));
        settingPanel.add(pathOptionsPanel);
        settingPanel.add(ipPanel);
        settingPanel.add(lookPanel);



        // Setup the Control Panel.
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new GridLayout(1, 10));


        // bottom panel
        currentSongLabel = new JLabel();
        currentSongLabel.setFont(new Font("TimesRoman", Font.PLAIN, 15));

        mediaPanel = new JPanel();

        bottomPanel = new JPanel();
        LayoutManager bottomPanelLayout = new BorderLayout();
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanel.add(mediaPanel);
        bottomPanel.add(currentSongLabel, BorderLayout.NORTH);

        // place the comonents onto JFrame
        c.add(ControlPanel, BorderLayout.NORTH);
        c.add(bottomPanel, BorderLayout.SOUTH);
    }            

    private static void loadSongLists()
    {
        List<String> urls = configuration.getSongTitleUrls();
        for (String uri : urls)
        {
            URL url = null;
            try
            {
                url = new URL(uri);
                songListManager.discoverSongLists(url);
                if(mode == ApplicationMode.GUI)
                {
                    songListPathPanel.addSongListPath(url.toString());   
                }
            } 
            catch (MalformedURLException e1)
            {
                e1.printStackTrace();
            }
        }        
    }

    /**
     * this method will loop until it finds a song that has not played before
     */
    public void playNextSong()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.dispose();
        }
        
        String[] songNames = null;
        int rs = -1;
        boolean loopingAgain;
        int dupCount = 0;
        boolean haveSong = true;
        do
        {
            String[] artistsNames = songListManager.getSongListTitles().toArray(new String[0]);

            // get random into the artist list
            int artistNamesLimit = songListManager.getSongListTitles().size();
            Random random = new Random();            
            int ra = random.nextInt(artistNamesLimit);

            if (ra < 0)
            {
                loopingAgain = false;
                haveSong = false;
                uiService.infoMessage("No songs sound.  Try adding more soungs sources in the configration panel");
            } 
            else
            {
                String artistName = artistsNames[ra];

                SongList songList = songListManager.getSongListFor(artistName);
                songNames = songList.getSongTitles();

                int i;
                
                if(songNames.length == 1)
                {
                    i = 1;
                }
                else
                {
                    i = songNames.length - 1;
                }
                
                rs = randomTitle.nextInt(i);

                String songTitle = songNames[rs];

                currentSong = songList.getUrlFor(songTitle);
                currentSongTitle = currentSong.toString();

                boolean dupSong = false;
                try
                {
                    dupSong = songsPlayedService.hasBeenPlayed(currentSongTitle);
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                    Fatal("Error:" + e);
                }

                if (dupSong && dupCount < duplicateThreshold)
                {
                    loopingAgain = true;
                    System.out.println("dup song: " + currentSongTitle);

                    dupCount++;
                } else
                {
                    loopingAgain = false;

                    if (dupCount == duplicateThreshold)
                    {
                        String message = "\nDuplicate threashold reached.";
                        System.out.println(message);
                        try
                        {
                            songsPlayedService.clearPlayedSongs();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Fatal("Error:" + e);
                        }
                    }

                    dupCount = 0;
                }
            }
        } 
        while (loopingAgain);

        String label;
        if (haveSong)
        {
            // take note of the song that has been taken out of the selection pool
            try
            {
                currentSongSerice.next(currentSongTitle, "localhost");
                currentSongSerice.setCurrentSong(currentSongTitle);
                songsPlayedService.addPlayedSong(currentSongTitle);
            } 
            catch (Exception e)
            {
                e.printStackTrace();
                Fatal("Error:" + e);
            }

            playSong();

            label = songNames[rs];
        } 
        else
        {
            label = "";
        }

        if(mode == ApplicationMode.GUI)
        {
            // update the JLabel that shows the name of the current song.
            currentSongLabel.setText(label);
        }
    }

    /**
     * we need to get the logic for the song list paths out of the GUI and 
     * ready for server side extraction.
     */
    private class ChangePathButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int result = JOptionPane.showConfirmDialog(null, songListPathPanel);
//            int result = JOptionPane.showConfirmDialog(guiWindow, songListPathPanel);

            if (result == JOptionPane.OK_OPTION)
            {
                List<String> songListUrls = songListPathPanel.getSongListPaths();

                setSongListUrls(songListUrls);
            }
        }
    }
    
    private static void Fatal(String s)
    {
        System.err.println(s);
    }
    
    private class SettingsButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String ipLabelText = "http://" + nextSongServer.ip + ":" + nextSongServer.port;
            ipLabel.setText(ipLabelText);
        }
    }
}