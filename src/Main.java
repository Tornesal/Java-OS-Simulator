import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.Parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import OS.JobDetails;

public class Main {
    public static void main(String[] args) {
        // Initialize the OS
        SharkOS os = new SharkOS();

        System.out.println("----- Loading 6 jobs into the system at startup -----");

        // Set up list to hold pending jobs
        ArrayList<JobDetails> jobs = new ArrayList<>();

        // Add 6 jobs to the list
        jobs.add(new JobDetails("programs/sharkosprog1.txt", 0, 100, 1));
        jobs.add(new JobDetails("programs/sharkosprog2.txt", 6, 200, 2));
        jobs.add(new JobDetails("programs/sharkosprog3.txt", 18, 300, 3));
        jobs.add(new JobDetails("programs/sharkosprog4.txt", 64, 400, 4));
        jobs.add(new JobDetails("programs/sharkosprog5.txt", 95, 500, 5));
        jobs.add(new JobDetails("programs/sharkosprog6.txt", 100, 600, 6));

        os.start(jobs);
    }
}
