<div align="center">
  <h1>🚀 OS Scheduler Engine</h1>
  <p><b>Advanced Multi-threaded CPU Scheduling Simulator</b></p>
</div>

## 📖 Overview
This project is a high-performance, multi-threaded CPU Scheduling Simulator built entirely in Java. It demonstrates core Operating System principles, precise time-clock simulation, and advanced Object-Oriented Programming (OOP) architectures. 

Unlike basic simulators, this engine runs multiple scheduling algorithms **concurrently** using Java Threads, ensuring a fair, side-by-side performance comparison using the exact same workload without data interference.

## ✨ Key Features
* **🧵 Multi-threaded Execution:** Algorithms run in parallel threads (`Thread.start()`), demonstrating true concurrent processing capabilities.
* **🛡️ Thread-Safe Architecture:** Utilizes Deep Copying (via custom Copy Constructors) to prevent shared mutable state and race conditions among threads.
* **📊 Comprehensive Metrics:** Accurately calculates Waiting Time (WT), Turnaround Time (TAT), Completion Time (CT), and Response Time (RT) for every process.
* **📈 Visual Gantt Charts:** Generates clean, console-based Gantt charts to track Context Switching and process execution flow block-by-block.
* **🧩 Extensible Design:** Built on a unified `CpuScheduler` interface, enforcing a strict contract that makes it incredibly easy to plug in new algorithms.

## 🧠 Implemented Algorithms
1. **FCFS (First-Come, First-Served):** Non-preemptive, strict FIFO sequence.
2. **SJF (Shortest Job First):** Non-preemptive, optimally minimizes the average waiting time.
3. **SRTF (Shortest Remaining Time First):** Preemptive SJF, dynamically evaluates remaining execution times and handles context switching.
4. **HRRN (Highest Response Ratio Next):** Non-preemptive, effectively solves the SJF starvation problem using dynamic priority calculation.
5. **Round Robin (RR):** Preemptive, handles exact Time Quantum execution and strictly manages edge-case queue traps (simultaneous arrival vs. preemption).
6. **Priority Scheduling:** Implemented in dual-mode (Preemptive & Non-Preemptive) to schedule based on assigned priority levels. 
***Note: to use the preemptive version of priority scheduling write in the constractor 'prem' e.g. 'new PriorityScheduling("prem")' otherwise it will be not preemptive***

## 🏗️ Project Architecture
```text
src/
├── contract/
│   └── CpuScheduler.java       # Base interface enforcing the scheduling contract
├── model/
│   ├── Process.java            # Process entity with metrics & Deep Copy constructor
│   └── ExecutionRecord.java    # Java Record for immutable Gantt chart blocks
├── scheduler/                  # Concrete implementations of the algorithms
│   ├── FCFS.java
│   ├── SJF.java
│   ├── SRTF.java
│   ├── HRRN.java
│   ├── RoundRobin.java
│   └── PriorityScheduling.java
└── Main.java                   # Thread orchestrator & synchronized output formatter
```

## 🚀 Getting Started
> Java Version Requirement: Ensure your environment is set to Java 17 or higher (required for Record types).

1. **Clone the Repository:**
```bash
git clone https://github.com/abdelhalimyasser/operating-system-cpu-scheduling-algorithms.git
cd operating-system-cpu-scheduling-algorithms
```
2. **Compile the Code:**
```bash
javac -sourcepath src src/Main.java -d out
```
3. **Run the Simulator:**
```bash
java -cp out Main
```

<p align="center">
  <strong>FCAI – Capital University ~ (Formerly Helwan University)</strong><br>
  <span>© 2026 <strong>Abdelhalim Yasser</strong>. All Rights Reserved.</span><br>
</p>
