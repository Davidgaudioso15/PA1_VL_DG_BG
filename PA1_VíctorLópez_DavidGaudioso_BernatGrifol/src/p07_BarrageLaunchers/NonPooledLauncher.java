package p07_BarrageLaunchers;

import java.time.Duration;
import java.time.Instant;
import p06_BarrageCommon.DualCounter;
import p06_BarrageCommon.IncrementTask;

public class NonPooledLauncher {

	public static void main (String [] args) {
		final int NUM_TASKS = 100000;
		
		Instant start, end;
		Duration  elapsedTime;
		DualCounter counter = new DualCounter();
		IncrementTask [] tasks = new IncrementTask[NUM_TASKS];

		/* COMPLETE, if needed */
		
		System.out.println("Experimenting WITHOUT pooling. Launching "+NUM_TASKS+" short-lived tasks");
		System.out.println();
		
		for (int i=0; i<tasks.length; i++) tasks[i]=new IncrementTask(counter);
		
		start = Instant.now();
		
		/* COMPLETE *
		 	In a single iteration give each task a thread to run on and start it. 
		  */
		Thread[] threads = new Thread[NUM_TASKS];
		for (int i = 0; i < tasks.length; i++) {
			threads[i] = new Thread(tasks[i]);
			threads[i].start();
		}

		for (int i = 0; i < tasks.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		while (counter.getSyncCount()!=NUM_TASKS) {
			Thread.onSpinWait();
		}
		
		end = Instant.now();
		elapsedTime = Duration.between(start, end);
		
		System.out.println("finished after "+elapsedTime.toMillis()+" ms.");
		System.out.println("Sychronized counter has value: "+counter.getSyncCount());
		System.out.println("Unsychronized counter has value: "+counter.getUnsyncCount());
		
		/* COMPLETE if needed */ 
	}
	
}
