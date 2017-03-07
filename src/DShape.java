import java.awt.*;
import java.awt.geom.Point2D;

public abstract class DShape implements ModelListener {
	protected DShapeModel shapeModel;
	
	public DShape() {
		shapeModel = new DShapeModel();
	}
	
	
	public Rectangle getBounds() {
		return shapeModel.getBounds();
	}
	
	public boolean contains(Point2D p) {
		return p.getX() < this.shapeModel.getWidth() +  this.shapeModel.getX() - 1
				&& p.getX() >  this.shapeModel.getX() + 1
				&& p.getY() <  this.shapeModel.getHeight() +  this.shapeModel.getY() - 1
				&& p.getY() >  this.shapeModel.getY() + 1;
	}

	@Override
	public void modelChanged(DShapeModel model) {
		shapeModel = model;
	}
	
	public DShapeModel getShapeModel() { return shapeModel; }
	
	public void setShapeModel(DShapeModel shapeModel) { this.shapeModel = shapeModel; }
	
	public abstract void draw(Graphics g);
	
	public String getName() {
		return "DShape";
	}
}