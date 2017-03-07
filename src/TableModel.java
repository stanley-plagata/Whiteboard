import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel implements ModelListener {
	private Canvas canvas;
	private static final int NUM_COLUMNS = 4;
	private static final int X_COLUMN = 0;
	private static final int Y_COLUMN = 1;
	private static final int WIDTH_COLUMN = 2;
	
	public TableModel() {
		canvas = null;
	}
	
	public String getColumnName(int column) {
		if (column == X_COLUMN) {
			return "X";
		} else if (column == Y_COLUMN) {
			return "Y";
		} else if (column == WIDTH_COLUMN) {
			return "WIDTH";
		} else {
			return "HEIGHT";
		}
	}
	
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	public int getRowCount() {
		if (canvas != null) {
			return canvas.getShapesList().size();
		} else {
			return 0;
		}
	}

	public Object getValueAt(int x, int y) {
		DShapeModel model = canvas.getShapesList().get(x).getShapeModel();
		if (y == X_COLUMN) {
			return model.getX();
		} else if (y == Y_COLUMN) {
			return model.getY();
		} else if (y == WIDTH_COLUMN) {
			return model.getWidth();
		} else {
			return model.getHeight();
		}
	}

	public void modelChanged(DShapeModel model) {
		ArrayList<DShape> shapes = canvas.getShapesList();
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).getShapeModel() == model) {
				fireTableRowsUpdated(i, i);
				break;
			}
		}
	}
}