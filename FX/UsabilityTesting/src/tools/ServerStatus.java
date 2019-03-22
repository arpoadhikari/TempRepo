package tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * This class is written to check the server and port connectivity
 * @author Arpo Adhikari
 *
 */
public class ServerStatus {
	
	/**
	 * Return the server response message
	 * @param serverAddress
	 * @return Server response message
	 * @throws IOException
	 */
	public String get(String serverAddress) throws IOException {
		
		URL link = new URL(serverAddress);
		String status = null;
		HttpURLConnection connection = (HttpURLConnection) link.openConnection();
		try {
			connection.connect();
			status = connection.getResponseMessage();
			return status;
		} catch (Exception e) {
			return e.getMessage();
		}
		finally {
			connection.disconnect();
		}
	}
	
	/** Check whether a port is free or not
	 * @param portNo
	 * @return Boolean
	 */
	public Boolean isFreePort(String hostName, int portNo) {
		Boolean status = true;
		Socket skt;
		try {
			if (portNo > 0 && portNo <= 65535) {
				skt = new Socket(hostName, portNo);
				System.out.println("Port No. : " + portNo + " is being used by -> " + hostName);
				skt.close();
			}
			status = false;
		} catch (Exception e) {
			//System.out.println("Exception occured : "+ e.getMessage());
		}
		return status;
	}
}
