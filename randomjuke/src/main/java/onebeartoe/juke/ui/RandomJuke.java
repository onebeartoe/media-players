
package onebeartoe.juke.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.media.ui.MessageBox;
import ioio.lib.api.AnalogInput;

import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import onebeartoe.juke.network.RandomJukeServerConnection;
import onebeartoe.juke.ui.eck.KaleidaAnimate;
import org.onebeartoe.application.ApplicationMode;
import org.onebeartoe.application.ui.GUITools;
import org.onebeartoe.application.ui.GraphicalUserInterfaceServices;
import org.onebeartoe.application.ui.LookAndFeelButton;
import org.onebeartoe.application.ui.SwingServices;
import org.onebeartoe.io.ObjectRetriever;
import org.onebeartoe.io.ObjectSaver;
import org.onebeartoe.juke.sound.meter.SoundMeterModes;
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
import org.onebeartoe.network.ThreadedServer;
import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.pixel.sound.meter.AllOffSoundMeter;
import org.onebeartoe.pixel.sound.meter.BlobSoundMeter;
import org.onebeartoe.pixel.sound.meter.ButtonUpSoundMeter;
import org.onebeartoe.pixel.sound.meter.CircleSoundMeter;
import org.onebeartoe.pixel.sound.meter.RectangularSoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundReading;

public class RandomJuke
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

    public static String currentSongTitle;

    private static URL currentSong;

    private JButton pathOptionsButton;

    private LookAndFeelButton lookButton;

    private static JButton nextSongButton;

    private JPanel ControlPanel;

    // the visual effect
    static KaleidaAnimate visual;

    // the current song playing
    private static JLabel currentSongLabel;

    // object reference used to play media
    private static Player player;

    private static int duplicateThreshold;

    private ThreadedServer nextSongServer;

    private JLabel ipLabel;

    private JPanel mediaPanel;

    private Component mediaControls;

    private JPanel bottomPanel;

    private Container c;
    
    private static ApplicationMode mode;
    
    private static SongsControllerListener songListener;
    
    private static PixelIntegration pixelIntegration;
    
    private static IOIO ioiO;

    private static PixelEnvironment pixelEnvironment;

    private static int offscreenImageHeight;
    
    private static int offscreenImageWidth;
    
    private static Pixel pixel;
    
    private static JFrame guiWindow;
    
    private static AnalogInput microphoneSensor;
    
    private static volatile List<SoundReading> microphoneValues;
    
    private static int SAMPLE_BUFFER_SIZE = 50;
    
    public static SoundMeter soundMeter;
    
    private static Random random;
            
    /**
     * Setup the application.
     */
    public RandomJuke()
    {
        songListener = new SongsControllerListener();
        
        songsPlayedService = new NoPersistenceSongsPlayedService();

        currentSongSerice = new RegularCurrentSongService();

        uiService = new SwingServices();

        duplicateThreshold = 100;
          
        random = new Random();
        
        microphoneValues = new ArrayList();

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
        randomJukeServerConnection.setNextSongButton(nextSongButton);
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
        } catch (MalformedURLException e)
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
    public static void playSong()
    {
        String filename = currentSongTitle;

        try
        {
            System.out.println("\ncreating Player from URL with a uri value of:\n" + filename + "\n");
            URL url = new URL(filename);

            // Create an instance of a player for this media url
            try
            {
                player = null;
                player = Manager.createPlayer(url);

                String uri = url.toString();
                songsPlayedService.addPlayedSong(uri);
            } 
            catch (NoPlayerException e)
            {
                Fatal("Error: " + e);
                e.printStackTrace();
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

        if (player != null)
        {
            ControllerListener listener = songListener;
            player.addControllerListener(listener);
            player.realize();
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
                soundMeter = new ButtonUpSoundMeter(offscreenImageWidth, offscreenImageHeight);
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
            default:
            {
                // off
                soundMeter = new AllOffSoundMeter(offscreenImageWidth, offscreenImageHeight);
            }
        }
    }
    
    private void setupSwingUi()
    {
        guiWindow = new JFrame("onebeartoe RandomJuke 3.3 - You Know It Edition");
        songListPathPanel = new SwingSongListPathPanel();

        lookButton = new LookAndFeelButton("Change", guiWindow);
        String title = "Look and Feel";
        Color color = Color.BLUE;
        JPanel lookPanel = new JPanel();
        lookPanel.add(lookButton);
        lookPanel.setBorder(GUITools.factoryLineBorder(title, color));

        ipLabel = new JLabel();
        title = "Remote Control URL:";
        JPanel ipPanel = new JPanel();
        ipPanel.add(ipLabel);
        JButton ipButton = new JButton("View");
        ipButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String url = nextSongServer.ip;
                uiService.viewBrowser(url);
            }
        });
        ipPanel.add(ipLabel);
        ipPanel.add(ipButton);
        ipPanel.setBorder(GUITools.factoryLineBorder(title, color));

        pathOptionsButton = new JButton("Change");
        ChangePathButtonHandler changeSongListUrlsListeners = new ChangePathButtonHandler();
        pathOptionsButton.addActionListener(changeSongListUrlsListeners);
        title = "Song Paths";
        JPanel pathOptionsPanel = new JPanel();
        pathOptionsPanel.add(pathOptionsButton);
        pathOptionsPanel.setBorder(GUITools.factoryLineBorder(title, color));

        settingPanel = new JPanel();
        settingPanel.setLayout(new GridLayout(3, 1));
        settingPanel.add(pathOptionsPanel);
        settingPanel.add(ipPanel);
        settingPanel.add(lookPanel);

//        nextSongButton = new JButton("Next Song");


        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new SettingsButtonHandler());

        // Setup the Control Panel.
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new GridLayout(1, 10));
        ControlPanel.add(nextSongButton);
        ControlPanel.add(settingsButton);

        // set up the visal panel
        visual = new KaleidaAnimate();
        visual.init();

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
        c = guiWindow.getContentPane();
        c.add(ControlPanel, BorderLayout.NORTH);
        c.add(visual, BorderLayout.CENTER);
        c.add(bottomPanel, BorderLayout.SOUTH);

        guiWindow.addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    Object o = RandomJuke.configuration;
                    File outfile = RandomJuke.configurationFile;
                    boolean saved = ObjectSaver.encodeObject(o, outfile);
                    System.out.println("configuration saved: " + saved);

                    try
                    {
                        songsPlayedService.storeSongsPlayed();
                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }

                    System.exit(0);
                }
            }
        );
        
        guiWindow.setLocation(320, 105);
        guiWindow.setSize(475, 375);
        guiWindow.setVisible(true);
        
        visual.start();
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
     * main program to call the JFrame to the screen
     */
    public static void main(String args[])
    {
        songListManager = new NetworkAndFilesystemSearchingSongManager();
        ((NetworkAndFilesystemSearchingSongManager) songListManager).setNetworkSongManager(new JavaxNetworkSearchingSongManager());
        final String configurationFilename = "RandomJukeConfig.xml";

        configurationFile = new File(configurationFilename);
        System.out.println("loading configuration from: " + configurationFile.getAbsolutePath());
        String err_msg = null;

        // start off with a blank config
        configuration = new JukeConfig();
        
        nextSongButton = new JButton("Next Song");
        nextSongButton.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (player != null)
                    {
                        player.stop();
                        player.close();
                    }
                    playNextSong();
                }
            }
        );
                
        mode = ApplicationMode.COMMAND_LINE;
//mode = ApplicationMode.GUI;        
        if(args.length > 0)
        {
            String arg = args[0];
            if( arg.equals("--gui") )
            {
                mode = ApplicationMode.GUI;               
            }
        }                
        
        final RandomJuke app = new RandomJuke();

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
                app.badConfigFileRecover(err_msg);
            }
        } 
        else
        {
            err_msg = "<html>No configuration file was found.";
            err_msg += "<br>Please choose a directory with songs on the next screen.";
            app.badConfigFileRecover(err_msg);
        }

        loadSongLists();
        
        
        try
        {
            pixelIntegration = new PixelIntegration();
//            pixelIntegration.go(args);
        } 
        catch (Exception ex)
        {
            Logger.getLogger(RandomJuke.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pausePlayer()
    {
        if (player != null)
        {
            player.stop();
        }
    }        

    public void unpausePlayer()
    {
        if (player != null)
        {
            player.start();
        }
    }
    
    private void updateMediaControls()
    {
        if (mediaControls != null)
        {
            mediaPanel.remove(mediaControls);
        }

        mediaControls = player.getControlPanelComponent();
        int width = guiWindow.getWidth();
        int height = mediaControls.getHeight();
        mediaControls.setPreferredSize(new Dimension(width, height));
        mediaControls.doLayout();
        mediaPanel.add(mediaControls, BorderLayout.CENTER);
        mediaPanel.revalidate();
        mediaPanel.doLayout();
        bottomPanel.doLayout();
        c.doLayout();
    }

    /**
     * this method will loop until it finds a song that has not played before
     */
    public static void playNextSong()
    {
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

                Random randomTitle = new Random();
                rs = randomTitle.nextInt(songNames.length - 1);

                String songTitle = songNames[rs];

                currentSong = songList.getUrlFor(songTitle);
                currentSongTitle = currentSong.toString();

                boolean dupSong = false;
                try
                {
                    dupSong = songsPlayedService.hasBeenPlayed(currentSongTitle);
                } catch (Exception e)
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

    private class ChangePathButtonHandler implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            int result = JOptionPane.showConfirmDialog(guiWindow, songListPathPanel);

            if (result == JOptionPane.OK_OPTION)
            {
                List<String> songListUrls = songListPathPanel.getSongListPaths();

                setSongListUrls(songListUrls);
            }
        }
    }
    
    private static void Fatal(String s)
    {
        if(mode == ApplicationMode.GUI)
        {
            MessageBox mb = new MessageBox("JMF Error", s);
        }
        else
        {
            System.err.println(s);
        }
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
                            System.out.println("sound meter called but not initialized");
                        }
                        else
                        {
//                            System.out.println("DISPLAYING SOUND DATA");
                            List<SoundReading> values = new ArrayList();
                            values.addAll(microphoneValues);
                            soundMeter.displaySoundData(pixel, values);
                        }
                    }
                };
                        
                long refreshRate = 1;
//                long refreshRate = 300;
                timer.schedule(task, now, refreshRate);
                System.out.println("SCHEDULED DISPLAY TASK");
                
System.out.println("CALLING GO");
                go(null);
            } 
            catch (Exception ex)
            {
                Logger.getLogger(RandomJuke.class.getName()).log(Level.SEVERE, null, ex);
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

                @Override
                protected void setup() throws ConnectionLostException,
                        InterruptedException 
                {
                    ioiO = ioio_;
                    pixelEnvironment = new PixelEnvironment(1);
                    offscreenImageHeight = pixelEnvironment.KIND.height * 2;
                    offscreenImageWidth = pixelEnvironment.KIND.width * 2;
                    RgbLedMatrix ledMatrix = ioio_.openRgbLedMatrix(pixelEnvironment.KIND);                    
                    pixel = new Pixel(pixelEnvironment.KIND);
                    pixel.matrix = ledMatrix;
                    pixel.ioiO = ioio_;
                    microphoneSensor = Pixel.getAnalogInput1();
                    microphoneSensor.setBuffer(SAMPLE_BUFFER_SIZE);
  
                    soundMeter = new AllOffSoundMeter(offscreenImageWidth, offscreenImageHeight);
                            
                    System.out.println("Found PIXEL: " + pixel.matrix + "\n");                     
                }

                @Override
                public void loop() throws ConnectionLostException,
                        InterruptedException 
                {
                    float p = microphoneSensor.readBuffered();
                    
//                    System.out.println("proximity sensor: " + p);

                    float ratio = offscreenImageHeight * p;
                    int height = (int) ratio;

                    Color c = ButtonUpSoundMeter.randomcolor();
                    
                    SoundReading reading = new SoundReading();
                    reading.height = height;
                    reading.color = c;
                    
                    microphoneValues.add(reading);
                    
                    if(microphoneValues.size() > SAMPLE_BUFFER_SIZE)
                    {
                        microphoneValues.remove(0);
                    }
                    
//                    System.out.print(".");
                }
            };
                    
            return looper;
        }        
    }
            
    private class SettingsButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String ipLabelText = "http://" + nextSongServer.ip + ":" + nextSongServer.port;
            ipLabel.setText(ipLabelText);

            JOptionPane.showMessageDialog(guiWindow, settingPanel);
        }
    }
    
    private class SongsControllerListener implements ControllerListener
    {
        @Override
        public void controllerUpdate(ControllerEvent ce)
        {
            if (ce instanceof RealizeCompleteEvent)
            {
                player.prefetch();            
                updateMediaControls();
            } 
            else if (ce instanceof PrefetchCompleteEvent)
            {
                player.setMediaTime(new Time(0));
                player.start();
            } 
            else if (ce instanceof EndOfMediaEvent)
            {
                playNextSong();
            }
        }
    }
    
}
