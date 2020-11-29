package clock.io;

import java.util.concurrent.Semaphore;

public class SettingTime {
	private ClockOutput clock_out;
	private int time_h;
	private int time_m;
	private int time_s;
	private int alarmTime;
	private Semaphore mutex;

	public SettingTime(ClockOutput clock_out) {
		this.clock_out = clock_out;
		mutex = new Semaphore(1);
	}

	public void setTime(int h, int m, int s) {
		try {
			mutex.acquire();
			time_h = h;
			time_m = m;
			time_s = s;
			clock_out.displayTime(h, m, s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mutex.release();
	}

	public int getTime_s() throws InterruptedException {
		try {
			mutex.acquire();
			return time_h * 3600 + time_m * 60 + time_s;
		} finally {
			mutex.release();
		}
	}
	public void tick(){
		try {
			mutex.acquire();
			time_s++;
			if (time_s > 59) {
				time_s = 0;
				time_m++;
			}
			if (time_m > 59) {
				time_m = 0;
				time_h++;
			}
			
			if (time_h > 23) {
				time_h = 0;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		clock_out.displayTime(time_h, time_m, time_s);
		mutex.release();
	
	}
 
	public void setAlarm(int alarmTime) {
		this.alarmTime = alarmTime;
	}

	public int getAlarm() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mutex.release();
		return alarmTime;
	}
}