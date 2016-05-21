import java.awt.Color;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.lang.management.*;
/**
 * Implementation of concurrent class deciding where given pixel on (x,y)
 * position belongs in cluster array.
 * 
 * @author ak
 */
public class PixelWorker implements Runnable {
	public static int counter = 0;
	public static long max = 0;
	private int threadID = counter;
	XYContainer xy;
	int xSize, ySize;
	int[] coords = new int[2];
	Cluster[] clusterArray;
	ConcurrentPixelArray imageMatrix;
	long elapsedTime;

	public PixelWorker(XYContainer xyContainer, Cluster[] clusterArray, int xSize,
			int ySize) {
		counter++;
		this.xy = xyContainer;
		this.xSize = xSize;
		this.ySize = ySize;
		this.clusterArray = clusterArray;
		this.imageMatrix = imageMatrix;
	}

	@Override
	public void run() {
		CountDownLatch a;
		long start = System.nanoTime();
		// TODO Auto-generated method stub
		/**
		 * All computations are enclosed in this loop
		 */
		while (coords[1] < ySize) {
			coords = xy.getXY();
			xy.setXY(0, coords[1] + 1);
			if (coords[0] >= xSize || coords[1] >= ySize)
				break;
			/**
			 * Now thread is ready to make computation on the single pixel
			 */
			for(int x=0;x<xSize;x++){
				Pixel pixelBuffer = imageMatrix.getPixel(x, coords[1]);
				int minDist = 255; // minimalny dystans piksela do najbli¿szego centroidu
				int nearestCenter = 0; // liczba reprezentuj¹ca najbli¿szy centroid
				for (int i = 0; i < clusterArray.length; i++) {
					int dR = Math.abs(clusterArray[i].getCentroidComponent("R") - pixelBuffer.getPixelR());
					int dG = Math.abs(clusterArray[i].getCentroidComponent("G") - pixelBuffer.getPixelG());
					int dB = Math.abs(clusterArray[i].getCentroidComponent("B") - pixelBuffer.getPixelB());
					int dist = (dR + dG + dB) / 3;
					if (minDist > dist) { // porównuje obliczon¹ odleg³oœæ z minimaln¹ dotychczasow¹
						minDist = dist;
						nearestCenter = i;
					}
				}
				clusterArray[nearestCenter].addPixel(pixelBuffer);
			}
		}
		long stop = System.nanoTime();
		if(ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()>max){
			max =ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		}
	}

}
