import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;

public class Main {
    public static void main(String[] args) {
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        cpu.setInterruptHandler(os::handleInterrupt);

        // --- PROGRAM 1: LDA 100; STR 101; YLD; HALT ---
        int[] mem = cpu.getMemory();
        int base1 = 0;
        mem[base1 + 0] = (30 << 16) | (base1 + 100); // LDA 100
        mem[base1 + 1] = (40 << 16) | (base1 + 101); // STR 101
        mem[base1 + 2] = (70 << 16) | 0;             // YLD
        mem[base1 + 3] = (99 << 16) | 0;             // HALT
        mem[base1 + 100] = 11;                       // data

        // --- PROGRAM 2: LDA 200; STR 201; HALT ---
        int base2 = 20;
        mem[base2 + 0] = (30 << 16) | (base2 + 200); // LDA 200
        mem[base2 + 1] = (40 << 16) | (base2 + 201); // STR 201
        mem[base2 + 2] = (99 << 16) | 0;             // HALT
        mem[base2 + 200] = 22;                       // data

        // --- create PCBs ---
        PCB job1 = new PCB(1);
        job1.PSIAR = base1;
        job1.state = ProcessState.READY;

        PCB job2 = new PCB(2);
        job2.PSIAR = base2;
        job2.state = ProcessState.READY;

        // --- load jobs into OS ---
        os.addJob(job1);
        os.addJob(job2);
        os.start(cpu, job1); // start scheduler with first job

        // --- run until OS has no work left ---
        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("All jobs completed.");
    }
}
