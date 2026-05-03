package scheduler;

import model.Process;
import model.ExecutionRecord;
import contract.CpuScheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class FCFS
 * <br>
 * This class implements the First-Come, First-Served (FCFS) scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * The FCFS algorithm schedules processes in the order they arrive, without preemption.
 * </p>
 *
 * <h3>How the Simulated Clock Works:</h3>
 * <p>
 * This algorithm uses a single integer variable (<code>currentTime</code>) to simulate the CPU clock.
 * Instead of using real-world time, this variable jumps forward to track exactly when the CPU is idle
 * and how long it takes to execute a process. This ensures perfect, deterministic calculations.
 * </p>
 *
 * <h3>Execution Flow:</h3>
 * <ol>
 *    <li><b>Sort:</b> Processes are sorted by their arrival time (FIFO).</li>
 *    <li><b>Wait for Arrival:</b> If the CPU is idle, advance <code>currentTime</code> to the next process's arrival time.</li>
 *    <li><b>Execute:</b> Advance the clock by the process's burst time (since FCFS is non-preemptive).</li>
 *    <li><b>Calculate Metrics:</b> Compute Response Time, Waiting Time, Turnaround Time, and Completion Time based on the clock's movement.</li>
 *    <li><b>Record:</b> Save the start and end times in the execution record for visualization (Gantt Chart).</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 15-04-2026
 */
public class FCFS implements CpuScheduler {
    /**
     * The main method that schedules the processes based on the FCFS algorithm.
     * @param processes a list of processes to be scheduled
     * @return a list of execution records that contain the start and end times of each process's execution for visualization purposes
     */
    @Override
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();

        processes.sort(Comparator.comparingInt(Process::getArrivalTime)); // Sort processes by arrival time

        int currentTime = 0;
        for (Process process : processes) {
            // if CPU is empty and don't have any processes then add this processes to the CPU and start executing it
            if(currentTime < process.getArrivalTime()) { currentTime = process.getArrivalTime(); }

            // record the start time of the process execution
            int startTime = currentTime;

            // execute the process for its entire burst time
            currentTime += process.getBurstTime();

            // record the response time
            if (process.getResponseTime() == -1) { process.setResponseTime(startTime - process.getArrivalTime()); }

            /*
             * Calculate Waiting Time:
             * Formula: Waiting Time = Start Time - Arrival Time
             * - We use 'startTime' because 'currentTime' has already moved to the end of the execution.
             * - We subtract 'arrivalTime' because waiting time is strictly the duration spent in the Ready Queue.
             */
            process.setWaitingTime(startTime - process.getArrivalTime());

            // record turnaround time
            process.setTurnaroundTime(process.getWaitingTime() + process.getBurstTime());

            // record completion time
            process.setCompletionTime(startTime + process.getBurstTime());

            // save process in execution records
            executionRecords.add(new ExecutionRecord(process.getId(), startTime, currentTime));
        }

        return executionRecords;
    }
}