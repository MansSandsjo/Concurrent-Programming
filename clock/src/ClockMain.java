import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.Alarm;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import clock.io.SettingTime;
import clock.io.TimeThread;

public class ClockMain {

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();
		Semaphore sem = in.getSemaphore();
		Semaphore alarmMutex = new Semaphore(1);
		alarmMutex.acquire();
		SettingTime time = new SettingTime(out);
		time.setTime(0, 0, 0);
		TimeThread CountingUp = new TimeThread(time);
		Alarm beepbeep = new Alarm(out, alarmMutex, time);
		CountingUp.start();
		beepbeep.start();
		boolean alarm = false;

		while (true) {
			sem.acquire();
			UserInput userInput = in.getUserInput();
			int choice = userInput.getChoice();
			int h = userInput.getHours();
			int m = userInput.getMinutes();
			int s = userInput.getSeconds();
			switch (choice) {
			case 1:
				// set time
				time.setTime(h, m, s);
				break;
			case 2:
				// set alarm
				time.setAlarm(h * 3600 + m * 60 + s);
				alarmMutex.release();
				break;
			case 3:
				// toggle
				alarm = !alarm;
				out.setAlarmIndicator(alarm);
				break;
			}
			System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
		}
	}
}