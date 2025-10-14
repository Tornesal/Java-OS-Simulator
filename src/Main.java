import CPU.SharkMachine;

void main() {

    SharkMachine cpu = new SharkMachine();

    // --- Seed a tiny program: LDA 100; STR 101; HALT ---
    // encode(op, operand) = (op << 16) | operand
    int[] mem = cpu.getMemory();
    mem[0] = (30 << 16) | 100;  // LDA 100
    mem[1] = (40 << 16) | 101;  // STR 101
    mem[2] = (99 << 16) | 0;    // HALT
    mem[100] = 42;              // data to load

    // --- Start state: fetch begins at 0 ---
    // PSIAR defaults to 0 in your class; ensure CSIAR = 0 to start microcode fetch (00).
    // If you have a setter, use it; otherwise rely on default.

    // --- Run micro-steps until HALT ---
    while (!cpu.isHalted()) {
        cpu.microStep();
    }

    // --- Quick verification ---
    System.out.println("After HALT:");
    System.out.println("ACC should be 42 -> " + cpu.getACC());
    System.out.println("mem[101] should be 42 -> " + cpu.getMemory()[101]);
}
