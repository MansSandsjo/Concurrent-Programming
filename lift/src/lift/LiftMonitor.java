package lift;

import java.util.concurrent.Semaphore;

public class LiftMonitor {
	private int hissFloor;
	private int[] waitingLevel;
	private LiftView view;
	private int[] exitLevel;
	private volatile int peopleInHiss;
	private boolean doorsOpen;
	private volatile int someoneIsWaiting;
	private boolean runHis;
	private Semaphore walkingIn;
	
	public LiftMonitor(LiftView view) {
		hissFloor = 0;
		waitingLevel = new int[7];
		exitLevel = new int[7];
		this.view = view;
		peopleInHiss = 0;
		someoneIsWaiting = 0;
		runHis = false;
		walkingIn = new Semaphore(0);

	}

	// public synchronized void operatePassenger(Passenger pass) throws
	// InterruptedException {
	// // move to hiss
	// waitingLevel[pass.getStartFloor()]++;
	// someoneIsWaiting++;
	// notifyAll();
	//
	// while (!(pass.getStartFloor() == hissFloor && doorsOpen) || peopleInHiss
	// >= 4) {
	// wait();
	// }
	// // pass.enterLift();
	// // get where the passenger exits
	// exitLevel[pass.getDestinationFloor()]++;
	// view.showDebugInfo(waitingLevel, exitLevel);
	// peopleInHiss++;
	// waitingLevel[hissFloor]--;
	// notifyAll();
	// while (!(pass.getDestinationFloor() == hissFloor && doorsOpen)) {
	// wait();
	// }
	// // pass.exitLift();
	// // passengers exits lift
	// someoneIsWaiting--;
	// exitLevel[hissFloor]--;
	// peopleInHiss--;
	//
	// notifyAll();
	// }

	public synchronized void movePtoHiss(Passenger pass) throws InterruptedException {
		waitingLevel[pass.getStartFloor()]++;
		someoneIsWaiting++;
		notifyAll();

		while (!(pass.getStartFloor() == hissFloor && doorsOpen) || peopleInHiss >= 4) {
			wait();
		}
		peopleInHiss++;
		walkingIn.release();
		waitingLevel[hissFloor]--;

	}

	// pass.enterlift
	public synchronized void whereToExit(Passenger pass) throws InterruptedException {
		walkingIn.acquire();
		someoneIsWaiting--;
		exitLevel[pass.getDestinationFloor()]++;
		view.showDebugInfo(waitingLevel, exitLevel);
		notifyAll();
		while (!(pass.getDestinationFloor() == hissFloor && doorsOpen)) {
			wait();
		}
	}
	// pass.exitLift

	public synchronized void setVariables() {
		peopleInHiss--;
		exitLevel[hissFloor]--;
		notifyAll();
	}

	public synchronized void operateHiss(int floor) throws InterruptedException {
		hissFloor = floor;

		notifyAll();
		view.showDebugInfo(waitingLevel, exitLevel);
		// väntar på att passagerare går på
		// öppna bara om det finns passagerare

		while (waitingLevel[hissFloor] > 0 && peopleInHiss < 4 || (exitLevel[hissFloor] > 0) || someoneIsWaiting == 0 
				|| walkingIn.availablePermits()!=0 ) {
			if (!doorsOpen) {
				view.openDoors(hissFloor);
				doorsOpen = true;
			}
			wait();
		}
		if (doorsOpen) {
			doorsOpen = false;
			view.closeDoors();
		}
	}
}
