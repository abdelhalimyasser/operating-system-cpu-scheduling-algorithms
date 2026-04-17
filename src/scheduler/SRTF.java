package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Shortest Remaining Time First (SRTF) scheduling algorithm,
 * which is a preemptive version of the Shortest Job First (SJF) algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * SRTF actively monitors the ready queue. If a new process arrives with a shorter
 * remaining execution time than the currently running process, the CPU preempts
 * (pauses) the current process and allocates execution to the new, shorter process.
 * </p>
 *
 * <h3>How it works with the Simulated Clock:</h3>
 * <ol>
 * <li><b>Tick-by-Tick Evaluation:</b> The clock moves forward 1 unit at a time to check for new arrivals.</li>
 * <li><b>Context Switching:</b> If the shortest process changes from the previously running one, an execution block is saved, and a new one starts.</li>
 * <li><b>Metrics Trick:</b> Because a process can start and stop multiple times, Waiting Time is calculated at the very end using the formula: <code>WT = Turnaround Time - Burst Time</code>.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 17-04-2026
 */


public class SRTF implements CpuScheduler {
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
