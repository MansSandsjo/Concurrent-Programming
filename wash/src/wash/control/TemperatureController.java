package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	// temperature increase
	int dt = 10; // sekunder
	double m_upper = 0.0478 * dt;
	double m_lower = 9.52E-4;
	WashingIO io;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {
			int value = 0;
			double C = 0;
			boolean sendSignal = true;
			ActorThread<WashingMessage> sender = null;
			
			while (true) {
				WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);

				if (m != null) {
					value = m.getCommand();
					sender = m.getSender();
					C = m.getValue();
					sendSignal = true;
					
					// sender.send(new WashingMessage(this, 9));

				}

				switch (value) {

				case WashingMessage.TEMP_IDLE:
					io.heat(false);
					break;

				case WashingMessage.TEMP_SET:
					if (io.getTemperature()>=C-2 && sendSignal) {
						sender.send(new WashingMessage(this, 9));
						sendSignal = false;
					}
					if (io.getTemperature() <= (C - 1.5 + m_lower)) {
						io.heat(true);
					}
					if (io.getTemperature() >= (C - m_upper * 10 / io.getWaterLevel())) {
						io.heat(false);
					}
					break;

				}
				// loop to control heat

				// Thread.sleep(dt*1000 / Settings.SPEEDUP);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
