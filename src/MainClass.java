import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class MainClass {
	ConcurrentPixelArray pixelArray;
	BufferedImage buff;
	Cluster clusterArray[];
	XYContainer xyContainer;
	int xSize;
	int ySize;
	/**
	 * 
	 * @param imagePath
	 * @param clusterCount
	 */
	public MainClass(String imagePath, int clusterCount, int threadCount) {
		long start = System.nanoTime();
		loadImageIntoConcurrentResource(loadImage(imagePath));
		this.clusterArray = new Cluster[clusterCount];
		this.xyContainer = new XYContainer();
		setRandomClusterCentroids();
		long stop = System.nanoTime();
		System.out.println("Koniec. Czas: " + (stop - start));
		/**
		 * Heres all magic
		 */
		doKmeans(threadCount);
		/**
		 * End of magic
		 */
		start = System.nanoTime();
		saveImage(writePixelArrayIntoBufferedImage(), "output");
		stop = System.nanoTime();
		System.out.println("Koniec. Czas: " + (stop - start));
	}
	private void doKmeans(int threadCount){
		
		int globalChange=0;
		boolean centerChange[] = new boolean[clusterArray.length];
		int conts=0;
		while(globalChange<1){
			conts++;
			System.out.println("ITERATION COUNT: "+ conts);
			for(int i=0;i<clusterArray.length;i++){ //resetujê tablice pikseli przynale¿¹cych do centroidów
				clusterArray[i].resetArray();
			}
			/**
			 * HERE ALL THREADS WORKS
			 */
			ExecutorService exec= Executors.newCachedThreadPool();
			for(int i=0;i<threadCount;i++){
				exec.submit(new PixelWorker(xyContainer, clusterArray, xSize, ySize));
			}
			try {
				exec.shutdown();
				exec.awaitTermination(5, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.xyContainer = new XYContainer();

			/**
			 * END OF THREAD WORKING
			 */
			
			for(int i=0;i<clusterArray.length;i++){
				int cent = clusterArray[i].newCentroid();
				if(cent == 1) centerChange[i] = true;
				else centerChange[i] = false;
			}
			for(int i=0;i<clusterArray.length;i++){
				if(centerChange[i] == true) break;
				//globalChange = false;
			}
			globalChange++;
		}
	}
	/**
	 * Loads image from file and return Buffered image object of that image
	 * 
	 * @param filePath
	 *            - path to image file
	 * @return - image object
	 */
	private BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Loads BufferedImage into an 2d array of pixels for easier future
	 * processing
	 * 
	 * @param image
	 */
	private void loadImageIntoConcurrentResource(BufferedImage image) {
		this.xSize = image.getWidth();
		this.ySize = image.getHeight();
		pixelArray = new ConcurrentPixelArray(xSize,ySize);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixelArray.setPixel(new Pixel(x, y, image.getRGB(x, y)));
			}
		}
		System.out.println("Image loaded into ConcurrentPixelArray");
	}
	/**
	 * Saves the pixelArray as image in .PNG format
	 * @param image
	 * @param name
	 */
	private void saveImage(BufferedImage image, String name){
		File output = new File(name+".png");
		try {
			ImageIO.write(image, "PNG", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Image saved to file!");
	}
	
	private BufferedImage writePixelArrayIntoBufferedImage(){ //ATTENTION x OR x-1 !!!!
		BufferedImage bf = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
		setNewColorsOnPixelArray();
		for(int x = 0; x<xSize;x++){
			for(int y =0;y<ySize;y++){
				bf.setRGB(x, y, pixelArray.getPixel(x, y).getPixelColor().getRGB());
			}
		}
		System.out.println("pixelArray succefully writen into BufferedImage");
		return bf;
	}
	
	private void setNewColorsOnPixelArray(){
		for(int i=0;i<clusterArray.length;i++){
//			System.out.println(clusterArray[i].getCentroidComponent("R")+"  "+clusterArray[i].getCentroidComponent("G")+"  "+clusterArray[i].getCentroidComponent("B"));
			for(int j=0;j<clusterArray[i].getClusterSize();j++){
				Pixel buffer = clusterArray[i].getPixel(j);
				buffer.setPixelColor(clusterArray[i].getClusterRGB());
				pixelArray.setPixel(buffer);
			}
		}
	}

	/**
	 * Generating new clusters containing no pixels but with random Centroid
	 * color
	 */
	public void setRandomClusterCentroids() {
		Random random = new Random();
		for (int i = 0; i < clusterArray.length; i++){
			clusterArray[i] = new Cluster(new Pixel(0,0,new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)).getRGB())); 	
		}
			
	}
	
	public static void main(String []args){
		MainClass mc = new MainClass("C:\\Users\\Dupasraka\\Desktop\\mapyevol\\349_max.jpg",6,50);
		System.out.println(PixelWorker.max);
	}

}
