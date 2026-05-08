package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * Class SJF
 * <br>
 * This class implements the Shortest Job First (SJF) scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * SJF selects the process with the smallest execution (burst) time from the ready queue.
 * This algorithm can be implemented in both preemptive and non-preemptive forms.
 * </p>
 *
 * <h3>How it works with the Simulated Clock:</h3>
 * <ol>
 *    <li><b>Process Selection:</b> At any given <code>currentTime</code>, the scheduler filters all processes that have arrived and are not completed, then selects the one with the shortest burst time (non-preemptive) or shortest remaining time (preemptive).</li>
 *    <li><b>Preemptive vs Non-Preemptive:</b>
 *    <ul>
 *        <li><b>Non-Preemptive:</b> Once a process is allocated the CPU, it runs to completion, even if a shorter process arrives.</li>
 *        <li><b>Preemptive (SRTF):</b> If a process with a shorter remaining time arrives while another process is running, the currently running process is preempted and the CPU is allocated to the new shorter process.</li>
 *    </ul>
 *    </li>
 *    <li><b>Tie-Breaker:</b> If two processes have the exact same burst/remaining time, it falls back to First-Come-First-Served (FCFS) based on their <code>arrivalTime</code>.</li>
 *    <li><b>Idle Handling:</b> If no processes have arrived at the current time, the scheduler advances the clock by one tick until a process arrives.</li>
 *    <li><b>Metrics Calculation:</b> Once a process completes, its Waiting Time (WT), Turnaround Time (TAT), Completion Time (CT), and Response Time (RT) are calculated based on the current time and its arrival and burst times.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 2.0
 * @since 07-05-2026
 */
public class SJF implements CpuScheduler {

    private final boolean isPreemptive;

    // default constructor that initializes the scheduler as non-preemptive
    public SJF() {
        this.isPreemptive = false;
    }

    // constructor that takes a string input to determine if the scheduler is preemptive or non-preemptive
    public SJF(String input) {
        this.isPreemptive = input.equalsIgnoreCase("prem");
    }

    // the main method that schedules the processes based on the type of the scheduler (preemptive or non-preemptive)
    @Override
    public List<ExecutionRecord> schedule(List<Process> processes) {
        if (isPreemptive) {
            return schedulePreemptive(processes);
        } else {
            return scheduleNonPreemptive(processes);
        }
    }

    /**
     * helper method to get the shortest process of the processes that have arrived by the current time
     * @param temp it is the temp list that have all the processes
     * @param shortest it is the process that has the shortest burst/remaining time among the processes that have arrived by the current time
     * @param currentTime it is the current time of the simulated clock
     * @param useRemainingTime if true, compares by remaining time (preemptive); otherwise compares by burst time (non-preemptive)
     * @return the process that has the shortest burst/remaining time among the processes that have arrived by the current time
     */
    private Process setShortest(List<Process> temp, Process shortest, int currentTime, boolean useRemainingTime) {
        // iterate over each process in the processes
        for (Process p : temp) {
            // check if the arrival time is less than or equal to the current time
            if (p.getArrivalTime() <= currentTime) {
                int pTime = useRemainingTime ? p.getRemainingTime() : p.getBurstTime();
                int shortestTime = shortest == null ? Integer.MAX_VALUE : (useRemainingTime ? shortest.getRemainingTime() : shortest.getBurstTime());

                // if there is not any process OR the time of the new process is shorter than the current then update the shortest process
                if (shortest == null || pTime < shortestTime) {
                    shortest = p;
                }
                // if the new process and the current process have the same time, then we will execute the one that arrived first
                else if (pTime == shortestTime && p.getArrivalTime() < shortest.getArrivalTime()) {
                    shortest = p;
                }
            }
        }
        return shortest;
    }

    /**
     * This method implements the non-preemptive logic of the SJF scheduling algorithm.
     * @param processes it is the list of processes that we want to schedule
     * @return a list of execution records that contains the execution history of the processes
     */
    private List<ExecutionRecord> scheduleNonPreemptive(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();
        List<Process> temp = new ArrayList<>(processes);

        int currentTime = 0;

        while (!temp.isEmpty()) {
            Process shortest = null;

            shortest = setShortest(temp, shortest, currentTime, false);

            // check if there is not any process then jump into the next process and update the time
            if (shortest == null) {
                currentTime++;
                continue;
            }

            // calculate the regular completions
            int startTime = currentTime;
            currentTime += shortest.getBurstTime();

            shortest.setWaitingTime(startTime - shortest.getArrivalTime());
            shortest.setTurnaroundTime(shortest.getWaitingTime() + shortest.getBurstTime());
            shortest.setCompletionTime(currentTime);
            shortest.setResponseTime(shortest.getWaitingTime());

            executionRecords.add(new ExecutionRecord(shortest.getId(), startTime, currentTime));
            temp.remove(shortest);
        }

        return executionRecords;
    }

    /**
     * This method implements the preemptive logic of the SJF scheduling algorithm (Shortest Remaining Time First).
     * @param processes it is the list of processes that we want to schedule
     * @return a list of execution records that contains the execution history of the processes
     */
    private List<ExecutionRecord> schedulePreemptive(List<Process> processes) {
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

            shortest = setShortest(temp, shortest, currentTime, true);

            // check if there is not any process then jump into the next process and update the time
            if (shortest == null) {
                currentTime++;
                continue;
            }

            // Context Switch Detection
            if (currentRunning != shortest) {
                // check if there is running process AND the remaining time is greater than 0, then add it to the execution record
                if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
                    executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
                }

                // update the current running process and the start time of the new block
                currentRunning = shortest;
                currentBlockStartTime = currentTime;

                // if the response time is not set yet, set it to the current time minus the arrival time
                if (shortest.getResponseTime() == -1) {
                    shortest.setResponseTime(currentTime - shortest.getArrivalTime());
                }
            }

            shortest.setRemainingTime(shortest.getRemainingTime() - 1);
            currentTime++;

            if (shortest.getRemainingTime() == 0) {
                completedCount++;
                executionRecords.add(new ExecutionRecord(shortest.getId(), currentBlockStartTime, currentTime));

                shortest.setCompletionTime(currentTime);
                shortest.setTurnaroundTime(shortest.getCompletionTime() - shortest.getArrivalTime());
                shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());

                temp.remove(shortest);
                currentRunning = null;
            }
        }

        return executionRecords;
    }
}