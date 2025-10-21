package OS;

// Simple class to hold job details.
public class JobDetails {

    public String fileName;
    public int arrivalTime;
    public int baseAddress;
    public int pid;

    public JobDetails(String fileName, int arrivalTime, int baseAddress, int pid) {

        this.fileName = fileName;
        this.arrivalTime = arrivalTime;
        this.baseAddress = baseAddress;
        this.pid = pid;

    }
}
