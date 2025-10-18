package OS;

// Defines the state of a process. Used in the PCB to keep track of process status.

public enum ProcessState {

    NEW,
    READY,
    RUNNING,
    WAITING,
    DONE

}
