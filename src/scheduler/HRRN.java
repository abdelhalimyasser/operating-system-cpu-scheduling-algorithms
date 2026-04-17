package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Highest Response Ratio Next (HRRN) scheduling algorithm.
 * * <h3>Algorithm Overview:</h3>
 * <p>
 * HRRN is a Non-Preemptive algorithm designed to solve the Starvation problem of SJF.
 * Instead of just picking the shortest job, it considers how long a process has been waiting.
 * </p>
 *
 * <h3>The Formula:</h3>
 * <p>
 * Response Ratio = (Waiting Time + Burst Time) / Burst Time
 * </p>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 17-04-2026
 */

public class HRRN implements CpuScheduler {
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
