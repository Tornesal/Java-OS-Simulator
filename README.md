# SharkOS: A Simulated CPU and Multitasking Operating System in Java

###  ACADEMIC INTEGRITY NOTICE 
**This repository contains my solution for a university programming assignment. It is intended for portfolio and demonstration purposes only.** If you are a student, please be aware that submitting any part of this code as your own work is a serious violation of academic integrity.

---

## Overview

SharkOS is a complete simulation of a computer system, featuring a custom-built CPU and a preemptive multitasking operating system, developed entirely in Java. This project demonstrates a deep understanding of low-level computer architecture, operating system theory, and complex software design.

The simulation is capable of loading multiple custom-written assembly programs from text files, managing them as concurrent processes, and executing them using a round-robin scheduling algorithm.

**[View the Full Design Document Here](./docs/Design_Document.docx)**

## Key Features

- **Microcode-Level CPU Simulation:** The "Shark Machine" CPU is simulated at the micro-operation level, faithfully executing a custom instruction set as defined by a state machine driven by a micro-program counter (`CSIAR`).
- **Preemptive Multitasking:** The SharkOS kernel implements a round-robin scheduler with a fixed time quantum. A logical timer in the CPU generates hardware interrupts to enforce preemption, allowing for fair distribution of CPU time among all active processes.
- **Complete Context Switching:** The OS performs a full context switch upon every interrupt. This includes saving and restoring the entire state of a process, from architectural registers (`ACC`, `PSIAR`) down to internal, non-architectural registers (`nextOpcode`, `nextOperand`) to ensure seamless resumption of execution.
- **Interrupt Handling:** A robust interrupt mechanism handles signals for timer expirations (`TIMER`), software yields (`YLD`), and program termination (`HALT`), allowing the OS to manage the process lifecycle.
- **Dynamic Program Loader:** A workload of six or more jobs with specified arrival times is managed by the simulation driver, which feeds new processes into the OS kernel as the simulation clock advances.

## Architectural Design

The system is built on a clean, layered architecture that separates the responsibilities of the hardware, the operating system, and the simulation manager. The `InterruptHandler` interface serves as the critical communication link between the hardware and software layers.

### Process Lifecycle

The SharkOS manages the complete lifecycle of a process, transitioning it between states in response to system events like scheduling decisions and interrupts.


## Technical Details

- **Language:** Java
- **Core Concepts:** CPU Simulation, Operating System Design, Process Management, Preemptive Scheduling, Interrupt Handling, Context Switching.

## How to Run

The project is designed to be compiled and run from the command line, with no external dependencies other than a standard JDK.

1.  **Navigate to the project root directory.**
2.  **Compile all source files:**
    ```bash
    javac -sourcepath src -d out src/*.java src/CPU/*.java src/OS/*.java
    ```
3.  **Run the simulation:**
    ```bash
    java -cp out Main
    ```
Upon completion, detailed state dumps for each terminated job will be available in the `/logs` directory.
