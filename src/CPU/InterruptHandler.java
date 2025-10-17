package CPU;

// Simple interface for interrupt handlers.

public interface InterruptHandler {

    void handleInterrupt(InterruptType type, SharkMachine cpu);

}
