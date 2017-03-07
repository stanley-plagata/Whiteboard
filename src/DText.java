import java.awt.*;

public class DText extends DShape {
    private int ascent;
    
	public DText() {
		super();
	}

	public Font computeFont(Graphics g) {
		double size = 1.0;
		double previousFontSize = 1.0;
		Font currentFont = new Font(((DTextModel) shapeModel).getFont(), Font.PLAIN, (int) size);
		FontMetrics metrics = new FontMetrics(currentFont) { };
		while (metrics.getHeight() <= shapeModel.getHeight()) {	
			previousFontSize = size;
			size = (size * 1.10) + 1;
			Font biggerFont = currentFont.deriveFont((float)size);
			metrics = new FontMetrics(biggerFont) {};
			ascent = metrics.getAscent();
		}
		return currentFont.deriveFont((float) previousFontSize);
	} 
	
	public void draw(Graphics g) {	
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    ((Graphics2D) g).setRenderingHints(rh);
	    
	    Font f = computeFont(g);
	    g.setFont(f);
	    g.setColor(shapeModel.getColor());
	    
	    Shape clip = g.getClip();
	    g.setClip(clip.getBounds().createIntersection(getBounds()));
	    g.drawString(((DTextModel) shapeModel).getText(), shapeModel.getX() , shapeModel.getY() + shapeModel.getHeight() - ascent / 5);
	    g.setClip(clip);
	}
	
	public String getName() {
		return "DText";
	}
}