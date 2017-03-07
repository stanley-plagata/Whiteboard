public class DTextModel extends DShapeModel {
	private String text;
    private String font;
    
    public DTextModel() {
    	
    }
    
	public DTextModel(int x , int y, int width, int height, String text, String font) {
		super(x, y, width, height);
		this.text = text;
		this.font = font;
	}

	public String getText() { return text; }
	public void setText(String newText) { text = newText; }
	
	public String getFont() { return font; }
	public void setFont(String font) { this.font = font; }
}