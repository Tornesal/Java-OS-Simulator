package CPU;

import OS.*;

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
    private int MIR;
    private int CSIAR;

    private boolean halted = false;

    // timer for timer interrupts
    private int timer = 0;
    private final int QUANTUM = 100;

    private InterruptHandler interruptHandler;

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
    public int getMIR() {
        return MIR;
    }
    public int getCSIAR() {
        return CSIAR;
    }
    public boolean isHalted() {
        return halted;
    }

    public void setMemory(int[] memory) {
        this.memory = memory;
    }
    public void setACC(int ACC) {
        this.ACC = ACC;
    }
    public void setPSIAR(int PSIAR) {
        this.PSIAR = PSIAR;
    }
    public void setSAR(int SAR) {
        this.SAR = SAR;
    }
    public void setSDR(int SDR) {
        this.SDR = SDR;
    }
    public void setTMPR(int TMPR) {
        this.TMPR = TMPR;
    }
    public void setIR(int IR) {
        this.IR = IR;
    }
    public void setMIR(int MIR) {
        this.MIR = MIR;
    }
    public void setCSIAR(int CSIAR) {
        this.CSIAR = CSIAR;
    }
    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public void setInterruptHandler(InterruptHandler handler) {
        interruptHandler = handler;
    }

    // Raises an interrupt for the OS to handle.
    private void raiseInterrupt(InterruptType type) {
        if (interruptHandler != null && !halted) {
            interruptHandler.handleInterrupt(type, this);
        }
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

        MIR = CSIAR;

        switch (CSIAR) {

            // Fetch and decode the next instruction
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


            // ADD (Opcode 10)
            case 10:
                TMPR = ACC;
                CSIAR = 11;
                break;

            case 11:
                ACC = PSIAR + 1;
                CSIAR = 12;
                break;

            case 12:
                PSIAR = ACC;
                CSIAR = 13;
                break;

            case 13:
                ACC = TMPR;
                CSIAR = 14;
                break;

            case 14:
                TMPR = SDR;
                CSIAR = 15;
                break;

            case 15:
                SAR = TMPR;
                CSIAR = 16;
                break;

            case 16:
                READ();
                CSIAR = 17;
                break;

            case 17:
                TMPR = SDR;
                CSIAR = 18;
                break;

            case 18:
                ACC = ACC + TMPR;
                CSIAR = 19;
                break;

            case 19:
                CSIAR = 0;
                break;


            // SUB (Opcode 20)
            case 20:
                TMPR = ACC;
                CSIAR = 21;
                break;

            case 21:
                ACC = PSIAR + 1;
                CSIAR = 22;
                break;

            case 22:
                PSIAR = ACC;
                CSIAR = 23;
                break;

            case 23:
                ACC = TMPR;
                CSIAR = 24;
                break;

            case 24:
                TMPR = SDR;
                CSIAR = 25;
                break;

            case 25:
                SAR = TMPR;
                CSIAR = 26;
                break;

            case 26:
                READ();
                CSIAR = 27;
                break;

            case 27:
                TMPR = SDR;
                CSIAR = 28;
                break;

            case 28:
                ACC = ACC - TMPR;
                CSIAR = 29;
                break;

            case 29:
                CSIAR = 0;
                break;


            // LOAD (LDA, Opcode 30)
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


            // STORE (Name STR, Opcode 40)
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


            // BRANCH (Name BRH, Opcode 50)
            case 50:
                PSIAR = SDR;
                CSIAR = 51;
                break;

            case 51:
                CSIAR = 0;
                break;


            // COND BRANCH (Name CBR, Opcode 60)
            case 60:
                if (ACC == 0) {
                    CSIAR += 2;
                } else {
                    CSIAR += 1;
                }
                break;

            case 61:
                CSIAR = 64;
                break;

            case 62:
                PSIAR = SDR;
                break;

            case 63:
                CSIAR = 0;
                break;

            case 64:
                TMPR = ACC;
                CSIAR = 65;
                break;

            case 65:
                ACC = PSIAR + 1;
                CSIAR = 66;
                break;

            case 66:
                PSIAR = ACC;
                CSIAR = 67;
                break;

            case 67:
                ACC = TMPR;
                CSIAR = 68;
                break;

            case 68:
                CSIAR = 0;
                break;


            // YIELD (Name YLD, Opcode 98)
            case 98:
                raiseInterrupt(InterruptType.YIELD);
                break;


            // HALT (Name HLT, Opcode 99)
            case 99:
                // Notifies the OS that the CPU has halted
                raiseInterrupt(InterruptType.HALT);
                dumpState();
                halted = true;
                break;

        }

        // Advance timer by one tick for timer interrupt.
        timer++;

        if (timer >= QUANTUM) {
            timer = 0;
            raiseInterrupt(InterruptType.TIMER);
        }

    }
    
    private void dumpState() {
        System.out.println("ACC: " + ACC);
        System.out.println("PSIAR: " + PSIAR);
        System.out.println("SAR: " + SAR);
        System.out.println("SDR: " + SDR);
        System.out.println("TMPR: " + TMPR);
        System.out.println("IR: " + IR);
        System.out.println("MIR: " + MIR);
        System.out.println("CSIAR: " + CSIAR); 
        System.out.println("halted: " + halted);
        System.out.println("End of Job");
        //TODO: Dump the memory
        System.out.println("---------------------------------------");
    }

}
