package clock.io;

import java.util.concurrent.Semaphore;

public class Alarm extends Thread {
	private ClockOutput clock_out;
	private Semaphore alarmMutex;
	SettingTime time;

	public Alarm(ClockOutput clock_out, Semaphore alarmMutex, SettingTime time) {
		this.clock_out = clock_out;
		this.alarmMutex = alarmMutex;
		this.time = time;
	}

	@Override
	public void run() {
		while (true) {
			try {
				alarmMutex.acquire();
				int shut_off_alarm = time.getAlarm() + 20;
				while (time.getTime_s() <= time.getAlarm()) {
					if (time.getTime_s() == time.getAlarm()) {
						clock_out.alarm();
					}
				}
				while (time.getTime_s() <= shut_off_alarm) {
					clock_out.alarm();
					if (time.getTime_s() == shut_off_alarm) {
						alarmMutex.acquire();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			alarmMutex.release();

		}
	}
	
}

