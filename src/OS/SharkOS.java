package OS;

import CPU.*;
import OS.Parser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SharkOS implements InterruptHandler {

    // Queue for PCBs that are being processed
    private final java.util.ArrayDeque<PCB> readyQueue = new java.util.ArrayDeque<>();
    private PCB program;
    private final SharkMachine cpu;
    private final Parser parser;


    public SharkOS() {

        // Link CPU, Parser, and InterruptHandler together to OS
        this.cpu = new SharkMachine();
        this.parser = new Parser();
        this.cpu.setInterruptHandler(this);

    }

    public void start(ArrayList<JobDetails> jobs) {

        System.out.println("----- Starting SharkOS Simulation -----");

        // Set up our logs directory now and system time
        // Create the logs directory if it doesn't exist
        java.io.File logDir = new java.io.File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        long systemTime = 0;

        // Main loop for the simulation. Runs until there are no jobs left to run.
        while (hasWork() || !jobs.isEmpty()) {

            // Iterator for the jobs list
            var iterator = jobs.iterator();

            while (iterator.hasNext()) {

                JobDetails job = iterator.next();

                // If the job's time arrives, load the program and add it to the ready queue
                if (systemTime >= job.arrivalTime) {

                    System.out.println("\n----- System Time: " + systemTime + " -> Job PID " + job.pid + ", Job Name: " + "\"" + job.fileName + "\"" + " has been added to the OS queue -----");

                    addJob(job.fileName, job.baseAddress, job.pid);
                    iterator.remove();

                }
            }

            // run CPU one cycle
            if (program != null) {
                cpu.microStep();
            }

            systemTime++;
        }

        // Final printout to show results of simulation
        System.out.println("\n---------------------------------------\n");
        System.out.println("All jobs completed.");
        System.out.println("\n--- Final Memory State Verification ---");
        System.out.println("Job 1 Result at mem[13]: " + cpu.getMemory()[13] + " (Expected: 150)");
        System.out.println("Job 2 Result at mem[20]: " + cpu.getMemory()[20] + " (Expected: 0)");
        System.out.println("Job 3 Result at mem[30]: " + cpu.getMemory()[30] + " (Expected: 30)");
        System.out.println("Job 4 Result at mem[43]: " + cpu.getMemory()[43] + " (Expected: 150)");
        System.out.println("Job 5 Result at mem[50]: " + cpu.getMemory()[50] + " (Expected: 0)");
        System.out.println("Job 6 Result at mem[60]: " + cpu.getMemory()[60] + " (Expected: 30)");
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
    public void addJob(String fileName, int baseAddress, int pid) {

        ArrayList<Integer> code = parser.parseFile(fileName);

        // If the file was parsed successfully, add it to the ready queue
        if (!code.isEmpty()) {

            // Load the program into memory
            int[] memory = cpu.getMemory();
            for (int i = 0; i < code.size(); i++) {
                memory[baseAddress + i] = code.get(i);
            }

            PCB job = new PCB(pid);
            job.PSIAR = baseAddress;
            readyQueue.addLast(job);

            if (program == null){
                runNextJob();
            }
        }
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

                if (program != null) {
                    program.state = ProcessState.TERMINATED;
                    dumpJobStateToFile(cpu, program);
                }
                runNextJob();
                break;

        }

    }

    // Check if there is work to be done
    public boolean hasWork() {
        return program != null || !readyQueue.isEmpty();
    }

    private void dumpJobStateToFile(SharkMachine cpu, PCB job) {
        if (job == null) return;

        String logFileName = "logs/job_" + job.pid + "_dump.log";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName))) {
            writer.write("----- DUMP STATE on HALT for Job PID: " + job.pid + " -----\n");
            writer.write("ACC: " + cpu.getACC() + "\n");
            writer.write("PSIAR: " + cpu.getPSIAR() + "\n");
            writer.write("SAR: " + cpu.getSAR() + "\n");
            writer.write("SDR: " + cpu.getSDR() + "\n");
            writer.write("TMPR: " + cpu.getTMPR() + "\n");
            writer.write("IR: " + cpu.getIR() + "\n");
            writer.write("MIR: " + cpu.getMIR() + "\n");
            writer.write("CSIAR: " + cpu.getCSIAR() + "\n");

            writer.write("----- Used Memory Contents -----\n");
            int[] memory = cpu.getMemory();
            for (int i = 0; i < memory.length; i++) {
                // To keep it readable but complete, let's print 8 addresses per line.
                if (i % 8 == 0) {
                    writer.write(String.format("%nmem[%04d]: ", i));
                }
                writer.write(String.format(" %11d", memory[i]));
            }

            writer.write("----- End of Job -----\n");
            writer.write("---------------------------------------\n");

        } catch (IOException e) {
            System.err.println("Error writing log file for Job " + job.pid);
            e.printStackTrace();
        }
    }

}
