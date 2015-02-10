
package onebeartoe.juke.network;

import onebeartoe.juke.network.ServerConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//TODO: rename thios to ThreadedJukeServer
public class ThreadedServer extends Thread 
{

	private ServerSocket serverSocket;
	
	ServerConnection connection;	
	
	String serverConnectionClass;

	public int port = 1978;
	
	public String ip;

	public void run() 
	{
		try 
		{			
			serverSocket = new ServerSocket(port);

			InetAddress addr = InetAddress.getLocalHost();

		    // Get IP Address
		    byte[] ipAddr = addr.getAddress();

		    // Convert to dot representation
		    String ipAddrStr = "";
		    for (int i=0; i<ipAddr.length; i++) 
		    {
		        if (i > 0) 
		        {
		            ipAddrStr += ".";
		        }
		        ipAddrStr += ipAddr[i]&0xFF;
		    }
		    
		    ip = ipAddrStr;		    
		    
		    // Get hostname
		    String hostname = addr.getHostName();
			
		    System.out.println(ipAddr.toString() + " - " + hostname + " - " + ipAddrStr);
			
//			String inet = serverSocket.getLocalSocketAddress().toString();
//			InetAddress inetAddress = serverSocket.getInetAddress();			
			
			while (true) 
			{
				Socket client = serverSocket.accept();			
//				InetAddress inetAddress2 = serverSocket.getInetAddress();			

				ServerConnection nextServerConnection = (ServerConnection) connection.clone();				
				nextServerConnection.setClient(client);				
				
				Thread request = new Thread(nextServerConnection);
				request.start();
			}
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		} 
	}

	public void shutDown() 
	{
		try 
		{
			serverSocket.close();
			serverSocket = null;
		} 
		catch (IOException ioe) 
		{
			String errorMessage = ioe.toString();
			System.out.println(errorMessage);			
		}
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(ServerConnection connection) {
		this.connection = connection;
	}

	/**
	 * @param serverConnectionClass the serverConnectionClass to set
	 */
	public void setServerConnectionClass(String serverConnectionClass) {
		this.serverConnectionClass = serverConnectionClass;
	}

}
