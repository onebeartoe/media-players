
package org.onebeartoe.media.microphone;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.onebeartoe.filesystem.DefaultFao;
import org.onebeartoe.filesystem.FilesystemAccessObject;
import org.onebeartoe.multimedia.DefaultSoundRecorder;
import org.onebeartoe.multimedia.SoundRecorder;

/**
 * This application demonstrates the capture of audio data from a microphone into an audio file.
 * It also has the ability to play or loop the recorded audio.
*/
public class SoundRecorderDemo extends JFrame 
{
    private AudioFormat audioFormat;
	
    private SoundRecorder soundRecorder;

    private JButton captureButton;	

    private JButton stopButton;

    private File outdir;	

    private ButtonGroup formatOptions;

    private AudioFileFormat.Type [] audioFormats = {AudioFileFormat.Type.AIFC, AudioFileFormat.Type.AIFF, AudioFileFormat.Type.AU, 
                                                                                    AudioFileFormat.Type.SND, AudioFileFormat.Type.WAVE};

    private JFileChooser outdirChooser;

    private JTextField infileField;
    
    private JPanel southPanel;        
    
    private File outputFile;
    
    private List<Recording> recordings;
    
    private JFileChooser loadDirectoryChooser;
    
    private FilesystemAccessObject fao;
	
    public SoundRecorderDemo() 
    {
        JMenu appMenu = new JMenu("Application");
        appMenu.setMnemonic('A');
        JMenuItem openDirectoryMenuItem = new JMenuItem("Load Directory");
        openDirectoryMenuItem.addActionListener( new LoadDirectoryListener() );
        appMenu.add(openDirectoryMenuItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(appMenu);
        setJMenuBar(menuBar);
        
        fao = new DefaultFao();
        
        String path = System.getProperty("user.home");
        
        if( path == null || path.equals("") )
        {
                path = ".";
        }
        outdir = new File(path);

        outdirChooser = new JFileChooser(outdir);
        outdirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        loadDirectoryChooser = new JFileChooser(outdir);
        loadDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        recordings = new ArrayList();
        
        formatOptions = new ButtonGroup();

        JPanel formatPanel = new JPanel();
        formatPanel.setBorder( new TitledBorder("Audio Format") );				

        for(AudioFileFormat.Type type : audioFormats)
        {   
                JRadioButton rb = new JRadioButton( type.toString() );
                rb.setActionCommand( type.toString().toLowerCase() );
                formatPanel.add(rb);			
                formatOptions.add(rb);
                System.out.println(type.getExtension() + " - " + type.toString() );
        }
        ((JRadioButton)formatPanel.getComponent( formatPanel.getComponentCount() - 1 )).setSelected(true);

        captureButton = new JButton("Record");
        captureButton.setEnabled(true);
        captureButton.addActionListener( new CaptureButtonListener() );

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener( new StopButtonListener() );		
		
        JPanel buttonsPanel = new JPanel( new BorderLayout(3,3) );
        buttonsPanel.setBorder( new TitledBorder("Output Path") );
        buttonsPanel.setPreferredSize( new Dimension(-1, 50) );
        buttonsPanel.add(captureButton, BorderLayout.WEST);
        buttonsPanel.add(stopButton, BorderLayout.EAST);
        
        infileField = new JTextField( outdir.getAbsolutePath() );
        JButton selectFileButton = new JButton("Select");
        selectFileButton.addActionListener( new SelectOutdirButtonListener() );
        
        JPanel fileInputModeButtonsPanel = new JPanel( new FlowLayout(FlowLayout.CENTER) );        
        fileInputModeButtonsPanel.add(selectFileButton);                                
        
        JPanel fileInputPanelz = new JPanel( new BorderLayout(3,3) );
        fileInputPanelz.setPreferredSize( new Dimension(-1, 70) );
        fileInputPanelz.setBorder( new TitledBorder("Output Folder") );
        fileInputPanelz.add(infileField, BorderLayout.CENTER);
        fileInputPanelz.add(fileInputModeButtonsPanel, BorderLayout.EAST);

        JPanel fileInputPanel = new JPanel( new BorderLayout(3,3) );
        fileInputPanel.add(fileInputPanelz, BorderLayout.CENTER);
        fileInputPanel.add(formatPanel, BorderLayout.SOUTH);
		
        JPanel buttonPanel = new JPanel( new BorderLayout() );
        buttonPanel.add(captureButton, BorderLayout.CENTER);
        
        JPanel rightButtonPanel = new JPanel( new BorderLayout() );
        rightButtonPanel.add(stopButton);
        
        JPanel buttonPanelPanel = new JPanel();
        buttonPanelPanel.setLayout( new BoxLayout(buttonPanelPanel, BoxLayout.X_AXIS) );
        buttonPanelPanel.add(buttonPanel);
        buttonPanelPanel.add(rightButtonPanel);
		
        setLayout( new BorderLayout(3,3) );
        
        JPanel northPanel = new JPanel( new BorderLayout() );
        northPanel.add(fileInputPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanelPanel, BorderLayout.CENTER);

        int rows = 1;
        southPanel = new JPanel( new GridLayout(rows, 1) );
        southPanel.setBorder( new TitledBorder("Recordings"));
        
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.CENTER);
		
        setTitle("onebeartoe.com - Sound Recorder - Desktop Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(200, 300);
        setSize(650, 340);
        setVisible(true);				
    }
	
    private class SelectOutdirButtonListener implements ActionListener
    {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                    int result = outdirChooser.showOpenDialog(SoundRecorderDemo.this);
                    if(result == JFileChooser.APPROVE_OPTION)
                    {
                            outdir = outdirChooser.getSelectedFile();
                            infileField.setText( outdir.getAbsolutePath() );
                    }
            }
    }

    private class CaptureButtonListener implements ActionListener
    {
            public void actionPerformed(ActionEvent e) 
            {
                    captureButton.setEnabled(false);
                    stopButton.setEnabled(true);

                    // Capture input data from the microphone until the Stop button is clicked.
                    captureAudio();
            }		
    }
    
    private class LoadDirectoryListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            int result = loadDirectoryChooser.showOpenDialog(SoundRecorderDemo.this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                recordings.clear();
                southPanel.removeAll();
                southPanel.updateUI();
                
                File directory = loadDirectoryChooser.getSelectedFile();
                ButtonModel button = formatOptions.getSelection();
                String extension = button.getActionCommand();
                if( extension.equals("wave") )
                {
                    extension = "wav";
                }
                Collection<File> files = fao.findFilesByExtention(directory, extension, false);
                for(File f : files)
                {
                    final Recording recording = new Recording();
                    recording.location = f;
                    
                    addAudioClip(recording);
                }
            }
        }        
    }
	
    private class StopButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            captureButton.setEnabled(true);
            stopButton.setEnabled(false);

            // Terminate the capturing of input data from the microphone.
            soundRecorder.stop();

            final Recording recording = new Recording();
            recording.location = outputFile;

            addAudioClip(recording);
        }
    }
    
    private void addAudioClip(final Recording recording)
    {
        AudioClip ac;
        try 
        {
            File f = recording.location;            
            ac = Applet.newAudioClip( f.toURL() );
//            ac = Applet.newAudioClip( outputFile.toURL() );
            recording.audioClip = ac;

            final JPanel recordingPanel = new JPanel( new BorderLayout() );
            recordingPanel.setBorder( new TitledBorder( f.getAbsolutePath() ) );
//            recordingPanel.setBorder( new TitledBorder(outputFile.getAbsolutePath() ) );
            JButton loopButton = new JButton("Loop");
            loopButton.addActionListener( new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    recording.audioClip.loop();
                }
            });

            JButton playButton = new JButton("Play");
            playButton.addActionListener(new ActionListener() 
            {

                public void actionPerformed(ActionEvent e) 
                {
                    recording.audioClip.play();
                }
            });

            JButton stopButton = new JButton("Stop");
            stopButton.addActionListener( new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    recording.audioClip.stop();
                }
            });

            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener( new ActionListener() 
            {
                @Override
                public void actionPerformed(ActionEvent e) 
                {
                    recording.audioClip.stop();
                    
                    removeRecordingPanel(recordingPanel);
                }
            });
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener( new ActionListener() 
            {
                @Override
                public void actionPerformed(ActionEvent e) 
                {
                    recording.audioClip.stop();
                    
                    recording.location.delete();

                    removeRecordingPanel(recordingPanel);
                }
            });
            JPanel removeButtonsPannel = new JPanel();
            removeButtonsPannel.add(removeButton);
            removeButtonsPannel.add(deleteButton);

            JPanel buttonsPanel = new JPanel( new BorderLayout() );
            buttonsPanel.add(loopButton, BorderLayout.WEST);
            buttonsPanel.add(playButton, BorderLayout.CENTER);
            buttonsPanel.add(stopButton, BorderLayout.EAST);
            buttonsPanel.add(removeButtonsPannel, BorderLayout.SOUTH);

            recordingPanel.add(buttonsPanel, BorderLayout.CENTER);
// try this -> recordingPanel.updateUI();
            recordingPanel.invalidate();
            recordingPanel.revalidate();
            recordingPanel.repaint();

            southPanel.add(recordingPanel);
            southPanel.updateUI();
// this worked but we tried updateUI                
//                southPanel.invalidate();
//                southPanel.revalidate();
//                southPanel.repaint();
        } 
        catch (MalformedURLException ex) 
        {
            Logger.getLogger(SoundRecorderDemo.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    /**
     * This method captures audio input from a microphone and saves it in an audio file.
    */
    private void captureAudio() 
    {
            try 
            {
                    // Get things set up for capture
                    audioFormat = DefaultSoundRecorder.getAudioFormat();
                    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);			
                    AudioFileFormat.Type fileType = null;			
                    Enumeration<AbstractButton> buts = formatOptions.getElements();
                    List<AbstractButton> bs = Collections.list(buts);
                    for(AbstractButton but : bs)
                    {
                            if( but.isSelected() )
                            {
                                    for(AudioFileFormat.Type af : audioFormats)
                                    {
                                            if( but.getText().equals( af.toString() ) )
                                            {
                                                    fileType = af;

                                                    break;
                                            }
                                    }

                                    break;
                            }
                    }

                    System.out.println("format is: " + fileType.toString() );			

                    String extention = "." + fileType.getExtension();
                    
                    Calendar cal = Calendar.getInstance();
                    long milli = cal.getTimeInMillis();
                    String outfileName = "audio" + "." + milli + extention;
                    outputFile = new File(outdir, outfileName);

                    // Capture the microphone data into an audio file on a separate thread.
                    // Capture continues until the Stop button is clicked.
                    soundRecorder = new DefaultSoundRecorder(dataLineInfo, audioFormat, fileType, extention, outdir);
                    soundRecorder.start(outputFile);
            } 
            catch (Exception e) 
            {
                    e.printStackTrace();
                    System.exit(0);
            }
    }
	
    public static void main(String args[]) 
    {
        // sun
        new SoundRecorderDemo();
    }

    private void removeRecordingPanel(JPanel  recordingPanel)
    {        
        southPanel.remove(recordingPanel);
        southPanel.updateUI();
    }
    
}
