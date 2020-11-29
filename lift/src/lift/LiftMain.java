package lift;

public class LiftMain {

	public static void main(String[] args) throws InterruptedException {
		LiftView view = new LiftView();
		LiftMonitor mon = new LiftMonitor(view);
		LiftThread lift = new LiftThread(view, mon);
		lift.start();
		
		for(int n = 0; n<20; n++){
		PassengerThread passengers = new PassengerThread(view, mon);
		Thread.sleep(500);
		passengers.start();
		}
	}
}
