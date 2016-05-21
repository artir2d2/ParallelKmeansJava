/**
 * Klasa pozwalaj¹ca na bezpieczne wielow¹tkowe zarz¹dzanie koordynatami x,y w macierzy obrazka
 * @author ak
 *
 */
public class XYContainer {
	private int x;
	private int y;
	private boolean busy = false;
	public XYContainer(){
		this.x = 0;
		this.y = 0;
	}
	
	public synchronized int[] getXY(){
		while (busy == true) 
		{
			try 
			{	wait(); } 
			catch (InterruptedException e) { }
		}
		busy = true;
		notifyAll();
		int ret[] = {x,y};
		return ret; 
	}
	
	public synchronized void setXY(int x, int y){
		while (busy == false) 
		{
			try 
			{ wait();	} 
			catch (InterruptedException e) { }
		}
		this.x=x;
		this.y=y;
		busy = false;
		notifyAll();
	}
}
