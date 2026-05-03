package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * Class PriorityScheduling
 * <br>
 * This class implements the Priority Scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * Priority Scheduling is a scheduling algorithm that selects processes based on their priority levels.
 * Each process is assigned a priority, and the CPU is allocated to the process with the highest priority (lowest numerical value).
 * In the case of a tie in priority, the process that arrived first is selected.
 * This algorithm can be implemented in both preemptive and non-preemptive forms.
 * </p>
 *
 * <h3>How it works with the Simulated Clock:</h3>
 * <ol>
 *    <li><b>Process Selection:</b> At any given <code>currentTime</code>, the scheduler filters all processes that have arrived and are not completed, then selects the one with the highest priority.</li>
 *    <li><b>Preemptive vs Non-Preemptive:</b>
 *    <ul>
 *        <li><b>Non-Preemptive:</b> Once a process is allocated the CPU, it runs to completion, even if a higher priority process arrives.</li>
 *        <li><b>Preemptive:</b> If a higher priority process arrives while another process is running, the currently running process is preempted and the CPU is allocated to the new higher priority process.</li>
 *    </ul>
 *    </li>
 *    <li><b>Idle Handling:</b> If no processes have arrived at the current time, the scheduler can either fast-forward the clock to the next process's arrival time or simply increment the clock until a process arrives.</li>
 *    <li><b>Metrics Calculation:</b> Once a process completes, its Waiting Time (WT), Turnaround Time (TAT), Completion Time (CT), and Response Time (RT) are calculated based on the current time and its arrival and burst times.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 03-05-2026
 */

public class PriorityScheduling implements CpuScheduler {

    private final boolean isPreemptive;

    // default constructor that initializes the scheduler as non-preemptive
    public PriorityScheduling() {
        this.isPreemptive = false;
    }

    // constructor that takes a string input to determine if the scheduler is preemptive or non-preemptive
    public PriorityScheduling(String input) {
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
     * helper method to get the highest priority of the processes that have arrived by the current time
     * @param temp it is the temp list that have all the processes
     * @param highestPriority it is the process that has the highest priority among the processes that have arrived by the current time
     * @return  the process that has the highest priority among the processes that have arrived by the current time
     */
    private Process setHighestPriority(List<Process> temp, Process highestPriority, int currentTime) {
        // iterate over each process in the processes
        for (Process p : temp) {
            // check if the arrival time is less that the current time
            if (p.getArrivalTime() <= currentTime) {
                // if there is not any process OR the priority of the new process is slower than the current then update the highest priority process
                if (highestPriority == null || p.getPriority() < highestPriority.getPriority()) {
                    highestPriority = p;
                }
                // if the new process and the current process have the same priority, then we will execute the one that arrived first
                else if (p.getPriority() == highestPriority.getPriority() && p.getArrivalTime() < highestPriority.getArrivalTime()) {
                    highestPriority = p;
                }
            }
        }
        return highestPriority;
    }

    /**
     * This method implements the non-preemptive logic of the Priority Scheduling algorithm.
     * @param processes it is the list of processes that we want to schedule
     * @return a list of execution records that contains the execution history of the processes
     */
    private List<ExecutionRecord> scheduleNonPreemptive(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();
        List<Process> temp = new ArrayList<>(processes);

        int currentTime = 0;

        while (!temp.isEmpty()) {
            Process highestPriority = null;

            highestPriority = setHighestPriority(temp, highestPriority, currentTime);

            // check if there is not any process then jump into the next process and update the time
            if (highestPriority == null) {
                currentTime++;
                continue;
            }

            // calculate the regular completions
            int startTime = currentTime;
            currentTime += highestPriority.getBurstTime();

            highestPriority.setWaitingTime(startTime - highestPriority.getArrivalTime());
            highestPriority.setTurnaroundTime(highestPriority.getWaitingTime() + highestPriority.getBurstTime());
            highestPriority.setCompletionTime(currentTime);
            highestPriority.setResponseTime(highestPriority.getWaitingTime());

            executionRecords.add(new ExecutionRecord(highestPriority.getId(), startTime, currentTime));
            temp.remove(highestPriority);
        }

        return executionRecords;
    }

    /**
     * This method implements the preemptive logic of the Priority Scheduling algorithm.
     * @param processes it is the list of processes that we want to schedule
     * @return a list of execution records that contains the execution history of the processesp
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
            Process highestPriority = null;

            highestPriority = setHighestPriority(temp, highestPriority, currentTime);

            // check if there is not any process then jump into the next process and update the time
            if (highestPriority == null) {
                currentTime++;
                continue;
            }

            // Context Switch Detection
            if (currentRunning != highestPriority) {
                // check if there is running process AND the remaining time is greater than 0, then add it to the execution record
                if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
                    executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
                }

                // update the current running process and the start time of the new block
                currentRunning = highestPriority;
                currentBlockStartTime = currentTime;

                // if the response time is not set yet, set it to the current time minus the arrival time
                if (highestPriority.getResponseTime() == -1) {
                    highestPriority.setResponseTime(currentTime - highestPriority.getArrivalTime());
                }
            }

            highestPriority.setRemainingTime(highestPriority.getRemainingTime() - 1);
            currentTime++;

            if (highestPriority.getRemainingTime() == 0) {
                completedCount++;
                executionRecords.add(new ExecutionRecord(highestPriority.getId(), currentBlockStartTime, currentTime));

                highestPriority.setCompletionTime(currentTime);
                highestPriority.setTurnaroundTime(highestPriority.getCompletionTime() - highestPriority.getArrivalTime());
                highestPriority.setWaitingTime(highestPriority.getTurnaroundTime() - highestPriority.getBurstTime());

                temp.remove(highestPriority);
                currentRunning = null;
            }
        }

        return executionRecords;
    }
}