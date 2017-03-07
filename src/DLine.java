import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.geom.*;

public class DLine extends DShape {
	Line2D.Double line = new Line2D.Double();
	
	public DLine() {
		super();
	}
	
	public void draw(Graphics g) {	
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(shapeModel.getColor());
		line.setLine(shapeModel.getX(), shapeModel.getY(), shapeModel.getWidth() + shapeModel.getX(), shapeModel.getHeight() + shapeModel.getY());
        g2.draw(line);
	}
	
	public boolean contains(Point2D p) {
		int width = 4;
		int height = 4;
		int xRec = (int) p.getX() - width / 2;
		int yRec = (int) p.getY() - height / 2;
		return line.intersects(xRec, yRec, width, height);
	}
	
	public String getName() {
		return "DLine";
	}
}
