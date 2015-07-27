
package onebeartoe.juke.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import onebeartoe.juke.network.PixelClient;

import org.onebeartoe.io.TextFileReader;
import org.onebeartoe.multimedia.BasicMultimediaController;
import org.onebeartoe.multimedia.MultimediaController;
import org.onebeartoe.pixel.PixelClient;
import org.onebeartoe.pixel.sound.meter.SoundMeterModes;

/**
 * @author Roberto Marquez
 */
public abstract class ServerConnection implements Runnable, Cloneable
{
    protected Socket client;

    protected String path = "/";
    
    /**
     * @param client the client to set
     */
    public void setClient(Socket client)
    {
        this.client = client;
    }
    
    private String VOLUME_UP = "volume-up";

    private String VOLUME_DOWN = "volume-down";

    private String SERVER_VOLUME = "SERVER_VOLUME";

    protected JukeClient app;

//    private String changeSoundMeterMode(String mode)
//    {
//        String modeChangeResult = "Mode request change for " + mode + " received.  ";
//        SoundMeterModes meterMode = null;
//        try
//        {
//            meterMode = SoundMeterModes.meterFor(mode);
//            app.setSoundMeter(meterMode);
//            modeChangeResult += "Mode changed to " + meterMode;
//        }
//        catch(Exception e)
//        {
//            modeChangeResult += "Mode NOT chagned to " + meterMode + " (" + mode + ")";
//        }
//        
//        return modeChangeResult;
//    }
    
// TODO: test to see if the method can be removed    
    @Override
    public Object clone()
    {
        Object obj = null;
        try
        {
            obj = super.clone();
        } 
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return obj;
    }

    protected void sendHttpResponse(String html, boolean includeHeaders) throws IOException
    {
        String headers = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n";
        headers += "\r\n";
        
        OutputStream outs = client.getOutputStream();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(outs, "UTF-8"), true);
        if (includeHeaders)
        {
            out.print(headers);
        }
        out.println(html);
        out.flush();
        out.close();
    }

    /**
     * Sends the static contents of a file on the classpath
     * @param resourcePath - the path to the resource, on the classpath
     * @throws IOException 
     */
    protected void sendClasspathResource(String resourcePath) throws IOException
    {
        InputStream instream = getClass().getResourceAsStream(path + resourcePath);
        String html = TextFileReader.readText(instream);
        boolean includeHeader = false;
        sendHttpResponse(html, includeHeader);
    }
    
    protected void sendPlainTextResponse(String text) throws IOException
    {
        boolean includeHeader = false;
        sendHttpResponse(text, includeHeader);
    }
    
    public void setApp(JukeClient app)
    {
        this.app = app;
    }

    public abstract String getControlsResourcePath();
    
    protected String invalidRequest(String request)
    {
        StringBuilder out = new StringBuilder();
        out.append("Request Recieved Busta: " + request);
        out.append("<br /><br />" + getClass().getSimpleName() + " accepts requests like \"GET filename.html\"<br />");
        out.append("<br /><br />or something like \"filename.zip\"<br />");

        return out.toString();
    }
    
    public abstract void like(String currentSongTitle, String clientAddress);
    
    public abstract void nextAction(String currentSongTitle, String clientAddress);
    
    /**
     * This crazy method delegates each HTTP request.
     */
    @Override
    public void run()
    {
        try
        {
            InetAddress inetAddress = client.getInetAddress();
            String clientAddress = inetAddress.toString();
//            System.out.println(clientAddress + " is connecting");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "8859_1"));

            String request = in.readLine();

//            System.out.println("Request: " + request);
            
            boolean quitRequested = false;

            Pattern pattern = Pattern.compile("(GET|POST) /?(\\S*).*");

            Matcher httpRequestMatcher = pattern.matcher(request);
            if (!httpRequestMatcher.matches())
            {
                invalidRequest(request);
            } 
            else
            {
                MultimediaController systemMediaControler = new BasicMultimediaController();

                request = httpRequestMatcher.group(2);

                if (request.equals("style.css"))
                {
                    sendClasspathResource(request);
                } 
                else if (request.equals("layout.css"))
                {
                    sendClasspathResource(request);
                }
                else if( request.equals("sound-visualizer.js") )
                {
                    sendClasspathResource(request);
                }
                else if( request.equals("quit") )
                {
                    quitRequested = true;
                    String text = "Quit request received";
                    sendPlainTextResponse(text);

                    System.exit(1);
                }
                else
                {
                    boolean sendPlainText = false;
                    String plainText = "--plain text not set---";
                    
                    
                    
                    String[] parameters = request.split("&");
                    for (String param : parameters)
                    {
                        String[] nameValues = param.split("=");
                        if (nameValues.length == 2)
                        {
                            if (nameValues[0].startsWith("?"))
                            {
                                nameValues[0] = nameValues[0].substring(1);
                            }
                            
                            if (nameValues[0].equals("action"))
                            {
                                String currentSongTitle = PixelClient.currentSongTitle;
                                if (nameValues[1].equals("next"))
                                {
                                    nextAction(currentSongTitle, clientAddress);
                                } 
                                else if (nameValues[1].equals("unpause"))
                                {
                                    app.unpausePlayer();
                                } 
                                else if (nameValues[1].equals("pause"))
                                {
                                    app.pausePlayer();
                                } 
                                else if (nameValues[1].equals("like"))
                                {
                                    like(currentSongTitle, clientAddress);                                    
                                } 
                                else if (nameValues[1].equals(VOLUME_UP) )
                                {
                                    System.out.println("volume up requested, current level: " + systemMediaControler.currentVolume());
                                    int volume = systemMediaControler.currentVolume() + 5;
                                    volume = volume > 100 ? 100 : volume;
                                    System.out.println("proposed new volume level: " + volume);
                                    systemMediaControler.setVolume(volume);
                                    System.out.println("after processing the UP request, current volume level is " + systemMediaControler.currentVolume());
                                } 
                                else if (nameValues[1].equals(VOLUME_DOWN) )
                                {
                                    System.out.println("volume DOWN requested, current level: " + systemMediaControler.currentVolume());
                                    int volume = systemMediaControler.currentVolume() - 5;
                                    volume = volume < 0 ? 0 : volume;
                                    System.out.println("proposed new volume level: " + volume);
                                    systemMediaControler.setVolume(volume);
                                    System.out.println("after processing the DOWN request, current volume level is " + systemMediaControler.currentVolume());
                                }
                            }
                            else
                            {
                                sendPlainText = true;
                                plainText = "no action was given";
                            }
                        }
                    }
                    
                    // send a response
                    if(sendPlainText)
                    {
                        // a response to a JavaScript request
                        sendPlainTextResponse(plainText);
                    }
                    else
                    {
                        boolean includeHeader = true;

                        String uiHtmlath = path + getControlsResourcePath();
                        
//                        System.out.println("loading: " + uiHtmlath);
                        
                        InputStream instream = getClass().getResourceAsStream(uiHtmlath);
                        String html = TextFileReader.readText(instream);

                        if(PixelClient.currentSongTitle != null)
                        {
                            String currentSong = URLDecoder.decode(PixelClient.currentSongTitle);
                            html = html.replace("CURRENT_SONG", currentSong);
                        }

                        int volume = systemMediaControler.currentVolume();
                        html = html.replace(SERVER_VOLUME, "" + volume);

                        sendHttpResponse(html, includeHeader);
                    }
                }
            }

//            System.out.println("message sent for request: " + request);
        } 
        catch (IOException e)
        {
            System.out.println("I/O error " + e);
        } 
        catch (SecurityException se)
        {
            System.out.println("Security error: " + se);
        }
    }    

}
