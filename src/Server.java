import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Server extends Thread {
	private Canvas canvas;
	private int port;

	private static ArrayList<ObjectOutputStream> clientStreams = new ArrayList<>();
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private final int DEFAULT_PORT = 39587;
	
	public Server() {
		port = DEFAULT_PORT;
		try {
			setServerSocket(new ServerSocket(port));
			System.out.println("Connected to port " + port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to port " + port, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);	
		}
	}
	
	public Server(Canvas canvas, int port) {
		this.canvas = canvas;
		this.port = port;
		try {
			setServerSocket(new ServerSocket(port));
			System.out.println("Connected to port " + port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to port " + port, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);	
		}
	}
	
	public void run() {
		while (true) {
			try {
	        	clientSocket = getServerSocket().accept();
	        	ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
	        	clientStreams.add(output);
	
	        	System.out.println("New client: " + clientSocket.getPort());
	        	for (DShape shape : canvas.getShapesList()) {
	        		DShapeModel model = shape.getShapeModel();
	        		String xmlModel = ObjectToXML(model);
	        		output.writeObject("add");
	        		output.writeObject(xmlModel);
	        	}
			} catch (IOException e) {
	        	System.out.println("Unable to accept " + port);
	        	System.exit(-1);	
			}
	    }
	}
	
	public static String ObjectToXML(DShapeModel d) {
		String xml = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(baos);
		xmlEncoder.writeObject(d);
		xmlEncoder.close();
		xml = baos.toString();
		return xml;
	}

	public static void send(String command, String xmlModel) {
		for (int i = 0; i < clientStreams.size(); i++) {
			if (getServerSocket() != null && !getServerSocket().isClosed() && clientSocket != null) {
				try {
					clientStreams.get(i).writeObject(command);
					clientStreams.get(i).writeObject(xmlModel);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Error while sending data", "Error", JOptionPane.ERROR_MESSAGE);
				}
		    }
		}
	}

	public static ServerSocket getServerSocket() {
		return serverSocket;
	}

	public static void setServerSocket(ServerSocket serverSocket) {
		Server.serverSocket = serverSocket;
	}
}