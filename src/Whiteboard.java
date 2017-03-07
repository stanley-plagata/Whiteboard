import java.awt.*;
import javax.swing.*;

public class Whiteboard extends JFrame {
	public static Server server;
	
	public Whiteboard() {
		JFrame frame = new JFrame("Whiteboard");
	    frame.setLayout(new BorderLayout());
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    Canvas canvas = new Canvas(this);
	    frame.add(canvas, BorderLayout.CENTER);
	    
	    GUI gui = new GUI(canvas);
	    canvas.setControlPanel(gui);
	    gui.setLayout(new BoxLayout(gui, BoxLayout.Y_AXIS));
	    frame.add(gui, BorderLayout.WEST);

	    frame.pack();
	    frame.setVisible(true);
	}
	
	public static void main(String args[]) {
		int whiteboardCount = 2;
		for (int i = 0; i < whiteboardCount; i++) {
			new Whiteboard();
		}
	}
}