package OS;

import CPU.*;

public class SharkOS implements InterruptHandler {

    // Queue for PCBs that are being processed
    private final java.util.ArrayDeque<PCB> readyQueue = new java.util.ArrayDeque<>();
    private PCB program;

    private SharkMachine cpu;

    public void start(SharkMachine cpu) {

        this.cpu = cpu;

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
        pcb.nextOpcode = cpu.getNextOpcode();
        pcb.nextOperand = cpu.getNextOperand();

    }

    // Restores the CPU state from the PCB EXACTLY as it was before the interrupt
    private void restoreContext(SharkMachine cpu, PCB pcb) {

        cpu.setACC(pcb.ACC);
        cpu.setPSIAR(pcb.PSIAR);
        cpu.setSAR(pcb.SAR);
        cpu.setSDR(pcb.SDR);
        cpu.setTMPR(pcb.TMPR);
        cpu.setIR(pcb.IR);
        cpu.setCSIAR(pcb.CSIAR);
        pcb.state = ProcessState.RUNNING;
        cpu.setNextOpcode( pcb.nextOpcode );
        cpu.setNextOperand( pcb.nextOperand );

    }

    // Add a new job to the ready queue
    public void addJob(PCB pcb) {

        pcb.state = ProcessState.READY;
        readyQueue.addLast(pcb);

    }

    // Run the next job in the ready queue
    public void runNextJob() {

        // If there are no jobs to run, return
        if (readyQueue.isEmpty()) {
            program = null;
            return;
        }

        // Pull the first available job off the queue
        program = readyQueue.pollFirst();

        restoreContext(this.cpu, program);
    }

    // Interrupt handler for interrupts from the CPU
    @Override
    public void handleInterrupt(InterruptType type, SharkMachine cpu) {

        // Get the PCB of the running process
        if (program != null) {

            saveContext(cpu, program);

        }

        switch(type) {

            case TIMER:

            case YIELD:
                if (program != null) {
                    program.state = ProcessState.READY;
                    readyQueue.addLast(program);
                }
                runNextJob();
                break;

            case HALT:

            case FAULT:
                if (program != null) {
                    program.state = ProcessState.TERMINATED;
                    program = null;
                }
                runNextJob();
                break;

        }

    }

    // Check if there is work to be done
    public boolean hasWork() {
        return program != null || !readyQueue.isEmpty();
    }

}
