package clock.io;


public class TimeThread extends Thread {
	private SettingTime mon;
	
	public TimeThread(SettingTime mon) {
		this.mon = mon;
	}

	public void run() {
		long timeNow = System.currentTimeMillis();
		while (true) {
			try {
				mon.tick();
			long time_t0 = System.currentTimeMillis();
				Thread.sleep(1000 - (time_t0 - timeNow)%1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
