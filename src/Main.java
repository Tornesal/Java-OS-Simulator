import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;

public class Main {
    public static void main(String[] args) {
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        cpu.setInterruptHandler(os::handleInterrupt);

        // --- seed a tiny program: LDA 100; STR 101; HALT ---
        int[] mem = cpu.getMemory();
        mem[0] = (30 << 16) | 100;  // LDA 100
        mem[1] = (40 << 16) | 101;  // STR 101
        mem[2] = (99 << 16) | 0;    // HALT
        mem[100] = 42;              // data

        // --- create a PCB and set its starting state ---
        PCB pcb = new PCB(1);
        pcb.state = ProcessState.READY;
        pcb.PSIAR = 0; // start at address 0

        // --- hand control to OS (no queue yet; single job) ---
        os.start(cpu, pcb);

        // --- run until OS says no more work ---
        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("Done.");
    }
}
