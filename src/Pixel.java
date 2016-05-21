import java.awt.Color;

public class Pixel {
	private Color color;
	private int xPos;
	private int yPos;

	public Pixel(int xPos, int yPos, int rgb) {
		this.color = new Color(rgb);
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public Pixel(Color rgb) {
		this.color = rgb;
		this.xPos = 0;
		this.yPos = 0;
	}
	
	public synchronized int getPixelR() {
		return this.color.getRed();
	}

	public synchronized int getPixelG() {
		return this.color.getGreen();
	}

	public synchronized int getPixelB() {
		return this.color.getBlue();
	}

	public synchronized Color getPixelColor() {
		return this.color;
	}
	public synchronized void setPixelColor(Color color){
		this.color = color;
	}
	public int getY(){
		return this.yPos;
	}
	public int getX(){
		return this.xPos;
	}

}
