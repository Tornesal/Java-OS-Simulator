import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
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
        cpu.setInterruptHandler(os);

        System.out.println("--- Loading 6 jobs into the system at startup ---");

        // --- 2. Load All 6 Programs and Create PCBs ---
        // Program 1
        ArrayList<Integer> prog1Code = parser.parseFile("programs/sharkosprog1.txt");
        loadProgram(cpu.getMemory(), prog1Code, 100);
        PCB job1 = new PCB(1);
        job1.PSIAR = 100;

        // Program 2
        ArrayList<Integer> prog2Code = parser.parseFile("programs/sharkosprog2.txt");
        loadProgram(cpu.getMemory(), prog2Code, 200);
        PCB job2 = new PCB(2);
        job2.PSIAR = 200;

        // Program 3
        ArrayList<Integer> prog3Code = parser.parseFile("programs/sharkosprog3.txt");
        loadProgram(cpu.getMemory(), prog3Code, 300);
        PCB job3 = new PCB(3);
        job3.PSIAR = 300;

        // Program 4 (Copy of 1)
        ArrayList<Integer> prog4Code = parser.parseFile("programs/sharkosprog4.txt");
        loadProgram(cpu.getMemory(), prog4Code, 400);
        PCB job4 = new PCB(4);
        job4.PSIAR = 400;

        // Program 5 (Copy of 2)
        ArrayList<Integer> prog5Code = parser.parseFile("programs/sharkosprog5.txt");
        loadProgram(cpu.getMemory(), prog5Code, 500);
        PCB job5 = new PCB(5);
        job5.PSIAR = 500;

        // Program 6 (Copy of 3)
        ArrayList<Integer> prog6Code = parser.parseFile("programs/sharkosprog6.txt");
        loadProgram(cpu.getMemory(), prog6Code, 600);
        PCB job6 = new PCB(6);
        job6.PSIAR = 600;

        // --- 3. Add All 6 Jobs to the OS Ready Queue ---
        os.addJob(job1);
        os.addJob(job2);
        os.addJob(job3);
        os.addJob(job4);
        os.addJob(job5);
        os.addJob(job6);

        // --- 4. Start the Simulation ---
        System.out.println("\n--- Starting SharkOS multitasking simulation ---");
        os.start(cpu); // Loads the first job and kicks off the process

        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("\n---------------------------------------");
        System.out.println("All jobs completed.");

        // --- 5. Verify All 6 Results ---
        System.out.println("\n--- Final Memory State Verification ---");
        System.out.println("Job 1 Result at mem[13]: " + cpu.getMemory()[13] + " (Expected: 150)");
        System.out.println("Job 2 Result at mem[20]: " + cpu.getMemory()[20] + " (Expected: 0)");
        System.out.println("Job 3 Result at mem[30]: " + cpu.getMemory()[30] + " (Expected: 30)");
        System.out.println("Job 4 Result at mem[43]: " + cpu.getMemory()[43] + " (Expected: 150)");
        System.out.println("Job 5 Result at mem[50]: " + cpu.getMemory()[50] + " (Expected: 0)");
        System.out.println("Job 6 Result at mem[60]: " + cpu.getMemory()[60] + " (Expected: 30)");
    }
}
