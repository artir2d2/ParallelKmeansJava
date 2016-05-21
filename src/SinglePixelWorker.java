
import java.awt.Color;
import java.util.Random;

/**
 * Implementation of concurrent class deciding where given pixel on (x,y)
 * position belongs in cluster array.
 * 
 * @author ak
 */
public class SinglePixelWorker implements Runnable {
	public static int counter = 0;
	private int threadID = counter;
	XYContainer xy;
	int xSize, ySize;
	int[] coords = new int[2];
	Cluster[] clusterArray;
	ConcurrentPixelArray imageMatrix;

	public SinglePixelWorker(XYContainer xyContainer, ConcurrentPixelArray imageMatrix, Cluster[] clusterArray, int xSize,
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
		long start = System.nanoTime();
		// TODO Auto-generated method stub
		/**
		 * All computations are enclosed in this loop
		 */
		while (coords[1] < ySize) {
			coords = xy.getXY();
			if (coords[0] % (xSize - 1) == 0 && coords[0] != 0) {// System.out.println(coords[1]+" "+coords[0]);
				xy.setXY(1, coords[1] + 1);
			} else {// System.out.println("podbijam x" + coords[0]);
				xy.setXY(coords[0] + 1, coords[1]);
			}
			if (coords[0] >= xSize || coords[1] >= ySize)
				break;
			/**
			 * Now thread is ready to make computation on the single pixel
			 */
			Pixel pixelBuffer = imageMatrix.getPixel(coords[0], coords[1]);
			int minDist = 255; // minimalny dystans piksela do najbli¿szego
								// centroidu
			int nearestCenter = 0; // liczba reprezentuj¹ca najbli¿szy centroid
			for (int i = 0; i < clusterArray.length; i++) {
				int dR = Math.abs(clusterArray[i].getCentroidComponent("R") - pixelBuffer.getPixelR());
				int dG = Math.abs(clusterArray[i].getCentroidComponent("G") - pixelBuffer.getPixelG());
				int dB = Math.abs(clusterArray[i].getCentroidComponent("B") - pixelBuffer.getPixelB());
				int dist = (dR + dG + dB) / 3;
				if (minDist > dist) { // porównuje obliczon¹ odleg³oœæ z
										// minimaln¹ dotychczasow¹
					minDist = dist;
					nearestCenter = i;
				}
				Random random = new Random();
				try {
					Thread.currentThread().sleep(random.nextInt(2));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clusterArray[nearestCenter].addPixel(pixelBuffer);
		}
		long stop = System.nanoTime();
		System.out.println(threadID + "  " + (stop - start));
	}

}
