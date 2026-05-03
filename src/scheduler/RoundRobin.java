package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.*;

/**
 * Class RoundRobin
 * <br>
 * This class implements the Round Robin (RR) scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * RR is a preemptive scheduling algorithm that assigns a fixed time unit,
 * known as a Time Quantum, to each process in a cyclic order. It heavily
 * relies on a FIFO (First-In-First-Out) Ready Queue to manage execution evenly
 * among all available processes.
 * </p>
 *
 * <h3>Execution Flow:</h3>
 * <ol>
 *    <li><b>Queue Initialization:</b> At any given <code>currentTime</code>, newly arrived processes are added to the back of the ready queue.</li>
 *    <li><b>Quantum Execution:</b> The scheduler polls the process at the front of the queue and allocates the CPU to it for a maximum duration equal to the <code>timeQuantum</code>.</li>
 *    <li><b>The Queue Trap (Edge Case Handled):</b> If a process exhausts its quantum at the exact same time a new process arrives, the newly arrived process is added to the queue <b>before</b> the preempted process is re-added.</li>
 *    <li><b>Re-queue or Complete:</b> If the running process still has remaining time after its quantum, it goes back to the end of the queue. If it finishes, it is marked as completed.</li>
 *    <li><b>Calculate Metrics:</b> Turnaround Time (TAT) and Waiting Time (WT) are calculated only when the process fully completes its entire execution.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 17-04-2026
 */
public class RoundRobin implements CpuScheduler {

    private final int timeQuantum;

    /**
     * Constructor for RoundRobin scheduler that takes the time quantum as a parameter.
     * @param timeQuantum the fixed time unit that each process will be allowed to execute before being preempted and moved to the back of the queue
     */
    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    /**
     * The main method that schedules the processes based on the Round Robin algorithm.
     * @param processes a list of processes to be scheduled
     * @return a list of execution records that contain the start and end times of each process's execution for visualization purposes
     */
    @Override
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();
        List<Process> pendingProcesses = new ArrayList<>(processes);

        // Initialize remaining time for each process
        for (Process p : pendingProcesses) {
            p.setRemainingTime(p.getBurstTime());
        }

        // Sort processes by arrival time to ensure correct order of execution
        pendingProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        int completedCount = 0;
        int n = processes.size();

        // Build Queue using linked list for efficient usage
        Queue<Process> readyQueue = new LinkedList<>();

        // Add processes that arrive at time 0 to the ready queue
        while (!pendingProcesses.isEmpty() && pendingProcesses.getFirst().getArrivalTime() <= currentTime) {
            readyQueue.add(pendingProcesses.removeFirst());
        }

        while (completedCount < n) {
            // If the ready queue is empty, jump to the next process
            if (readyQueue.isEmpty()) {
                // if there are still pending processes, jump to the arrival time of the next process
                if (!pendingProcesses.isEmpty()) {
                    currentTime = pendingProcesses.getFirst().getArrivalTime();
                    while (!pendingProcesses.isEmpty() && pendingProcesses.getFirst().getArrivalTime() <= currentTime) {
                        readyQueue.add(pendingProcesses.removeFirst());
                    }
                }
                continue;
            }

            Process currentProcess = readyQueue.poll();

            // Set response time if it's the first time the process is being executed
            if (currentProcess.getResponseTime() == -1) {
                currentProcess.setResponseTime(currentTime - currentProcess.getArrivalTime());
            }

            // set the execution time is the minimum between the time quantum and the remaining time of the process
            int executionTime = Math.min(timeQuantum, currentProcess.getRemainingTime());
            int startTime = currentTime;

            currentTime += executionTime;
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executionTime);

            // Record the execution of the current process
            executionRecords.add(new ExecutionRecord(currentProcess.getId(), startTime, currentTime));

            // Add any new processes that have arrived during the execution of the current process to the ready queue
            while (!pendingProcesses.isEmpty() && pendingProcesses.getFirst().getArrivalTime() <= currentTime) {
                readyQueue.add(pendingProcesses.removeFirst());
            }

            // Handle the edge case where a process finishes its quantum at the same time a new process arrives
            if (currentProcess.getRemainingTime() > 0) {
                readyQueue.add(currentProcess);
            } else {
                // Process has completed execution, make the default calculations for the process
                completedCount++;
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
            }
        }

        return executionRecords;
    }
}