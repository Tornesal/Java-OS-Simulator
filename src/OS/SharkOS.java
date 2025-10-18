package OS;

import CPU.*;

public class SharkOS implements InterruptHandler {

    private PCB running;

    public void start(SharkMachine cpu, PCB pcb) {

        this.running = pcb;

        pcb.restoreContext(cpu);

    }

    @Override
    public void handleInterrupt(InterruptType type, SharkMachine cpu) {

        running.saveContext(cpu);

        switch(type) {

            case TIMER:

            case YIELD:
                running.state = ProcessState.READY;
                running.restoreContext(cpu);
                break;

            case HALT:
                running.state = ProcessState.TERMINATED;
                break;

            case FAULT:
                running.state = ProcessState.TERMINATED;
                break;

        }

    }

    public boolean hasWork() {
        return running != null && running.state != ProcessState.TERMINATED;
    }

}
