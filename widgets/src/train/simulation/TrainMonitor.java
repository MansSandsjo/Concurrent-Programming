package train.simulation;

import java.util.HashSet;

import train.model.Segment;

public class TrainMonitor {
	HashSet<Segment> allSegments;

	public TrainMonitor() {
		allSegments = new HashSet<Segment>();
	}

	public synchronized void addSegment(Segment addS) throws InterruptedException {
		while (allSegments.contains(addS)) {
			wait();
		}
		allSegments.add(addS);
	}

	public synchronized void removeSegment(Segment s) {
		allSegments.remove(s);
		notifyAll();
	}
}
