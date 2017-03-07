import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class GUI extends JPanel {
	private Canvas canvas;
	private int count = 1;
	
	private JTextField textBox;
	private JComboBox<Font> fontBox;
	private JLabel status;

	public GUI(Canvas canvas) {
		super();
		this.canvas = canvas;
		JPanel shapePanel = new JPanel();
		JPanel colorPanel = new JPanel();
		JPanel fontPanel = new JPanel();
		JPanel movePanel = new JPanel();
		JPanel networkPanel = new JPanel();
		JTextArea textPanel = new JTextArea();
		JPanel tablePanel = new JPanel();
		setStatus(new JLabel("N/A"));
		
		textPanel.setBorder(new LineBorder(Color.gray, 2));
		textPanel.setEditable(false);
		
	    JButton rect = new JButton("Rect");
		rect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DRectModel bounds = new DRectModel(10, 10, 20, 20);	
				bounds.setId(count);
				count++;
				canvas.addShape(bounds);
				if (Whiteboard.server != null) {
					String xmlModel = Server.ObjectToXML(bounds);
					Server.send("add", xmlModel);
				}
			}
		});
		
		JButton oval = new JButton("Oval");
		oval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		
				DOvalModel bounds = new DOvalModel(10, 10, 20, 20);	
				bounds.setId(count);
				count++;
				canvas.addShape(bounds);
				if (Whiteboard.server != null) {
					String xmlModel = Server.ObjectToXML(bounds);
					Server.send("add", xmlModel);
				}
			}
		});
		
	    JButton line = new JButton("Line");
		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DLineModel bounds = new DLineModel(10,10, 20, 20);		
				bounds.setId(count);
				count++;
				canvas.addShape(bounds);
				if (Whiteboard.server != null) {
					String xmlModel = Server.ObjectToXML(bounds);
					Server.send("add", xmlModel);
				}
			}
		});
		
		JButton text = new JButton("Text");
		text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DTextModel bounds = new DTextModel(10,10,20,20,"Hello", "Dialog.plain"); 
				bounds.setId(count);
				count++;
				canvas.addShape(bounds);
				if (Whiteboard.server != null) {
					String xmlModel = Server.ObjectToXML(bounds);
					Server.send("add", xmlModel);
				}
			}
		});

		shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.X_AXIS)); 
		shapePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		shapePanel.add(new JLabel("Add"));
		shapePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		shapePanel.add(rect);
		shapePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		shapePanel.add(oval);
		shapePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		shapePanel.add(line);
		shapePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		shapePanel.add(text);
		shapePanel.add(Box.createRigidArea(new Dimension(5, 40)));
		
		JButton setColor = new JButton("Set Color");
		setColor.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				Color initialcolor = Color.GRAY;
				Color newColor = JColorChooser.showDialog(setColor, "Select a color", initialcolor);
				if (canvas.getCurrentShapeModel() == null) {
					return;
				}
				for (DShapeModel d : canvas.getCurrentShapeModel().getListeners()) {
					if (canvas.getCurrentShapeModel().equals(d)) {
						canvas.getCurrentShapeModel().setColor(newColor);
						canvas.getCurrentShape().modelChanged(canvas.getCurrentShapeModel());
						String xmlModel = Server.ObjectToXML(canvas.getCurrentShape().getShapeModel());
						Server.send("change",xmlModel);
					}
					repaint();
				} 
			}
		});
		
		JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
                if (result != null) {
                    File f = new File(result);
            		try {
                        XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(new FileInputStream(f))); 
                        DShape[] shapeArray = (DShape[]) xmlIn.readObject();
                        xmlIn.close();
                        canvas.clear();
                        String xmlModel = Server.ObjectToXML(canvas.getCurrentShapeModel());
                        Server.send("clear", xmlModel);
                        for(DShape shape : shapeArray) {
                        	canvas.getShapesList().add(shape);
                        	xmlModel = Server.ObjectToXML(shape.getShapeModel());
                        	Server.send("add", xmlModel);
                        }
                		repaint();
                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }
    			canvas.getTableModel().fireTableDataChanged();
			}
		});
		
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
                if (result != null) {
                    File f = new File(result);
                    try {
                        XMLEncoder xmlOut = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(f)));
                        DShape[] shapeArray = canvas.getShapesList().toArray(new DShape[canvas.getShapesList().size()]);
                        xmlOut.writeObject(shapeArray);
                        xmlOut.close();
                    } catch (IOException x) {
                    	x.printStackTrace();
                    }
                }
			}
		});
		
		JButton saveImage = new JButton("Save PNG");
        saveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(canvas.getCurrentShape() != null) {
            		canvas.setCurrentShape(null);
            	}
            	String result = JOptionPane.showInputDialog("File Name", null);
            	if(result != null) {
            		File f = new File(result);
            		canvas.saveImage(f);
            	}
            }
        });

		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
		colorPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		colorPanel.add(setColor);
		colorPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		colorPanel.add(open);
		colorPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		colorPanel.add(save);
		colorPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		colorPanel.add(saveImage);
		colorPanel.add(Box.createRigidArea(new Dimension(5, 40)));
		
		setTextBox(new JTextField(10));
		getTextBox().setMaximumSize(new Dimension(100, getTextBox().getPreferredSize().height));
		setFonts(new JComboBox<Font>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		getFontBox().setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value != null) {
					Font font = (Font) value;
					value = font.getName();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		
		fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.X_AXIS));
		fontPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		fontPanel.add(getTextBox());
		fontPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		fontPanel.add(getFontBox());
		fontPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		this.disableTextBox();
		
		JButton moveFront = new JButton("Move To Front");
		moveFront.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				System.out.println(" ");
				if (canvas.getCurrentShape() != null && !canvas.getShapesList().isEmpty()) {
					String xmlModel = Server.ObjectToXML(canvas.getCurrentShapeModel());
					Server.send("moveFront", xmlModel);
					canvas.getShapesList().remove(canvas.getShapesList().indexOf(canvas.getCurrentShape()));
					canvas.getShapesList().add(canvas.getCurrentShape());
				}
			}
		});
		
		JButton moveBack = new JButton("Move To Back");
		moveBack.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				System.out.println(" ");
				if (canvas.getCurrentShape() != null && !canvas.getShapesList().isEmpty()) {
					String xmlModel = Server.ObjectToXML(canvas.getCurrentShapeModel());
					Server.send("moveBack", xmlModel);
					canvas.getShapesList().remove(canvas.getShapesList().indexOf(canvas.getCurrentShape()));
					canvas.getShapesList().add(0,canvas.getCurrentShape());
				}
			}
		});
		
		JButton remove = new JButton("Remove Shape");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (canvas.getCurrentShape() != null && canvas.getShapesList().contains(canvas.getCurrentShape())) {
					String xmlModel = Server.ObjectToXML(canvas.getCurrentShapeModel());
					Server.send("remove",xmlModel);
					canvas.getCurrentShapeModel().getListeners().remove(canvas.getCurrentShapeModel());
					canvas.getShapesList().remove(canvas.getCurrentShape());
					repaint();
				}
				canvas.getTableModel().fireTableDataChanged();
			}
		});

		movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.X_AXIS));
		movePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		movePanel.add(moveFront);
		movePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		movePanel.add(moveBack);
		movePanel.add(Box.createRigidArea(new Dimension(5, 0)));
		movePanel.add(remove);
		movePanel.add(Box.createRigidArea(new Dimension(5, 40)));
		
		JButton server = new JButton("Server Start");
		server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getStatus().getText().equals("Client mode")) {
					JOptionPane.showMessageDialog(null, "Whiteboard is already running in server mode: unable to change to client.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				if (getStatus().getText().equals("Server mode")) {
					JOptionPane.showMessageDialog(null, "Server has already been started on this whiteboard.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					String result = JOptionPane.showInputDialog("Start server on port:", "39587");
					try {
						int port = Integer.parseInt(result);
						if (port > 65535 || port < 0) {
							throw new IllegalArgumentException();
						}
							System.out.println("server: start");
							getStatus().setText("Server mode");
							Server server = new Server(canvas, port);
							Whiteboard.server = server;
							server.start();
					} catch(IllegalArgumentException x) {
						JOptionPane.showMessageDialog(null, "Enter a port between 0 and 65535", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				canvas.getTableModel().fireTableDataChanged();
			}
		});
		
		JButton client = new JButton("Client Start");
		client.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getStatus().getText().equals("Server mode")) {
					JOptionPane.showMessageDialog(null, "Whiteboard is already running in client mode: unable to change to server.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					String result = JOptionPane.showInputDialog("Connect to host:port", "127.0.0.1:39587");
					if (Server.getServerSocket() == null) {
	                     JOptionPane.showMessageDialog(null, "Unable to locate server" , "Error", JOptionPane.ERROR_MESSAGE);
	                     return;
	                }
					try {
						String[] parts = result.split(":");
						int port = Integer.parseInt(parts[1].trim());
						if (port > 65535 || port < 0) {
							throw new IllegalArgumentException();
						}
						getStatus().setText("Client mode");
						canvas.clear();
					    rect.setEnabled(false);
					    oval.setEnabled(false);
					    line.setEnabled(false);
					    text.setEnabled(false);
						setColor.setEnabled(false);
						open.setEnabled(false);
						save.setEnabled(false);
						saveImage.setEnabled(false);
						moveFront.setEnabled(false);
						moveBack.setEnabled(false);
					    remove.setEnabled(false);
						server.setEnabled(false);
						client.setEnabled(false);
						getTextBox().setEnabled(false);
						getFontBox().setEnabled(false);
						Client client = new Client(canvas,parts[0].trim(), port);
						client.start();
					} catch (Exception x) {
						JOptionPane.showMessageDialog(null, "Invalid IP Address", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				canvas.getTableModel().fireTableDataChanged();
			}
		});
		
		networkPanel.setLayout(new BoxLayout(networkPanel, BoxLayout.X_AXIS));
		networkPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		networkPanel.add(server);
		networkPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		networkPanel.add(client);
		networkPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		networkPanel.add(getStatus());
		networkPanel.add(Box.createRigidArea(new Dimension(5, 50)));
		
		tablePanel.setLayout(new BorderLayout());
		JTable table = new JTable(canvas.getTableModel());
		JScrollPane tableContainer = new JScrollPane(table);
		tablePanel.add(tableContainer);
		tableContainer.setPreferredSize(new Dimension());
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(shapePanel);
		this.add(colorPanel);
		this.add(fontPanel);
		this.add(movePanel);
		this.add(networkPanel);
		this.add(textPanel);
		this.add(tablePanel);
		
		for (Component comp: this.getComponents()) {
			((JComponent)comp).setAlignmentX(LEFT_ALIGNMENT); // aligns all the panels to the left margin.
		}
	}
	
	public void enableTextBox() {
		getTextBox().setEnabled(true);
		getFontBox().setEnabled(true);
	}
	
	public void disableTextBox() {
		getTextBox().setEnabled(false);
		getFontBox().setEnabled(false);
	}
	
	public JTextField getTextBox() { return textBox; }
	public void setTextBox(JTextField textBox) { this.textBox = textBox; }

	public JComboBox<Font> getFontBox() { return fontBox; }
	public void setFonts(JComboBox<Font> fontBox) { this.fontBox = fontBox; }

	public JLabel getStatus() { return status; }
	public void setStatus(JLabel status) { this.status = status; }
}