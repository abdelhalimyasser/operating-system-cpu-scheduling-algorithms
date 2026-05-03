package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;


/**
 * Class HRRN
 * <br>
 * This class implements the Highest Response Ratio Next (HRRN) scheduling algorithm.
 *
 * <h3>Algorithm Overview:</h3>
 * <p>
 * HRRN is a non-preemptive scheduling algorithm designed to solve the starvation problem
 * found in Shortest Job First (SJF). Instead of solely selecting the process with the
 * shortest burst time, it calculates a dynamic priority based on how long a process has
 * been waiting in the ready queue.
 * </p>
 *
 * <h3>The Formula:</h3>
 * <p>
 * <code>Response Ratio = (Waiting Time + Burst Time) / Burst Time</code>
 * </p>
 *
 * <h3>Execution Flow:</h3>
 * <ol>
 *    <li><b>Filter Arrived Processes:</b> At any given <code>currentTime</code>, the scheduler identifies all processes that have already arrived.</li>
 *    <li><b>Ratio Calculation:</b> For each arrived process, the waiting time is calculated (<code>currentTime - arrivalTime</code>), and then the Response Ratio is computed.</li>
 *    <li><b>Process Selection:</b> The process with the highest Response Ratio is selected.</li>
 *    <li><b>Tie-Breaker:</b> If two processes have the exact same ratio, the algorithm falls back to First-Come-First-Served (FCFS) based on their arrival time.</li>
 *    <li><b>Execute:</b> Since HRRN is non-preemptive, the clock advances by the selected process's entire burst time.</li>
 *    <li><b>Calculate Metrics:</b> Compute Waiting Time (WT), Turnaround Time (TAT), Completion Time (CT), and Response Time (RT) for the completed process.</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 17-04-2026
 */
public class HRRN implements CpuScheduler {
    /**
     * The main method that schedules the processes based on the HRRN algorithm.
     * @param processes a list of processes to be scheduled
     * @return a list of execution records that contain the start and end times of each process's execution for visualization purposes
     */
    @Override
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();
        List<Process> temp =  new ArrayList<>(processes);

        int currentTime = 0;

        while (!temp.isEmpty()) {
            Process selected = null;
            double highestRatio = -1.0;

            for (Process p : temp) {
                // check if the process is arrived or not
                if (p.getArrivalTime() <= currentTime) {
                    // calculate waiting time
                    int waitingTime = currentTime - p.getArrivalTime();
                    // calculate the reposnse ration
                    double responseRatio = (double) (waitingTime + p.getBurstTime()) / p.getBurstTime();

                     /*
                      * check if selected process is empty or the response ratio is higher than the current highest ratio
                      * else if the same ration but one process is arrived before the other, then add it first
                      */
                    if (selected == null || responseRatio > highestRatio) {
                        highestRatio = responseRatio;
                        selected = p;
                    } else if (responseRatio ==  highestRatio && p.getArrivalTime() < selected.getArrivalTime()) {
                        selected = p;
                    }
                }
            }

            // if the process is null or empty then increase the time or iteration and  skip it
            if (selected == null) {
                currentTime++;
                continue;
            }

            // record the start time of the process execution
			int startTime = currentTime;

			// execute the process for its entire burst time
			currentTime += selected.getBurstTime();

			// Calculate Waiting Time
			selected.setWaitingTime(startTime - selected.getArrivalTime());

			// record turnaround time
			selected.setTurnaroundTime(selected.getWaitingTime() + selected.getBurstTime());

			// record completion time
			selected.setCompletionTime(startTime + selected.getBurstTime());

            // record response time (which is the same as waiting time for non-preemptive algorithms)
            selected.setResponseTime(selected.getWaitingTime());

			// save process in execution records
			executionRecords.add(new ExecutionRecord(selected.getId(), startTime, currentTime));

			// remove the process from the temp list
			temp.remove(selected);
        }

        return executionRecords;
    }
}
