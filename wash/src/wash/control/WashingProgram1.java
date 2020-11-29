package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;

	public WashingProgram1(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		try {
			// Lock the hatch
			System.out.println("washing program 1 started");
			io.lock(true);

			// let water in
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, io.MAX_WATER_LEVEL / 2));
			// ack that machine is filled with water and set water fill IDLE
			WashingMessage ack1 = receive();
			System.out.println("got " + ack1);
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			
			// heat to 40C
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
			// ack when temp is 40
			WashingMessage ack3 = receive();
			System.out.println("got " + ack3);
			
			// Instruct SpinController to rotate barrel slowly, back and forth
			System.out.println("setting SPIN_SLOW...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			// // Expect an acknowledgment in response.
			WashingMessage ack4 = receive();
			System.out.println("washing program 1 got " + ack4);

			// Spin and keep heat for 30 simulated minutes (one minute == 60000
			// milliseconds)
			Thread.sleep(30 * 60000 / Settings.SPEEDUP);

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			// ack when temp is IDLE
		
			
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			WashingMessage ack7 = receive();
			System.out.println("washing program 1 got drain " + ack7);
			
			for (int n = 0; n < 6; n++) {
				water.send(new WashingMessage(this, WashingMessage.WATER_FILL, io.MAX_WATER_LEVEL/2));
				// ack that machine is filled with water and set water fill IDLE
				WashingMessage ack8 = receive();
				System.out.println("got " + ack8);
				
				Thread.sleep(2*60000/Settings.SPEEDUP);
				
				water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
				WashingMessage ack12 = receive();
				System.out.println("washing program 1 got drain " + ack12);
				
				
			}
			
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			WashingMessage ack12 = receive();
			System.out.println("washing program 1 got drain " + ack12);
			
			System.out.println("setting SPIN_FAST...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
			// // Expect an acknowledgment in response.
			WashingMessage ack10 = receive();
			System.out.println("washing program 1 got " + ack10);
			Thread.sleep(5 * 60000 / Settings.SPEEDUP);
			
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			// // Expect an acknowledgment in response.
			WashingMessage ack11 = receive();
			System.out.println("washing program 1 got " + ack11);
			Thread.sleep(5 * 60000 / Settings.SPEEDUP);
			
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			
			System.out.println("washing program 1 finished");
			io.lock(false);
		} catch (InterruptedException e) {

			// If we end up here, it means the program was interrupt()'ed:
			// set all controllers to idle

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			System.out.println("washing program terminated");
		}
	}
}
