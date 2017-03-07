import java.awt.*;
import java.util.ArrayList;

public class DShapeModel {
	private ArrayList<DShapeModel> listeners;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;
	private int id;
	
	public DShapeModel() {
			
	}
	
	public DShapeModel(int x, int y, int width, int height) {
		listeners = new ArrayList<>();
		this.x = x;
		this.y = y;
	    this.width = width;
	    this.height = height;
	    color = Color.GRAY;
	    id = 1;
	}
	
	public boolean equals(DShapeModel d) {
		return d.getX() == this.x
				&& d.getY() == this.y
				&& d.getWidth() == this.width
				&& d.getHeight() == this.height
				&& d.getColor().equals(this.color)
				&& d.getId() == this.id;
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setLocation(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public ArrayList<DShapeModel> getListeners() { return listeners; }
	public void setListeners(ArrayList<DShapeModel> listeners) { this.listeners = listeners; }

	public int getX() { return x; }
	public void setX(int x) { this.x = x; }

	public int getY() { return y; }
	public void setY(int y) { this.y = y; }

	public int getWidth() { return width; }
	public void setWidth(int width) { this.width = width; }

	public int getHeight() { return height; }
	public void setHeight(int height) { this.height = height; }
	
	public Rectangle getBounds() { return new Rectangle(this.x, this.y, this.width, this.height); }
	public void setBounds(int x, int y, int width, int height) { this.x = x; this.y = y; this.width = width; this.height = height; }

	public Color getColor() { return color; }
	public void setColor(Color c) { this.color = c; }

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
}