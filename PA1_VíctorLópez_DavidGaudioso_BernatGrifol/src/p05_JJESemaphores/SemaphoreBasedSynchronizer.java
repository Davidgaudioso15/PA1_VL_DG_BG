package p05_JJESemaphores;

import p03_JJECommon.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SemaphoreBasedSynchronizer implements Synchronizer {
    private final Semaphore jumpSemaphore = new Semaphore(1);
    private final Semaphore jiveSemaphore = new Semaphore(0);
    private final Semaphore enjoySemaphore = new Semaphore(0);
    private volatile int jumpCount = 0;
    private volatile int firstJumpId = -1;
    private volatile int secondJumpId = -1;
    private volatile int requiredJives = 0;
    private volatile int jivesCompleted = 0;


    private volatile int secondJumpThreadId = -1;

    @Override
    public void letMeJump(int id) {
        try {
            jumpSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        /*
        jumpSemaphore.acquireUninterruptibly();
        while (secondJumpThreadId == id) { // Esperar hasta que el último TIC sea diferente
            jumpSemaphore.release();
            Thread.yield();
            jumpSemaphore.acquireUninterruptibly();
        }
        secondJumpThreadId = id;
         */
    }

    @Override
    public void jumpDone(int id) {
        if (jumpCount == 0) {
            firstJumpId = id;
            jumpCount = 1;
            jumpSemaphore.release();
        } else {
            if (id == firstJumpId) {
                System.err.println("ERROR: JUMP ID repetition");
                System.exit(1);
            }
            secondJumpId = id;
            requiredJives = secondJumpId;
            jivesCompleted = 0;
            jumpCount = 0;
            jiveSemaphore.release(requiredJives);
        }
    }

    @Override
    public void letMeJive(int id) {
        try {
            jiveSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void jiveDone(int id) {
        jivesCompleted++;
        if (jivesCompleted == requiredJives) {
            boolean even = (requiredJives % 2) == 0;
            if (even) {
                enjoySemaphore.release();
            } else {
                enjoySemaphore.release();
            }
        } else {
            jiveSemaphore.release();
        }
    }

    @Override
    public boolean letMeEnjoy(int id) {
        try {
            enjoySemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

    @Override
    public void enjoyDone(int id) {
        jumpSemaphore.release();
    }
}