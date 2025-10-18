package OS;

import CPU.SharkMachine;

public class PCB {

    public final int pid;

    // CPU context to be saved
    public int ACC, PSIAR, SAR, SDR, TMPR, IR, MIR, CSIAR;
    public boolean halted;
    public ProcessState state = ProcessState.NEW;

    public PCB(int pid) {
        this.pid = pid;
    }

    // Saves the CPU state into the PCB
    public void saveContext(SharkMachine cpu) {

        ACC = cpu.getACC();
        PSIAR = cpu.getPSIAR();
        SAR = cpu.getSAR();
        SDR = cpu.getSDR();
        TMPR = cpu.getTMPR();
        IR = cpu.getIR();
        MIR = cpu.getMIR();
        CSIAR = cpu.getCSIAR();
        halted = cpu.isHalted();

    }

    // Restores the CPU state from the PCB
    public void restoreContext(SharkMachine cpu) {

        cpu.setACC(ACC);
        cpu.setPSIAR(PSIAR);
        cpu.setSAR(SAR);
        cpu.setSDR(SDR);
        cpu.setTMPR(TMPR);
        cpu.setIR(IR);
        cpu.setMIR(MIR);
        cpu.setCSIAR(CSIAR);
        cpu.setHalted(halted);

        state = ProcessState.RUNNING;

    }

}
