package OS;

public class PCB {

    public final int pid;

    // CPU context to be saved
    public int ACC, PSIAR, SAR, SDR, TMPR, IR, MIR, CSIAR, nextOpcode, nextOperand;
    public boolean halted;
    public ProcessState state = ProcessState.NEW;

    public PCB(int pid) {
        this.pid = pid;
    }

}
