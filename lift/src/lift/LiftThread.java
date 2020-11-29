package lift;

public class LiftThread extends Thread {
	private LiftView view;
	private LiftMonitor mon;
	// private int fromFloor;

	public LiftThread(LiftView view, LiftMonitor mon) {
		this.view = view;
		this.mon = mon;
		// fromFloor = 0;
	}

	@Override
	public void run() {
		while (true) {
			for (int n = 0; n <= 5; n++) {
				if(n==0){
					try {
						mon.operateHiss(n);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//öppna inte hissdörrarna på varje våning
				view.moveLift(n, n+1);
				try {
					mon.operateHiss(n+1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (int n = 6; n > 0; n--) {
				view.moveLift(n, n - 1);
				try {
					mon.operateHiss(n-1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
