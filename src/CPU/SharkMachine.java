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

    private boolean halted = false;

    // Need these to make sure we do not condense the CSIAR < decoded IR (OP CODE) and SDR < decoded IR (Operand) into one case.
    // aka it allows us to keep accuracy with the CPU architecture.
    private int nextOpcode;
    private int nextOperand;

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

    // Read and Write functions
    private void WRITE() {
        memory[SAR] = SDR;
    }

    private void READ() {
        SDR = memory[SAR];
    }

    public void microStep() {
        if (halted) {
            return;
        }

        switch (CSIAR) {

            // CSIAR stuff
            case 0:
                SAR = PSIAR;
                CSIAR = 1;
                break;

            case 1:
                READ();
                CSIAR = 2;
                break;

            case 2:
                IR = SDR;
                CSIAR = 3;
                break;

            case 3:
                nextOpcode = (IR >>> 16) & 0xFFFF;
                nextOperand = IR & 0xFFFF;
                CSIAR = 4;
                break;

            // Now we can jump to the actual instruction called
            case 4:
                SDR = nextOperand;
                CSIAR = nextOpcode;
                break;


            // LDA
            case 30:
                ACC = PSIAR + 1;
                CSIAR = 31;
                break;

            case 31:
                PSIAR = ACC;
                CSIAR = 32;
                break;

            case 32:
                TMPR = SDR;
                CSIAR = 33;
                break;

            case 33:
                SAR = TMPR;
                CSIAR = 34;
                break;

            case 34:
                READ();
                CSIAR = 35;
                break;

            case 35:
                ACC = SDR;
                CSIAR = 36;
                break;

            // Finished LDA, return to case 0 to fetch next instruction
            case 36:
                CSIAR = 0;
                break;


            // STR
            case 40:
                TMPR = ACC;
                CSIAR = 41;
                break;

            case 41:
                ACC = PSIAR + 1;
                CSIAR = 42;
                break;

            case 42:
                PSIAR = ACC;
                CSIAR = 43;
                break;

            case 43:
                ACC = TMPR;
                CSIAR = 44;
                break;

            case 44:
                TMPR = SDR;
                CSIAR = 45;
                break;

            case 45:
                SAR = TMPR;
                CSIAR = 46;
                break;

            case 46:
                SDR = ACC;
                CSIAR = 47;
                break;

            case 47:
                WRITE();
                CSIAR = 48;
                break;

            case 48:
                CSIAR = 0;
                break;


            // HALT CPU
            case 99:
                dumpState();
                halted = true;
                break;

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
