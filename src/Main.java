import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;
import OS.Parser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    // Helper class to define a job before it's loaded
    static class JobSpec {
        String fileName;
        int arrivalTime;
        int baseAddress;
        int pid;

        JobSpec(String fileName, int arrivalTime, int baseAddress, int pid) {
            this.fileName = fileName;
            this.arrivalTime = arrivalTime;
            this.baseAddress = baseAddress;
            this.pid = pid;
        }
    }

    public static void loadProgram(int[] memory, ArrayList<Integer> code, int baseAddress) {
        for (int i = 0; i < code.size(); i++) {
            memory[baseAddress + i] = code.get(i);
        }
    }

    public static void main(String[] args) {
        // System Initialization
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        Parser parser = new Parser();
        cpu.setInterruptHandler((type, machine) -> os.handleInterrupt(type, machine));

        // Arrival Times and job definitions
        List<JobSpec> pendingJobs = new ArrayList<>();

        pendingJobs.add(new JobSpec("programs/sharkosprog1.txt", 0, 100, 1));
        pendingJobs.add(new JobSpec("programs/sharkosprog2.txt", 6, 200, 2));
        pendingJobs.add(new JobSpec("programs/sharkosprog3.txt", 18, 300, 3));
        pendingJobs.add(new JobSpec("programs/sharkosprog4.txt", 64, 400, 1));
        pendingJobs.add(new JobSpec("programs/sharkosprog5.txt", 95, 500, 2));
        pendingJobs.add(new JobSpec("programs/sharkosprog6.txt", 100, 600, 3));

        // Counter for arrival times
        long systemTime = 0;
        boolean osStarted = false;

        System.out.println("----- Starting SharkOS Simulation -----");

        // The simulation runs as long as the OS has work OR jobs are still waiting to arrive
        while (os.hasWork() || !pendingJobs.isEmpty()) {

            // Check for new job arrivals
            Iterator<JobSpec> iterator = pendingJobs.iterator();
            while (iterator.hasNext()) {
                JobSpec jobSpec = iterator.next();
                // Once the job's time arrives, load the program and add it to the ready queue
                if (systemTime >= jobSpec.arrivalTime) {
                    System.out.println("\n----- System Time: " + systemTime + " -> Job PID " + jobSpec.pid + " has arrived! -----");

                    ArrayList<Integer> code = parser.parseFile(jobSpec.fileName);
                    if (!code.isEmpty()) {
                        loadProgram(cpu.getMemory(), code, jobSpec.baseAddress);
                        PCB job = new PCB(jobSpec.pid);
                        job.PSIAR = jobSpec.baseAddress;
                        os.addJob(job);

                        // If this is the very first job, start the OS
                        if (!osStarted) {
                            os.start(cpu);
                            osStarted = true;
                        }
                    } else {
                        System.err.println("Failed to load program: " + jobSpec.fileName);
                    }
                    // Remove the job from the list
                    iterator.remove();
                }
            }

            // If the OS has been started and has work, execute one CPU cycle
            if (osStarted && os.hasWork()) {
                cpu.microStep();
            }

            // Increment the system clock
            systemTime++;
        }

        System.out.println("\n---------------------------------------");
        System.out.println("All jobs completed.");

        // --- 4. Verify Final Results ---
        System.out.println("\n----- Final Memory State Verification -----");
        System.out.println("\n--- Final Memory State Verification ---");
        System.out.println("Job 1 (Sum) Result at mem[13]: " + cpu.getMemory()[13] + " (Expected: 150)");
        System.out.println("Job 2 (Decrement) Result at mem[20]: " + cpu.getMemory()[20] + " (Expected: 0)");
        System.out.println("Job 3 (Increment) Result at mem[30]: " + cpu.getMemory()[30] + " (Expected: 30)");
        System.out.println("Job 4 (Sum Copy) Result at mem[43]: " + cpu.getMemory()[43] + " (Expected: 150)");
        System.out.println("Job 5 (Decrement Copy) Result at mem[50]: " + cpu.getMemory()[50] + " (Expected: 0)");
        System.out.println("Job 6 (Increment Copy) Result at mem[60]: " + cpu.getMemory()[60] + " (Expected: 30)");
    }
}
