import java.awt.Color;
import java.util.ArrayList;

public class Cluster {
	private Pixel centroid;
	private ArrayList<Pixel> clusterPixels;
	public Cluster(Pixel centroid){
		this.centroid = centroid;
		clusterPixels = new ArrayList<Pixel>();
	}
	
	public Color getClusterRGB(){
		return centroid.getPixelColor();
	}
	
	public int getClusterSize(){
		return clusterPixels.size();
	}
	
	public void resetArray(){
		this.clusterPixels.clear();
	}
	
	public synchronized void addPixel(Pixel pixel){
		clusterPixels.add(pixel);
	}
	
	public synchronized Pixel getPixel(int i){
		return clusterPixels.get(i);
	}
	
	public int getCentroidComponent(String component){
		switch(component){
		case"R":
			return this.centroid.getPixelR();
		case"G":
			return this.centroid.getPixelG();
		case"B":
			return this.centroid.getPixelB();
		}
		return 0;
	}
	
	public synchronized int newCentroid(){
		int newCentroidR = 0;
		int newCentroidG = 0;
		int newCentroidB = 0;
		for(int i=0;i<this.clusterPixels.size();i++){
			newCentroidR = newCentroidR + clusterPixels.get(i).getPixelR();
			newCentroidG = newCentroidG + clusterPixels.get(i).getPixelG();
			newCentroidB = newCentroidB + clusterPixels.get(i).getPixelB();
		}
		try{
			newCentroidR = newCentroidR/this.clusterPixels.size();
			newCentroidG = newCentroidG/this.clusterPixels.size();
			newCentroidB = newCentroidB/this.clusterPixels.size();
		}catch(ArithmeticException e){
			newCentroidR = 0;
			newCentroidG = 0;
			newCentroidB = 0;
		}
		
		if(newCentroidR != centroid.getPixelR() || newCentroidG != centroid.getPixelG() || newCentroidB != centroid.getPixelB() ){
			//System.out.println(centroid.getPixelR()+"  "+centroid.getPixelG()+"  "+centroid.getPixelB()+"<---");
			centroid.setPixelColor(new Color(newCentroidR,newCentroidG,newCentroidB));
			return 1;
		}else
			return 0;
	}
}
