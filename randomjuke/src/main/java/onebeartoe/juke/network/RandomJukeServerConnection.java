
package onebeartoe.juke.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;

import onebeartoe.juke.ui.RandomJuke;
import org.onebeartoe.io.TextFileReader;
import org.onebeartoe.multimedia.BasicMultimediaController;
import org.onebeartoe.multimedia.MultimediaController;
import org.onebeartoe.multimedia.juke.services.CurrentSongService;
import org.onebeartoe.network.ServerConnection;

public class RandomJukeServerConnection extends ServerConnection
{
    private JButton nextSongButton;

    private int POST_SIZE = 1024;

    private CurrentSongService currentSongService;

    private String VOLUME_UP = "volume-up";

    private String VOLUME_DOWN = "volume-down";

    private String SERVER_VOLUME = "SERVER_VOLUME";

    private RandomJuke app;

    @Override
    public void run()
    {
        try
        {
            InetAddress inetAddress = client.getInetAddress();
            String clientAddress = inetAddress.toString();
            System.out.println(clientAddress + " is connecting");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "8859_1"));

            String request = in.readLine();

            System.out.println("Request: " + request);

            Pattern pattern = Pattern.compile("(GET|POST) /?(\\S*).*");

            Matcher httpRequestMatcher = pattern.matcher(request);
            if (!httpRequestMatcher.matches())
            {
                invalidRequest(request);
            } else
            {
                MultimediaController systemMediaControler = new BasicMultimediaController();
                String path = "/onebeartoe/juke/ui/";

                request = httpRequestMatcher.group(2);

                if (request.equals("style.css"))
                {
                    InputStream instream = getClass().getResourceAsStream(path + "style.css");
                    String html = TextFileReader.readText(instream);
                    boolean includeHeader = false;
                    sendHttpResponse(html, includeHeader);
                } else if (request.equals("layout.css"))
                {
                    InputStream instream = getClass().getResourceAsStream(path + "layout.css");
                    String html = TextFileReader.readText(instream);
                    boolean includeHeader = false;
                    sendHttpResponse(html, includeHeader);
                } else
                {
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
                                String currentSongTitle = RandomJuke.currentSongTitle;
                                if (nameValues[1].equals("next"))
                                {
                                    try
                                    {
                                        boolean next = currentSongService.next(currentSongTitle, clientAddress);
                                        if (next)
                                        {
                                            nextSongButton.doClick();
                                        }
                                    } 
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
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
                                    try
                                    {
                                        currentSongService.like(currentSongTitle, clientAddress);
                                    } 
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
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
                        }
                    }
                }

                String heaersMessage = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n";
                heaersMessage += "\r\n";

                boolean includeHeader = true;
                String uiHtmlath = path + "remote-control.html";
                InputStream instream = getClass().getResourceAsStream(uiHtmlath);
                String html = TextFileReader.readText(instream);

                if (RandomJuke.currentSongTitle != null)
                {
                    String currentSong = URLDecoder.decode(RandomJuke.currentSongTitle);
                    html = html.replace("CURRENT_SONG", currentSong);
                }

                int volume = systemMediaControler.currentVolume();
                html = html.replace(SERVER_VOLUME, "" + volume);

                sendHttpResponse(html, includeHeader);
            }

//            client.close();
            System.out.println("message sent for request: " + request);
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

    public void setNextSongButton(JButton nextSongButton)
    {
        this.nextSongButton = nextSongButton;
    }

    public void setCurrentSongService(CurrentSongService currentSongService)
    {
        this.currentSongService = currentSongService;
    }

    public void setApp(RandomJuke app)
    {
        this.app = app;
    }

}
