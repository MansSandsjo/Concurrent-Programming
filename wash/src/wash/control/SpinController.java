package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {
	WashingIO io;
	// TODO: add attributes

	public SpinController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {
			int value = 0;
			ActorThread<WashingMessage> sender = null;
			// ... TODO ...

			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
				// vi vill köra casen tills det att ett nytt meddelande dyker
				// upp

				if (m != null) {
					value = m.getCommand();
					sender = m.getSender();
					if(value==2)
						sender.send(new WashingMessage(this, 9));
				}
				
					switch (value) {
					default:
						// do nothing
						break;
					case 1:
						// barrel rotation should stop
						io.setSpinMode(io.SPIN_IDLE);
						sender.send(new WashingMessage(this, 9));
						break;
					case 2:
						// barrel should alternate between slow left rotation
						// and
						// slow right rotation
						io.setSpinMode(io.SPIN_LEFT);
						Thread.sleep(60000 / Settings.SPEEDUP);
						io.setSpinMode(io.SPIN_RIGHT);
						
						break;
					case 3:
						// spin fast
						io.setSpinMode(io.SPIN_FAST);
						
						sender.send(new WashingMessage(this, 9));
						break;
				}
				// if m is null, it means a minute passed and no message was
				// received
				if (m != null) {
					System.out.println("got " + m);
				}

				// ... TODO ...
			}
		}catch(

	InterruptedException unexpected)
	{
		// we don't expect this thread to be interrupted,
		// so throw an error if it happens anyway
		throw new Error(unexpected);
	}
}}
