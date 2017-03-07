import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client extends Thread {
	private Canvas canvas;
	private String host;
	private int port;

	public Client(Canvas canvas, String host, int port) {
		this.canvas = canvas;
		this.host = host;
		this.port = port;
	}
	
	public void run() {
		try {
			@SuppressWarnings("resource")
			Socket toServer = new Socket(host, port);
			ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());
			System.out.println("client: connected on port " + port);
				while (true) {
					String verb = (String) in.readObject();
					String xmlString = (String) in.readObject();
					XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
					DShapeModel model = (DShapeModel) decoder.readObject();
					decoder.close();
					if (verb.equals("add")) {       	   
						canvas.addShape(model);     	   
					} else if (verb.equals("remove")) {
						canvas.removeShape(model);
					} else if (verb.equals("moveFront")) {
						canvas.moveFront(model);
					} else if (verb.equals("moveBack")) {
						canvas.moveBack(model);                
					} else if (verb.equals("change")) {
						canvas.change(model);
					} else if (verb.equals("clear")) {
	                    canvas.clear();
	                }
				}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid port", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}