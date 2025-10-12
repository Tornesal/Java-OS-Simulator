package CPU;

public class SharkMachine {

    // Array to hold memory
    private int[] memory = new int[1024];

    // Registers
    private int ACC;
    private int PSIAR;
    private int SAR;
    private int SDR;
    private int TMPR;
    private int IR;
    private int CSIAR;

    int currentOpcode;
    int currentOperand;

    private boolean halted = false;

    // Getters
    public int[] getMemory() {
        return memory;
    }

    public int getACC() {
        return ACC;
    }

    public int getPSIAR() {
        return PSIAR;
    }

    public int getSAR() {
        return SAR;
    }

    public int getSDR() {
        return SDR;
    }

    public int getTMPR() {
        return TMPR;
    }

    public int getIR() {
        return IR;
    }

    public int getCSIAR() {
        return CSIAR;
    }

    public boolean isHalted() {
        return halted;
    }

    // Fetches the operand and opcode
    private void fetch() {

        // Fetch address of next instruction
        SAR = PSIAR;

        // read instruction and load it into memory
        SDR = memory[SAR];

        // Store in instruction register
        IR = SDR;

        // Get opcode (Upper 16 bits)
        currentOpcode = (IR >>> 16) & 0xFFFF;

        // Get operand (Lower 16 bits)
        currentOperand = IR & 0xFFFF;

        // Sets up the PSIAR to point to the next instruction
        PSIAR++;

    }

    // Executes the next instruction
    public void step() {

        // Check if halted so we don't fetch next instruction
        if (halted) {
            return;
        }

        // Fetch the next instruction
        fetch();

        if (currentOpcode == 30) {

            SAR = currentOperand;
            SDR = memory[SAR];
            ACC = SDR;

        } else if (currentOpcode == 99) {

            dumpState();
            halted = true;

        }

    }
    
    private void dumpState() {
        System.out.println("ACC: " + ACC);
        System.out.println("PSIAR: " + PSIAR);
        System.out.println("SAR: " + SAR);
        System.out.println("SDR: " + SDR);
        System.out.println("TMPR: " + TMPR);
        System.out.println("IR: " + IR);
        System.out.println("CSIAR: " + CSIAR); 
        System.out.println("halted: " + halted);
        System.out.println("End of Job");
        //TODO: Dump the memory
        System.out.println("---------------------------------------");
    }

}
