import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Servers as a UDPClient that will allow the user to request a quote from a UDP Server running on localhost port 2000
 * 
 * @author Andrew Tanner
 * @version 1.0
 *
 */
public class UDPClient {
	static Boolean isRunning = true; //keeps track of if the client is continuing to run
	static Scanner scan = new Scanner(System.in); //gets keyboard input
	static Boolean isReceiving = true; //tracks if the client should still be expecting a response from the Server
	
	/**
	 * Runs a UDP Client on a loop and takes actions based on user input
	 * 
	 * @param args unused
	 * @throws IOException catches if there is an issue with the socket or Datagram Packet
	 */
	public static void main(String[] args) throws IOException {
		int port = 2000;
		String host = "localhost";
		
		//sets the host of the server to the passed IP address, if none is passed it uses localhost
		if(args.length > 0) {
			host = args[0];
		}
		
		//sets the port of the server it's looking for to the second passed argument. If none is passed it uses 2000
		if(args.length > 1) {
			port = Integer.parseInt(args[1]);
		}
		
		while(isRunning) {
			System.out.print("Enter a command: ");
			String temp = scan.nextLine();
			System.out.println();
			
			if(temp.equals("<REQUESTQUOTE>")) {
				//send request
				isReceiving = true;
				DatagramSocket socket = new DatagramSocket();
				InetAddress hostname = InetAddress.getByName(host);
				
				while(isReceiving) {
					DatagramPacket request = new DatagramPacket(new byte[1], 1, hostname, port);
					socket.send(request);
					
					byte[] buffer = new byte[512];
					DatagramPacket response = new DatagramPacket(buffer, buffer.length);
					socket.receive(response);
					
					String received = new String(buffer, 0, response.getLength());
					
					if(!(received.equals("") || received.equals(" "))){
						isReceiving = false;
					}
					
					System.out.println(received);
					System.out.println();
				}
			}
			else if(temp.equals("<END>")) {
				isRunning = false;
				System.out.println("Client Closed");
			}
			else {
				System.out.println("Unknown command.");
			}
		}
	}//end main
}//end class
