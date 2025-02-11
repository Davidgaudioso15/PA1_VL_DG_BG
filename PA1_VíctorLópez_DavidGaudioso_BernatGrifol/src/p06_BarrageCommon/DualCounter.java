package p06_BarrageCommon;

import java.util.concurrent.Semaphore;

public class DualCounter {
	public volatile int syncCounter = 0;
	public volatile int unsyncCounter = 0;
	
	/* COMPLETE */
	private final Semaphore semaphore = new Semaphore(1);
	public int getSyncCount () {return syncCounter;}
	public int getUnsyncCount () {return unsyncCounter;}
	
	public void increment () {
		int temp;
		
		temp = unsyncCounter;
		temp ++ ;
		Thread.yield();
		unsyncCounter = temp;
		
		preProtocol();
		temp = syncCounter;
		temp ++ ;
		syncCounter = temp;
		postProtocol();
	}
	
	private void preProtocol () {
		/* COMPLETE */
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private void postProtocol () {
		/* COMPLETE */
		semaphore.release();
	}
	
	public void reset () {
		syncCounter = 0;
		unsyncCounter = 0;
	}
		
}