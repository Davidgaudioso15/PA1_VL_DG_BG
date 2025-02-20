package p07_BarrageLaunchers;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import p06_BarrageCommon.DualCounter;
import p06_BarrageCommon.IncrementTask;

public class PooledLauncher {
	
	public static void main (String [] args) {
		final int NUM_TASKS = 100000;
		
		Instant start, end;
		Duration  elapsedTime;
		
		DualCounter counter = new DualCounter();
		IncrementTask [] tasks = new IncrementTask[NUM_TASKS];
		
		/* COMPLETE Declare pool here */ 
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		System.out.println("Experimenting WITH pooling. Launching "+NUM_TASKS+" short-lived tasks");
		System.out.println();
		
		for (int i=0; i<tasks.length; i++) tasks[i]=new IncrementTask(counter);
		
		start = Instant.now();
		
		/* COMPLETE 
		 create a thread pool and submit all the tasks in a single iteration.
		 Use a pool with as many threads as available processors in the current evironment. 
		 */
		for (int i = 0; i < tasks.length; i++) {
			executor.execute(tasks[i]);
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			Thread.onSpinWait();
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
