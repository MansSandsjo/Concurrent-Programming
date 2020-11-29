package lift;

public class PassengerThread extends Thread {
	LiftView view;
	LiftMonitor mon;

	public PassengerThread(LiftView view, LiftMonitor mon) {
		this.view = view;
		this.mon = mon;
	}

	@Override
	public void run() {
		while (true) {
			Passenger pass = view.createPassenger();
			pass.begin();
			
			try {
				mon.movePtoHiss(pass);
				pass.enterLift();
				mon.whereToExit(pass);
				pass.exitLift();
				mon.setVariables();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			try {
//				mon.operatePassenger(pass);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
			//	mon.waitPassenger(pass);
			
			pass.end(); // walk out (to the right)
		}
	}
}
