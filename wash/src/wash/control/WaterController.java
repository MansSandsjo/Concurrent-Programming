package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;

	public WaterController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		double waterLevel = 0;
		try {
			while (true) {
				int value = 0;
				ActorThread<WashingMessage> sender = null;

				WashingMessage m = receiveWithTimeout(2000 / Settings.SPEEDUP);

				if (m != null) {
					value = m.getCommand();
					sender = m.getSender();
					waterLevel = m.getValue();
				}

				switch (value) {
				case WashingMessage.WATER_DRAIN:
					io.fill(false);
					while (io.getWaterLevel() != 0)
						io.drain(true);
					if(io.getWaterLevel()==0)
						sender.send(new WashingMessage(this, 9));
					io.drain(true);
					break;
					
				case WashingMessage.WATER_FILL:
					io.drain(false);
					while(io.getWaterLevel()<=waterLevel){
						io.fill(true);
					}
					io.fill(false);
					sender.send(new WashingMessage(this, 9));
					break;
				case WashingMessage.WATER_IDLE:
					io.fill(false);
					io.drain(false);
					break;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
