package OS;

// Simple class to hold job details.
public class JobDetails {

    public String fileName;
    public int arrivalTime;
    public int baseAddress;
    public int pid;

    public JobDetails(String f, int a, int b, int p) {
        fileName = f; arrivalTime = a; baseAddress = b; pid = p;
    }

}
