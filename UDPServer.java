import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * This class servers as a UDP Server that is constantly listening on port 2000 for a request, and then will
 * respond with a DatagramPacket containing a quote about programming
 * 
 * @author Andrew Tanner
 * @version 1.0
 *
 */
public class UDPServer {
	static DatagramSocket socket; //acts as the socket for requests and responses
	static List<String> quotes = new ArrayList<String>(); //arraylist to hold all quotes
	static Random random = new Random(); //used to generate random number to choose quote to respond
	
	/**
	 * This method runs the UDP Server on a loop which waits for a request and responds with a random quote.
	 * @param args can be used to pass location of quotes file
	 * @throws IOException catches issues with the socket or quotes file
	 */
	public static void main(String[] args) throws IOException {
		
		socket = new DatagramSocket(2000); //the connection to the client
		String pathToQuotes = "";
		
		//if no arguments are passed, assumes file is called quotes.csv and is placed in the root dir
		if(args.length == 0) {
	        pathToQuotes = "/quotes.csv";
		}else
			pathToQuotes = args[0].toString(); //grabs the name and location of the quotes file from the arguments
		
		importQuotes(pathToQuotes);
		DateFormat df = new SimpleDateFormat("hh:mm a 'on' MMMMM d, yyyy");
		Date dateobj = new Date();
		System.out.println("Server started at " + df.format(dateobj) + "\n");
		
		//start listening for requests
		while(true) {
			 DatagramPacket request = new DatagramPacket(new byte[1], 1);
			 socket.receive(request); //waits until request is received
			 
			 //Prints info about each request
			 Date time = new Date();
			 DateFormat rf = new SimpleDateFormat("MM/dd/yyyy h:mma");
			 System.out.println("Request received from " + request.getAddress().toString().replaceAll("/", "") + ": " + request.getPort() + " " + rf.format(time) + "\n");
			 
			 //serves up a hot & fresh quote
			 String quote = quotes.get(random.nextInt(quotes.size())); //grabs a random quote
			 byte[] buffer = quote.getBytes();
			 
			 //grabs info about client to send response to
			 InetAddress address = request.getAddress();
			 int sendPort = request.getPort();
			 
			 //sends response to client
			 DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, sendPort);
			 socket.send(response);
		}
	}//end main
	/**
	 * used to read the quotes.csv file and import them to the quotes arraylist
	 * @param path Path to the quotes files
	 * @throws IOException if quotes file cannot be found
	 */
	public static void importQuotes(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String temp = reader.readLine();
		String[] tempStrings = temp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //use regex to split the quotes in csv file by commas, ignoring commas in quotes
		
		for(String s : tempStrings) {
			quotes.add(s);
		}
	}//end importQuotes
}//end class