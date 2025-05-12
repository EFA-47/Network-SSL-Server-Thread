
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {
	
	private final static String SERVER_KEYSTORE_FILE = "keystore.jks";
    private final static String SERVER_KEYSTORE_PASSWORD = "storepass";
    private final static String SERVER_KEY_PASSWORD = "keypass";
    private SSLServerSocket sslServerSocket;
    private SSLServerSocketFactory sslServerSocketFactory;

	public static void main(String[] args) {
        startEchoServerSSL(4444);
        startEchoServerTCP(4445);
    }
	
	private static void startEchoServerSSL(int port) {
	    new Thread(() -> {
	        try {
	            SSLContext sslContext = SSLContext.getInstance("TLS");

	            char ksPass[] = SERVER_KEYSTORE_PASSWORD.toCharArray();
	            KeyStore ks = KeyStore.getInstance("JKS");
	            ks.load(new FileInputStream(SERVER_KEYSTORE_FILE), ksPass);
	            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	            kmf.init(ks, SERVER_KEY_PASSWORD.toCharArray());
	            sslContext.init(kmf.getKeyManagers(), null, null);

	            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
	            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

	            System.out.println("SSL server is up and running on port " + port);

	            while (true) {
	                // Accept incoming connections
	                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
	                System.out.println("An SSL connection was established with a client on the address of " + sslSocket.getRemoteSocketAddress());

	                // Handle the connection in a separate thread
	                new Thread(() -> handleSSLConnection(sslSocket)).start();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }).start();
	}
	
	private static void handleSSLConnection(SSLSocket sslSocket) {
	    try {
	    	long startTime = System.currentTimeMillis();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
	        PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);

	        String line = reader.readLine();
	        writer.println("server_ack");
	        System.out.println("Client " + sslSocket.getRemoteSocketAddress() + " sent: " + line);
	        
	        long endTime = System.currentTimeMillis();
	        long delay = endTime - startTime;
	        System.out.println("delay was " + delay + " on secure connection\n");
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            sslSocket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
	private static void startEchoServerTCP(int port) {
	    new Thread(() -> {
	        try {
	        	
	            ServerSocket serverSocket = new ServerSocket(port);
	            System.out.println("Echo server started on port " + port);

	            while (true) {
	                // Accept incoming connections
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Accepted connection on port " + port);
	                
	                
	                // Handle the connection in a separate thread
	                long startTime = System.currentTimeMillis();
	                handleConnection(clientSocket);
	                long endTime = System.currentTimeMillis();
	    	        long delay = endTime - startTime;
	    	        System.out.println("delay was " + delay + " on unsecure connection\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }).start();
	}
	
	private static void handleConnection(Socket clientSocket) {
        try {
           BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           String message = in.readLine();
           System.out.println("TCP sent: " + message);
           

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
