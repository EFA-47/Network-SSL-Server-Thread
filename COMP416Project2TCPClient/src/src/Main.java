package src;

import java.io.*;
import java.net.*;

public class Main {

	public static void main(String[] args) throws IOException{
		
		String host = "localhost";
		int port = 4445;
		String message = "71527COMP416";
		try (
		        Socket echoSocket = new Socket (host, port );
		           
		    ){
			PrintWriter out = new PrintWriter ( echoSocket.getOutputStream (), true );
            System.out.println("Client socket: " + echoSocket.getRemoteSocketAddress());
            out.println(message);
            System.out.println("sent message " + message);
		}
	}
		// TODO Auto-generated method stub
	   

}
