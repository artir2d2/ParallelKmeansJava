public class ConcurrentPixelArray {
	private Pixel [][]concurrentResource;
	ConcurrentPixelArray(int xSize, int ySize){
		concurrentResource = new Pixel[xSize][ySize];
	}
	
	public void setPixel(Pixel pixel){
		concurrentResource[pixel.getX()][pixel.getY()] = pixel;
	}
	
	public Pixel getPixel(int x, int y){
		return concurrentResource[x][y];
	}
}
