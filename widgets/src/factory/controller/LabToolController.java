package factory.controller;

import java.util.concurrent.Semaphore;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface, to be used for the Widget
 * Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
	private final DigitalSignal conveyor, press, paint;
	private final long pressingMillis, paintingMillis;
	Semaphore mutex = new Semaphore(2);

	public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
			long paintingMillis) {
		this.conveyor = conveyor;
		this.press = press;
		this.paint = paint;
		this.pressingMillis = pressingMillis;
		this.paintingMillis = paintingMillis;

	}

	@Override
	public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method.
		//
		// Note that this method can be called concurrently with
		// onPaintSensorHigh
		// (that is, in a separate thread).
		//
		if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
			stopConveyor();
			press.on();
			waitOutside(pressingMillis);
			press.off();
			waitOutside(pressingMillis); // press needs this time to retract
			startConveyor();
		}
	}

	@Override
	public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method.
		//
		// Note that this method can be called concurrently with
		// onPressSensorHigh
		// (that is, in a separate thread).
		//
		if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
			stopConveyor();
			paint.on();
			waitOutside(paintingMillis);
			paint.off();
			startConveyor();

		}
	}

	private synchronized void stopConveyor() throws InterruptedException {
		conveyor.off();
		mutex.acquire();

	}

	private synchronized void startConveyor() throws InterruptedException {
		mutex.release();
		if(mutex.availablePermits() ==2)
		conveyor.on();
		

	}

	/** Helper method: wait outside of monitor for 'millis' milliseconds. */
	private void waitOutside(long millis) throws InterruptedException {
		 long now = System.currentTimeMillis();
		 long timeToWakeUp = System.currentTimeMillis() + millis;
		
		 while (now < timeToWakeUp) {
		 wait(timeToWakeUp-now);
		 now = System.currentTimeMillis();
		 }
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {
		Factory factory = new Factory();

		ToolController toolController = new LabToolController(factory.getConveyor(), factory.getPress(),
				factory.getPaint(), Press.PRESSING_MILLIS, Painter.PAINTING_MILLIS);
		factory.startSimulation(toolController);
	}
}
