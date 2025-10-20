import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;
import OS.Parser;
import java.util.ArrayList;

public class Main {
    // Helper method to load assembled code into memory
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

        // --- 2. Link OS and CPU Explicitly (CRITICAL STEP) ---
        // This ensures the OS's interrupt handler and the main CPU are the same object.
        // The lambda passes the interrupting machine directly to the OS handler.
        cpu.setInterruptHandler((type, machine) -> os.handleInterrupt(type, machine));

        // --- 3. Load Program(s) ---
        System.out.println("Loading programs...");
        ArrayList<Integer> prog1Code = parser.parseFile("programs/SharkOSprog1.txt");

        if (!prog1Code.isEmpty()) {
            int baseAddress1 = 300;
            loadProgram(cpu.getMemory(), prog1Code, baseAddress1);

            PCB job1 = new PCB(1);
            job1.PSIAR = baseAddress1;
            os.addJob(job1);
            System.out.println("Program 1 loaded successfully.");
        } else {
            System.err.println("FATAL: Could not load Program 1. Exiting.");
            return;
        }

        // --- ADD MORE PROGRAMS HERE LATER ---

        System.out.println("---------------------------------------");

        // --- 4. Start Simulation ---
        System.out.println("Starting SharkOS...");
        // Tell the OS which CPU to start. This gives the OS its own reference
        // to the main CPU for proactive tasks like starting the first job.
        os.start(cpu);

        // Main simulation loop
        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("---------------------------------------");
        System.out.println("All jobs completed.");

        // --- 5. Verify Results ---
        int finalResult = cpu.getMemory()[103];
        System.out.println("Result of Program 1 at mem[103]: " + finalResult);
        if (finalResult == 150) {
            System.out.println("Verification SUCCESSFUL!");
        } else {
            System.out.println("Verification FAILED! Expected 150.");
        }
    }
}
