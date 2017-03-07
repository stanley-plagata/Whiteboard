import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Canvas extends JPanel implements MouseMotionListener, MouseListener {
	private GUI gui;
	private DShape currentShape;
	private DShapeModel currentShapeModel;
	private ArrayList<DShape> shapesList;
	private ArrayList<Point> knobs;
	private TableModel tableModel;

	private Point K1;
	private Point K2;
	private Point K3;
	private Point K4;
	private Point movingPoint;
	private Point anchorPoint;

	private int offsetX;
	private int offsetY;
	
	private Rectangle R1;
	private Rectangle R2;
	private Rectangle R3;
	private Rectangle R4;

	private Point p;
	private boolean isDragging;
	private boolean isDragging1;
	private boolean isDragging2;
	private boolean isDragging3;
	private boolean isDragging4;
	
	private static final int KNOB_SIZE = 9;
	private static final int BUFFER = 3;
	
	public Canvas(Whiteboard board) {
		super();
		this.setPreferredSize(new Dimension(500,500));
		this.setLayout(new BorderLayout());
		this.setOpaque(true);
	    this.setBackground(Color.WHITE);
	    
	    shapesList = new ArrayList<>();
	    knobs = new ArrayList<>();
	    tableModel = new TableModel();
	    tableModel.setCanvas(this);
	    addMouseMotionListener(this);
	    addMouseListener(this);
	}

	public void setControlPanel(GUI gui) {
		this.gui = gui;
	}
	
	private void defineKnobs() {
		if (getCurrentShape().equals("DLine")) {
			K1 = new Point(getCurrentShape().getBounds().x, getCurrentShape().getBounds().y);
			K4 = new Point((int)getCurrentShape().getBounds().getMaxX(), (int)getCurrentShape().getBounds().getMaxY());
		}
		K1 = new Point(getCurrentShape().getBounds().x, getCurrentShape().getBounds().y);
		K2 = new Point(getCurrentShape().getBounds().x, (int) getCurrentShape().getBounds().getMaxY());
		K3 = new Point((int)getCurrentShape().getBounds().getMaxX(), getCurrentShape().getBounds().y);
		K4 = new Point((int)getCurrentShape().getBounds().getMaxX(), (int) getCurrentShape().getBounds().getMaxY());
	}

	private void createKnobs() {
		if (getCurrentShape().equals("DLine")) {
			R1 = new Rectangle(K1.x, K1.y, KNOB_SIZE, KNOB_SIZE);
			R4 = new Rectangle(K4.x, K4.y, KNOB_SIZE, KNOB_SIZE);
		}
		R1 = new Rectangle(K1.x - BUFFER, K1.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
		R2 = new Rectangle(K2.x - BUFFER, K2.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
		R3 = new Rectangle(K3.x - BUFFER, K3.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
		R4 = new Rectangle(K4.x - BUFFER, K4.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
	
	}

	private void drawKnobs(Graphics2D g2) {
		if(getCurrentShape().getName().equals("DLine")) {
			g2.setColor(Color.BLACK);
			g2.fillRect(K1.x, K1.y , KNOB_SIZE, KNOB_SIZE);
			g2.fillRect(K4.x, K4.y, KNOB_SIZE, KNOB_SIZE);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(K1.x - BUFFER, K1.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
			g2.fillRect(K2.x - BUFFER, K2.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
			g2.fillRect(K3.x - BUFFER, K3.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
			g2.fillRect(K4.x - BUFFER, K4.y - BUFFER, KNOB_SIZE, KNOB_SIZE);
		}
	}

	public void addShape(DShapeModel shapeModel) {
		if (shapeModel instanceof DRectModel) {
			DRect rect = new DRect();
			rect.setShapeModel((DRectModel) shapeModel);
			shapesList.add(rect);
			shapeModel.getListeners().add(shapeModel);
			getTableModel().fireTableDataChanged();
		} else if (shapeModel instanceof DOvalModel) {
			DOval oval = new DOval();
			oval.setShapeModel((DOvalModel) shapeModel);
			shapesList.add(oval);
			shapeModel.getListeners().add(shapeModel);
			getTableModel().fireTableDataChanged();
		} else if (shapeModel instanceof DLineModel) {
			DLine line = new DLine();
			line.setShapeModel((DLineModel) shapeModel);
			shapesList.add(line);
			shapeModel.getListeners().add(shapeModel);
			getTableModel().fireTableDataChanged();
		} else if (shapeModel instanceof DTextModel) {
			DText textLine = new DText();
			textLine.setShapeModel((DTextModel) shapeModel);
			shapesList.add(textLine);
			shapeModel.getListeners().add(shapeModel);
			getTableModel().fireTableDataChanged();
		} 
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		for (int i = 0 ; i < shapesList.size(); i++) {
			DShape d = shapesList.get(i);
			d.draw(g2);
			if (getCurrentShape() != null && shapesList.contains(getCurrentShape())) {
				DShape shape = shapesList.get(shapesList.indexOf(getCurrentShape()));
				if ("DRect".equals(shape.getName())) {
					defineKnobs();
					createKnobs();
					drawKnobs(g2);
				} else if ("DOval".equals(shape.getName())) {
					defineKnobs();
					createKnobs();
					drawKnobs(g2);
				} else if ("DLine".equals(shape.getName())) {
					defineKnobs();
					createKnobs();
					drawKnobs(g2);
				} else if (shape.getName().equals("DText")) {
					defineKnobs();
					if (!(gui.getStatus().getText().equals("Client mode"))) {
						((DTextModel) getCurrentShapeModel()).setFont(((Font) gui.getFontBox().getSelectedItem()).getName());
						((DTextModel) getCurrentShapeModel()).setText(gui.getTextBox().getText());		
						String xmlModel = Server.ObjectToXML(getCurrentShapeModel());
						Server.send("change", xmlModel );
						tableModel.fireTableDataChanged();
					}
					createKnobs();
					drawKnobs(g2);
				}
			}
		}
		repaint();
	}
	
	private Point getPoint() {
		return this.p;
	}
	
	public void saveImage(File file) {
		BufferedImage image = (BufferedImage) createImage(this.getWidth(), this.getHeight());
		Graphics g = image.getGraphics();
		paintAll(g);
		g.dispose();
		if (currentShape == null) {
			currentShape = null;
            currentShapeModel = null;
		}
		try {
			ImageIO.write(image, "png", file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}  
	
	public void mousePressed(MouseEvent e) {
		movingPoint = e.getPoint();
		int x = e.getX();
		int y = e.getY();
		if (getCurrentShape() != null) {
			if (R1 == null || R2 == null || R3 == null || R4 == null) {
				return;
			}
			if (getCurrentShape().contains(e.getPoint())) {
				isDragging = true;
				isDragging1 = false;
				isDragging2 = false;
				isDragging3 = false;
				isDragging4 = false;
				offsetX = x - getCurrentShape().getShapeModel().getX();
				offsetY = y - getCurrentShape().getShapeModel().getY();
			} else if (R1.contains(movingPoint)) {
				isDragging1 = true;
				isDragging = false;
				isDragging2 = false;
				isDragging3 = false;
				isDragging4 = false;
				setAnchorPoint(R4.getLocation());
			} else if (R2.contains(movingPoint)) {
				isDragging2 = true;
				isDragging = false;
				isDragging1 = false;
				isDragging3 = false;
				isDragging4 = false;
				setAnchorPoint(R3.getLocation());
			} else if (R3.contains(movingPoint)) {
				isDragging3 = true;
				isDragging = false;
				isDragging1 = false;
				isDragging2 = false;
				isDragging4 = false;
				setAnchorPoint(R2.getLocation());
			} else if (R4.contains(movingPoint)) {
				isDragging4 = true;
				isDragging = false;
				isDragging1 = false;
				isDragging2 = false;
				isDragging3 = false;
				setAnchorPoint(R1.getLocation());
			}
		}
	}
	
	public void mouseDragged(MouseEvent e)  {
		p = e.getPoint();
		if(getCurrentShape() == null || gui.getStatus().getText().equals("Client mode")) {
			return;
		} else if("DRect".equals(getCurrentShape().getName())) {
			resize();
		} else if("DOval".equals(getCurrentShape().getName())) {
			resize();
		} else if("DText".equals(getCurrentShape().getName())) {
			resize();
		} else if("DLine".equals(getCurrentShape().getName())) {
			resizeLine();
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		for (DShape d : shapesList) {
			if (d.getName().equals("DRect")) {
				DRect rect = (DRect) d;
				if (rect.contains(e.getPoint())) {
					setCurrentShape(rect);
     	    		setCurrentShapeModel(rect.getShapeModel());
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		knobs.add(new Point((int) getCurrentShape().getBounds().getMaxX()- BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point((int) getCurrentShape().getBounds().getMaxX() - BUFFER, (int) getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		break;
     	    	} else {
     	    		gui.disableTextBox();
     	    		setCurrentShape(null);
     	    		setCurrentShapeModel(null);
     	    		getKnobs().clear();
     	    	}
			} else if (d.getName().equals("DOval")) {
     	    	DOval oval = (DOval) d;
     	    	if (oval.contains(e.getPoint())) {
     	    		setCurrentShape(oval);
     	    		setCurrentShapeModel(oval.getShapeModel());
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		knobs.add(new Point((int)getCurrentShape().getBounds().getMaxX() - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point((int)getCurrentShape().getBounds().getMaxX() - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		break;
     	    	} else {
     	    		gui.disableTextBox();
     	    		setCurrentShape(null);
     	    		setCurrentShapeModel(null);
     	    		getKnobs().clear();
     	    	}	
			} else if (d.getName().equals("DLine")) {
     	    	DLine line = (DLine) d;
     	    	if (line.contains(e.getPoint())) {
     	    		setCurrentShape(line);
     	    		setCurrentShapeModel(line.getShapeModel());
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point((int)getCurrentShape().getBounds().getMaxX() - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		break;
     	    	} else {
     	    		gui.disableTextBox();
      	    		setCurrentShape(null);
      	    		setCurrentShapeModel(null);
      	    		getKnobs().clear();
      	    	}
     	  	} else if (d.getName().equals("DText")) {	        
     	    	DText text = (DText) d;
     	    	if (text.contains(e.getPoint())) {	        
     	    		gui.enableTextBox();
     	    		setCurrentShape(text);
     	    		setCurrentShapeModel(text.getShapeModel());
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point(getCurrentShape().getBounds().x - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		knobs.add(new Point((int)getCurrentShape().getBounds().getMaxX() - BUFFER, getCurrentShape().getBounds().y - BUFFER));
     	    		knobs.add(new Point((int)getCurrentShape().getBounds().getMaxX() - BUFFER, (int)getCurrentShape().getBounds().getMaxY() - BUFFER));
     	    		break;
     	    	} else {
     	    		gui.disableTextBox();
      	    		setCurrentShape(null);
      	    		setCurrentShapeModel(null);
      	    		getKnobs().clear();
     	    	}
     	  	}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		movingPoint = null;
		isDragging1 = false;
		isDragging2 = false;
		isDragging3 = false;
		isDragging4 = false;
	}
	
	private void resize() {
		String xmlModel = "";
		p = getPoint();
		int dx = p.x - getCurrentShape().getShapeModel().getX();
		int dy = p.y - getCurrentShape().getShapeModel().getY();
		
		if (isDragging) {
			getCurrentShape().getShapeModel().setX(p.x - offsetX);
			getCurrentShape().getShapeModel().setY(p.y - offsetY);
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
		} else if (isDragging1) {
			int width = getCurrentShape().getShapeModel().getWidth() - dx;
			int height = getCurrentShape().getShapeModel().getHeight() - dy;
			if (R1.x - anchorPoint.x >= 0) {
				isDragging1 = false;
				isDragging3 = true;
			} else if(R1.y - anchorPoint.y >= 0) {
				isDragging1 = false;
				isDragging2 = true;
				width = getCurrentShape().getShapeModel().getWidth() - dx;
				height = dy;
				getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() , width, height);
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				Server.send("change", xmlModel);
				tableModel.fireTableDataChanged();
			} else {
				getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() + dy, width, height);
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				Server.send("change", xmlModel);
				tableModel.fireTableDataChanged();
			}
		} else if(isDragging2) {
			int width = getCurrentShape().getShapeModel().getWidth() - dx;
			int height = dy;
			getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() , width, height);
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
			
			if(R2.x - anchorPoint.x >= 0) {
				isDragging2 = false;
				isDragging4 = true;
			} else if(R2.y - anchorPoint.y <= 0) {
				isDragging2 = false;
				isDragging1 = true;
				int width1 = getCurrentShape().getShapeModel().getWidth() - dx;
				int height1 = getCurrentShape().getShapeModel().getHeight() - dy;
				getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() + dy, width1, height1);
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				Server.send("change", xmlModel);
				tableModel.fireTableDataChanged();
			}	
		} else if(isDragging3 == true) {
			int width = dx;
			int height = getCurrentShape().getShapeModel().getHeight() - dy;
			getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX(), getCurrentShape().getShapeModel().getY() + dy, width, height);
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
			if (R3.x - anchorPoint.x <= 0) {
				int width1 = getCurrentShape().getShapeModel().getWidth() - dx;
				int height1 = getCurrentShape().getShapeModel().getHeight() - dy;
				isDragging3 = false;
				isDragging1 = true;
				getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() + dy, width1, height1);
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				Server.send("change", xmlModel);
				tableModel.fireTableDataChanged();
			} else if(R3.y - anchorPoint.y >= 0) {
				isDragging3 = false;
				isDragging4 = true;
			}
		} else if(isDragging4 == true) {
			int width = dx;
			int height = dy;
			getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX(), getCurrentShape().getShapeModel().getY(), width, height);
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
			if(R4.x - anchorPoint.x <= 0) {
				isDragging4 = false;
				isDragging2 = true;
			} else if(R4.y - anchorPoint.y <= 0) {
				isDragging4 = false;
				isDragging3 = true;
				width = dx;
				height = getCurrentShape().getShapeModel().getHeight() - dy;
				getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX(), getCurrentShape().getShapeModel().getY() + dy, width, height);
				xmlModel = Server.ObjectToXML(getCurrentShapeModel());
				Server.send("change", xmlModel);
				tableModel.fireTableDataChanged();
			}
		}
	}
	
	private void resizeLine() {
		p = getPoint();
		int dx = p.x - getCurrentShape().getShapeModel().getX();
		int dy = p.y - getCurrentShape().getShapeModel().getY();
		
		if(isDragging) {
			getCurrentShape().getShapeModel().setX(p.x - offsetX);
			getCurrentShape().getShapeModel().setY(p.y - offsetY);
			String xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
		} else if(isDragging1 == true) {
			int width = getCurrentShape().getShapeModel().getWidth() - dx;
			int height = getCurrentShape().getShapeModel().getHeight() - dy;
			getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX() + dx, getCurrentShape().getShapeModel().getY() + dy, width, height);
			String xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
		} else if(isDragging4) {
			int width = dx;
			int height = dy;
			getCurrentShape().getShapeModel().setBounds(getCurrentShape().getShapeModel().getX(), getCurrentShape().getShapeModel().getY(), width, height);
			String xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			xmlModel = Server.ObjectToXML(getCurrentShapeModel());
			Server.send("change", xmlModel);
			tableModel.fireTableDataChanged();
		}
	}
	
    public void clear() { 
    	shapesList.clear();
        setCurrentShape(null);
        setCurrentShapeModel(null);
    	repaint();  
    }
	
	public void moveFront(DShapeModel model) {
		DShape shape = null;
		if (!getShapesList().isEmpty()) {
			for (DShape d : getShapesList()) {
				if (d.getShapeModel().getId() == model.getId()) {
					shape = d;
				}
			}
		}
		if (shape != null) {
			getShapesList().remove(getShapesList().indexOf(shape));
			getShapesList().add(shape);
		}
	}
	
	public void moveBack(DShapeModel model) {
		DShape shape = null;
		if (!getShapesList().isEmpty()) {
			for (DShape d : getShapesList()) {
				if (d.getShapeModel().getId() == model.getId()) {
     				  shape = d;
				}
			}
		}
		if (shape != null) {
			getShapesList().remove(getShapesList().indexOf(shape));
			getShapesList().add(0,shape);
		}
	}

	public void removeShape(DShapeModel model) {
		if (!getShapesList().isEmpty()) {
			synchronized(getShapesList()) {
				Iterator<DShape> itr = getShapesList().iterator(); 
				while(itr.hasNext()) { 
					if (itr.next().getShapeModel().getId() == model.getId()) {
						itr.remove();
						break;
					} 
				}
			}
		}
		tableModel.fireTableDataChanged();
	}
	
	public void change(DShapeModel shapeModel) {
		if (!getShapesList().isEmpty()) {
			for (DShape d : getShapesList()) {
				if (d.getShapeModel().getId() == shapeModel.getId()) {
					d.modelChanged(shapeModel);
					tableModel.fireTableDataChanged();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public DShape getCurrentShape() { return currentShape;}
	public void setCurrentShape(DShape currentShape) { this.currentShape = currentShape; }

	public DShapeModel getCurrentShapeModel() { return currentShapeModel; }
	public void setCurrentShapeModel(DShapeModel currentShapeModel) { this.currentShapeModel = currentShapeModel; }
	
	public ArrayList<DShape> getShapesList() { return shapesList; }
	public ArrayList<Point> getKnobs() { return knobs; }

	public TableModel getTableModel() { return tableModel; }
	public void setTableModel(TableModel tableModel) { this.tableModel = tableModel; }
	
	public void setAnchorPoint(Point p) { this.anchorPoint = p; }
	public void setMovingPoint(Point p) { this.movingPoint = p; }
}