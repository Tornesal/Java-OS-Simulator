import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;
import OS.Parser;
import java.util.ArrayList;

public class Main {
    public static void loadProgram(int[] memory, ArrayList<Integer> code, int baseAddress) {
        for (int i = 0; i < code.size(); i++) {
            memory[baseAddress + i] = code.get(i);
        }
    }

    public static void main(String[] args) {
        // --- 1. System Initialization ---
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        Parser parser = new Parser();
        cpu.setInterruptHandler((type, machine) -> os.handleInterrupt(type, machine));

        // --- 2. Load ALL Programs ---
        System.out.println("Loading programs...");

        // Program 1: Summation
        loadAndCreateJob(cpu, os, parser, "programs/sharkosprog1.txt", 300, 1);

        // Program 2: Decrement to Zero
        loadAndCreateJob(cpu, os, parser, "programs/sharkosprog2.txt", 400, 2);

        // Program 3: Increment by 30
        loadAndCreateJob(cpu, os, parser, "programs/sharkosprog3.txt", 500, 3);

        System.out.println("---------------------------------------");

        // --- 3. Start Simulation ---
        System.out.println("Starting SharkOS multitasking simulation...");
        os.start(cpu);

        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("---------------------------------------");
        System.out.println("All jobs completed.");

        // --- 4. Verify Results ---
        System.out.println("\n--- Final Memory State Verification ---");
        System.out.println("Summation Result at mem[103]: " + cpu.getMemory()[103] + " (Expected: 150)");
        System.out.println("Decrement Result at mem[250]: " + cpu.getMemory()[250] + " (Expected: 0)");
        System.out.println("Increment Result at mem[260]: " + cpu.getMemory()[260] + " (Expected: 30)");
    }

    // Helper method to reduce code duplication
    public static void loadAndCreateJob(SharkMachine cpu, SharkOS os, Parser parser, String fileName, int baseAddress, int pid) {
        ArrayList<Integer> code = parser.parseFile(fileName);
        if (!code.isEmpty()) {
            loadProgram(cpu.getMemory(), code, baseAddress);
            PCB job = new PCB(pid);
            job.PSIAR = baseAddress;
            os.addJob(job);
            System.out.println("Job PID " + pid + " (" + fileName + ") loaded at address " + baseAddress);
        } else {
            System.err.println("Failed to load program: " + fileName);
        }
    }
}
