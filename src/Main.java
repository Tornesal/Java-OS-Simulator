import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.Parser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import OS.JobDetails;

public class Main {
    public static void loadProgram(int[] memory, ArrayList<Integer> code, int baseAddress) {
        for (int i = 0; i < code.size(); i++) {
            memory[baseAddress + i] = code.get(i);
        }
    }

    public static void main(String[] args) {
        // Initialize the CPU and OS
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        Parser parser = new Parser();
        cpu.setInterruptHandler(os);

        System.out.println("--- Loading 6 jobs into the system at startup ---");


        // Set up list to hold pending jobs
        List<JobDetails> pendingJobs = new ArrayList<>();

        // Add 6 jobs to the list
        pendingJobs.add(new JobDetails("programs/sharkosprog1.txt", 0, 100, 1));
        pendingJobs.add(new JobDetails("programs/sharkosprog2.txt", 6, 200, 2));
        pendingJobs.add(new JobDetails("programs/sharkosprog3.txt", 18, 300, 3));
        pendingJobs.add(new JobDetails("programs/sharkosprog4.txt", 64, 400, 4));
        pendingJobs.add(new JobDetails("programs/sharkosprog5.txt", 95, 500, 5));
        pendingJobs.add(new JobDetails("programs/sharkosprog6.txt", 100, 600, 6));

        // Counter for arrival times
        long systemTime = 0;
        boolean osStarted = false;

        System.out.println("----- Starting SharkOS Simulation -----");

        // The simulation runs as long as the OS has work OR jobs are still waiting to arrive
        while (os.hasWork() || !pendingJobs.isEmpty()) {

            // Check for new job arrivals
            Iterator<JobDetails> iterator = pendingJobs.iterator();
            while (iterator.hasNext()) {

                JobDetails jobs = iterator.next();

                // Once the job's time arrives, load the program and add it to the ready queue
                if (systemTime >= jobs.arrivalTime) {
                    System.out.println("\n----- System Time: " + systemTime + " -> Job PID " + jobs.pid + ", Job Name: " + "\"" + jobs.fileName + "\"" + " has been added to the OS queue -----");

                    ArrayList<Integer> code = parser.parseFile(jobs.fileName);

                    // if the file was parsed successfully
                    if (!code.isEmpty()) {

                        loadProgram(cpu.getMemory(), code, jobs.baseAddress);
                        PCB job = new PCB(jobs.pid);
                        job.PSIAR = jobs.baseAddress;
                        os.addJob(job);

                        // If this is the very first job, start the OS
                        if (!osStarted) {
                            os.start(cpu);
                            osStarted = true;
                        }

                    } else {
                        System.err.println("Failed to load program: " + jobs.fileName);
                    }

                    // Remove the job from the list
                    iterator.remove();
                }
            }

            // If the OS has been started and has work, execute one CPU cycle
            if (osStarted && os.hasWork()) {
                cpu.microStep();
            }

            // Increment the system clock for the simulation
            systemTime++;

        }

        System.out.println("\n---------------------------------------\n");
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
