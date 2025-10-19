package OS;

import CPU.*;

public class SharkOS implements InterruptHandler {

    // Queue for PCBs that are being processed
    private final java.util.ArrayDeque<PCB> readyQueue = new java.util.ArrayDeque<>();
    private PCB running;

    private SharkMachine cpu;

    public void start(SharkMachine cpu, PCB pcb) {

        this.cpu = cpu;

        addJob(pcb);

        // Load the first job into the CPU
        runNextJob();

    }

    // Saves the CPU state into the PCB
    private void saveContext(SharkMachine cpu, PCB pcb) {

        pcb.ACC = cpu.getACC();
        pcb.PSIAR = cpu.getPSIAR();
        pcb.SAR = cpu.getSAR();
        pcb.SDR = cpu.getSDR();
        pcb.TMPR = cpu.getTMPR();
        pcb.IR = cpu.getIR();
        pcb.CSIAR = cpu.getCSIAR();
        pcb.halted = cpu.isHalted();

    }

    // Restores the CPU state from the PCB
    private void restoreContext(SharkMachine cpu, PCB pcb) {

        cpu.setACC(pcb.ACC);
        cpu.setPSIAR(pcb.PSIAR);
        cpu.setSAR(pcb.SAR);
        cpu.setSDR(pcb.SDR);
        cpu.setTMPR(pcb.TMPR);
        cpu.setIR(pcb.IR);
        cpu.setCSIAR(pcb.CSIAR);
        cpu.setHalted(false);
        pcb.state = ProcessState.RUNNING;

    }

    // Add a new job to the ready queue
    public void addJob(PCB pcb) {

        pcb.state = ProcessState.READY;
        readyQueue.addLast(pcb);

    }

    // Run the next job in the ready queue
    public void runNextJob() {

        running = null;

        while (!readyQueue.isEmpty()) {

            // Pull the first job off the queue
            PCB next = readyQueue.pollFirst();

            if (next.state != ProcessState.TERMINATED) {

                running = next;
                restoreContext(cpu, running);
                return;

            }
        }
    }

    // Interrupt handler for interrupts from the CPU
    @Override
    public void handleInterrupt(InterruptType type, SharkMachine cpu) {

        // Get the PCB of the running process
        if (running != null) {

            saveContext(cpu, running);

        }

        switch(type) {

            case TIMER:

            case YIELD:
                if (running != null && running.state == ProcessState.TERMINATED) {

                    running.state = ProcessState.READY;
                    readyQueue.addLast(running);

                }

                runNextJob();
                break;

            case HALT:
                if (running != null) running.state = ProcessState.TERMINATED;
                runNextJob();
                break;

            case FAULT:
                if (running != null) running.state = ProcessState.TERMINATED; // or ERROR
                runNextJob();
                break;

        }

    }

    // Check if there is work to be done
    public boolean hasWork() {
        return (running != null && running.state != ProcessState.TERMINATED) || !readyQueue.isEmpty();
    }

}
