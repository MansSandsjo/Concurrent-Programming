package train.simulation;
import java.util.concurrent.ArrayBlockingQueue;

import train.model.Route;
import train.model.Segment;

public class TrainThread extends Thread {
	private Route route;
	private TrainMonitor mon;
	private int size;
	private ArrayBlockingQueue <Segment> abq;
	
	public TrainThread(Route route, TrainMonitor mon) {
		this.route = route;
		this.mon = mon; 
		
	}
	
	public void trainSize(int size){
		this.size=size;
		abq = new ArrayBlockingQueue<Segment>(size);
	}

	public void run() {
		//skapa tåget
		for(int n = 0; n<size; n++){
			Segment seg = route.next();
			abq.add(seg);
			try {
				mon.addSegment(seg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			seg.enter();
		}
		
		while (true) {
				
			Segment seg = route.next();
			Segment temp = abq.poll();
			abq.add(seg);
			try {
				mon.addSegment(seg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			seg.enter();
			temp.exit();
			mon.removeSegment(temp);
			
		}
	}
}
