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

        // Fetch the next instruction
        fetch();

        if (currentOpcode == 30) {
            SAR = currentOperand;
            SDR = memory[SAR];
            ACC = SDR;
        }

    }

}
