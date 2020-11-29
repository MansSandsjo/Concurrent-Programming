package train.simulation;

import train.model.Route;
import train.view.TrainView;

public class TrainSimulation {

	public static void main(String[] args) {

		TrainView view = new TrainView();
		TrainMonitor mon = new TrainMonitor();

		for(int n=0;n<6;n++){
		Route route = view.loadRoute();
		TrainThread train = new TrainThread(route, mon);
		train.trainSize(3);
		train.start();
		
		}
	}

}
