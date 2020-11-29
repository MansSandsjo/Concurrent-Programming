import java.awt.Component;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import javafx.concurrent.Task;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;
	private ExecutorService crackCodePool = Executors.newFixedThreadPool(2);
	private ExecutorService progressPool = Executors.newFixedThreadPool(2);
	private final JProgressBar mainProgressBar;
	private int mainMaxValue;

	// private ProgressItem progressItem;
	// private WorklistItem workItem;

	// -----------------------------------------------------------------------

	private CodeBreaker() {

		StatusWindow w = new StatusWindow();
		w.enableErrorChecks();
		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be
		 * performed in the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> {
			CodeBreaker codeBreaker = new CodeBreaker();
			new Sniffer(codeBreaker).start();
		});
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		System.out.println("message intercepted (N=" + n + ")...");

		SwingUtilities.invokeLater(() -> {
			WorklistItem workItem = new WorklistItem(n, message);
			JButton breakButton = new JButton("Click me!");
			workList.add(workItem);
			workList.add(breakButton);

			breakButton.addActionListener(e -> {
				mainMaxValue += 1000000;
				mainProgressBar.setMaximum(mainMaxValue);
				System.out.println("click");
				ProgressItem progressItem = new ProgressItem(n, message);
				Tracker tracker = new Tracker(progressItem.getProgressBar(), mainProgressBar);
				progressList.add(progressItem);
				workList.remove(workItem);
				workList.remove(breakButton);

				JButton removeButton = new JButton("Cancel");
				progressList.add(removeButton);

				Future future = crackCodePool
						.submit(new Thread(cTask(message, n, progressItem, tracker, removeButton)));

				removeButton.addActionListener(e2 -> {
					int value=1000000-progressItem.getProgressBar().getValue();
					tracker.onProgress(value);
					future.cancel(true);
					progressItem.getTextArea().setText("task was cancelled");
					removeButton.setText("Remove");
					removeProgressItem(removeButton, progressItem);
				});
			});
		});

	}

	// behövs inte synchronized här väl?
	private Runnable cTask(String message, BigInteger n, ProgressItem progressItem, Tracker tracker,
			JButton removeButton) {
		Runnable crackTask = () -> {
			try {
				String s = Factorizer.crack(message, n, tracker);
				SwingUtilities.invokeLater(() -> {
					progressItem.getTextArea().setText(s);
					removeButton.setText("Remove");
				});
				removeProgressItem(removeButton, progressItem);
			} catch (Throwable t) {
				System.out.println("task was cancelled");
			}
		};
		return crackTask;
	}

	private void removeProgressItem(JButton removeButton, ProgressItem progressItem) {
		SwingUtilities.invokeLater(() -> {
			removeButton.addActionListener(e2 -> {
				mainMaxValue -= 1000000;
				// add removebutton
				progressList.remove(progressItem);
				progressList.remove(removeButton);
				mainProgressBar.setMaximum(mainMaxValue);
			});
		});
	}

	private static class Tracker implements ProgressTracker {
		private int totalProgress;
		private int ppmDelta;
		private JProgressBar progressBar;
		private JProgressBar mainProgressBar;

		private Tracker(JProgressBar progressBar, JProgressBar mainProgressBar) {
			this.progressBar = progressBar;
			this.mainProgressBar = mainProgressBar;
		}

		/**
		 * Called by Factorizer to indicate progress. The total sum of ppmDelta
		 * from all calls will add upp to 1000000 (one million).
		 * 
		 * @param ppmDelta
		 *            portion of work done since last call, measured in ppm
		 *            (parts per million)
		 */
		@Override
		public void onProgress(int ppmDelta) {
			this.ppmDelta = ppmDelta;
			totalProgress += ppmDelta;
			SwingUtilities.invokeLater(() -> {
				progressBar.setValue(totalProgress);
				int olProgress = mainProgressBar.getValue();
				mainProgressBar.setValue(olProgress + ppmDelta);
			});
		}
		public int getppmDelta(){
			return ppmDelta;
		}

	}
}