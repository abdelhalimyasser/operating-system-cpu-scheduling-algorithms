package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * Class SRTF
 * <br>
 * This class implements the Shortest Remaining Time First (SRTF) scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * SRTF is the preemptive version of the Shortest Job First (SJF) algorithm.
 * It actively monitors the ready queue. If a new process arrives with a shorter
 * remaining execution time than the currently running process, the CPU preempts
 * (pauses) the current process and allocates execution to the new, shorter process.
 * </p>
 *
 * <h3>Execution Flow:</h3>
 * <ol>
 *    <li><b>Tick-by-Tick Evaluation:</b> The simulated clock moves forward 1 unit at a time to continuously check for new arrivals and evaluate remaining times.</li>
 *    <li><b>Process Selection:</b> At each time unit, the scheduler filters arrived processes and selects the one with the absolute shortest remaining time.</li>
 *    <li><b>Context Switching:</b> If the shortest process changes from the previously running one, the current execution block is saved, and a new execution block starts for the new process.</li>
 *    <li><b>Preemption:</b> The selected process executes for 1 time unit, and its remaining time is decremented.</li>
 *    <li><b>Calculate Metrics:</b> Because a process can start and pause multiple times, metrics are calculated only when its remaining time reaches 0, using the formula: <code>WT = Turnaround Time - Burst Time</code>.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 17-04-2026
 */
public class SRTF implements CpuScheduler {
    /**
     * The main method that schedules the processes based on the SRTF algorithm.
     * @param processes a list of processes to be scheduled
     * @return a list of execution records that contain the start and end times of each process's execution for visualization purposes
     */
    @Override
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();
        List<Process> temp = new ArrayList<>(processes);

        for (Process p : temp) {
            p.setRemainingTime(p.getBurstTime());
        }

        int currentTime = 0;
        int completedCount = 0;
        int n = temp.size();

        Process currentRunning = null;
        int currentBlockStartTime = -1;

        while (completedCount < n) {
            Process shortest = null;

            for (Process p : temp) {
                // check is the process arrived or not
                if (p.getArrivalTime() <= currentTime) {
                    /*
                     * if we found a process that has arrived and has less remaining time than the current shortest, update shortest
                     * else if we found a process that has the same remaining time as the current shortest but arrived earlier, update shortest
                     * */
                    if (shortest == null || p.getRemainingTime() < shortest.getRemainingTime()) {
                        shortest = p;
                    } else if (p.getRemainingTime() == shortest.getRemainingTime() && p.getArrivalTime() < shortest.getArrivalTime()) {
                        shortest = p;
                    }
                }
            }

            // if CPU was idle and no processes have arrived, forward to the next process's arrival time
			if (shortest == null) {
				currentTime++;
				continue;
			}

            // if there is context-switching
            if (currentRunning != shortest) {
                // if there is an process is running add it to record it
                if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
                    executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
                }

                // start a new block for the new process
                currentRunning = shortest;
                currentBlockStartTime = currentTime;

                // if the process is starting for the first time, set its response time
                if (shortest.getResponseTime() == -1) {
                    shortest.setResponseTime(currentTime - shortest.getArrivalTime());
                }
            }

            // execute the process for 1 time unit
            shortest.setRemainingTime(shortest.getRemainingTime() - 1);
            currentTime++;

            // if the process is completed, calculate its metrics and remove it from the temp list
            if (shortest.getRemainingTime() == 0) {
                completedCount++;

                // record the execution block for the completed process
                executionRecords.add(new ExecutionRecord(shortest.getId(), currentBlockStartTime, currentTime));

                // calculate completion time, turnaround time, and waiting time for the completed process
                shortest.setCompletionTime(currentTime);
                shortest.setTurnaroundTime(shortest.getCompletionTime() - shortest.getArrivalTime());
                shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());

                // remove the process from the temp list
                temp.remove(shortest);
                currentRunning = null;
            }
        }

        return executionRecords;
    }
}
