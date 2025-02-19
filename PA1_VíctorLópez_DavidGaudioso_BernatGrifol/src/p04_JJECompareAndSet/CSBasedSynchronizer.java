package p04_JJECompareAndSet;

import java.util.concurrent.atomic.AtomicInteger;
import p03_JJECommon.Synchronizer;

public class CSBasedSynchronizer implements Synchronizer {

    private static final int CAN_JUMP = 1;
    private static final int CAN_JIVE = 2;
    private static final int CAN_ENJOY = 3;
    private static final int CAN_JOY = 4;
    private static final int WRITING = 5;

    private AtomicInteger state;
    private volatile int lastTicId = -1;
    private volatile int jiveCount = 0;

    public CSBasedSynchronizer() {
        this.state = new AtomicInteger(CAN_JUMP); // Inicialmente se permite JUMP
    }

    @Override
    public void letMeJump(int id) {
        // Solo permite JUMP si el estado es CAN_JUMP y no es el mismo hilo que el último JUMP
        while (!state.compareAndSet(CAN_JUMP, WRITING) || lastTicId == id) {
            Thread.yield();
        }
        // Registra el ID del segundo hilo que hace JUMP
    }

    @Override
    public void jumpDone(int id) {
        // Reinicia el estado para permitir el siguiente JUMP o JIVE
        if (lastTicId != id) {
            state.set(CAN_JUMP);
            lastTicId = (lastTicId + 1) % 10;// Permite otro JUMP
        }else {
            state.set(CAN_JIVE); // Después de dos JUMPs, permite JIVE
        }
    }

    @Override
    public void letMeJive(int id) {
        // Solo permite JIVE si el estado es CAN_JIVE
        while (!state.compareAndSet(CAN_JIVE, WRITING)) {
            Thread.yield();
        }
        jiveCount++; // Incrementa el contador de JIVEs
    }

    @Override
    public void jiveDone(int id) {
        // Reinicia el estado para permitir más JIVEs o pasar a JOY/ENJOY
        if (jiveCount < lastTicId) {
            state.set(CAN_JIVE); // Permite más JIVEs
        } else {
            // Decide si terminar con JOY o ENJOY
            if (jiveCount % 2 == 0) {
                state.set(CAN_JOY); // Termina con JOY si el número de JIVEs es par
            } else {
                state.set(CAN_ENJOY); // Termina con ENJOY si el número de JIVEs es impar
            }
        }
    }

    @Override
    public boolean letMeEnjoy(int id) {
        // Solo permite ENJOY si el estado es CAN_ENJOY
        while (!state.compareAndSet(CAN_ENJOY, WRITING)) {
            Thread.yield();
        }
        return true;
    }

    @Override
    public void enjoyDone(int id) {
        // Reinicia el estado para permitir nuevos JUMPs
        state.set(CAN_JUMP);
        jiveCount = 0; // Reinicia el contador de JIVEs
    }
}