import CPU.SharkMachine;
import OS.SharkOS;
import OS.PCB;
import OS.ProcessState;

public class Main {
    public static void main(String[] args) {
        SharkMachine cpu = new SharkMachine();
        SharkOS os = new SharkOS();
        cpu.setInterruptHandler(os::handleInterrupt);

        int[] mem = cpu.getMemory();

        // Program 1 @0: LDA 100; STR 101; YLD; HALT
        int base1 = 0;
        mem[base1+0] = (30<<16) | (base1+100);
        mem[base1+1] = (40<<16) | (base1+101);
        mem[base1+2] = (98<<16) | 0;        // YLD
        mem[base1+3] = (99<<16) | 0;        // HALT
        mem[base1+100] = 11;

        // Program 2 @20: LDA 200; STR 201; HALT
        int base2 = 20;
        mem[base2+0] = (30<<16) | (base2+200);
        mem[base2+1] = (40<<16) | (base2+201);
        mem[base2+2] = (99<<16) | 0;        // HALT
        mem[base2+200] = 22;

        PCB job1 = new PCB(1);
        job1.PSIAR = base1;
        job1.state = ProcessState.READY;

        PCB job2 = new PCB(2);
        job2.PSIAR = base2;
        job2.state = ProcessState.READY;

        os.addJob(job1);
        os.addJob(job2);
        os.start(cpu);   // start() stores cpu, re-enqueues job1, then dispatches first

        while (os.hasWork()) {
            cpu.microStep();
        }

        System.out.println("All jobs completed.");
    }
}
