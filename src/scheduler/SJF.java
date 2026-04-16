package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Non-Preemptive Shortest Job First (SJF) scheduling algorithm.
 * * <h3>Algorithm Overview:</h3>
 * <p>
 * SJF selects the process with the smallest execution (burst) time from the ready queue.
 * Because this is the non-preemptive version, once a process is allocated the CPU,
 * it holds it until it completes its entire burst time, even if a shorter job arrives.
 * </p>
 *
 * <h3>How it works with the Simulated Clock:</h3>
 * <ol>
 * <li><b>Filter:</b> At any given <code>currentTime</code>, filter all processes that have already arrived and are not completed.</li>
 * <li><b>Select:</b> Pick the process with the minimum <code>burstTime</code>.</li>
 * <li><b>Tie-Breaker:</b> If two processes have the exact same burst time, it falls back to First-Come-First-Served (FCFS) based on <code>arrivalTime</code>.</li>
 * <li><b>Idle Handling:</b> If no processes have arrived yet, fast-forward the clock to the next upcoming process's arrival time.</li>
 * <li><b>Execute:</b> Advance the <code>currentTime</code> by the selected process's burst time and calculate metrics (WT, TAT, CT, RT).</li>
 * </ol>
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 16-04-2026
 */

public class SJF implements CpuScheduler {
	public List<ExecutionRecord> schedule(List<Process> processes) {
		List<ExecutionRecord> executionRecords = new ArrayList<>();

		List<Process> temp = new ArrayList<>(processes);

		int currentTime = 0;

		while (!temp.isEmpty()) {
			Process shortest = null;

			for (Process process : temp) {
				/*
				 * if we found a process that has arrived and is shorter than the current shortest, update shortest
				 * else if we found a process that has the same burst time as the current shortest but arrived earlier, update shortest
				 */
				if (process.getArrivalTime() <= currentTime) {
					if (shortest == null || process.getBurstTime() < shortest.getBurstTime()) {
						shortest = process;
					} else if (process.getBurstTime() ==  shortest.getBurstTime() && process.getArrivalTime() <  shortest.getArrivalTime()) {
						shortest = process;
					}
				}
			}

			// if CPU was idle and no processes have arrived, forward to the next process's arrival time
			if (shortest == null) {
				currentTime++;
				continue;
			}

			// record the start time of the process execution
			int startTime = currentTime;

			// execute the process for its entire burst time
			currentTime += shortest.getBurstTime();

			// Calculate Waiting Time
			shortest.setWaitingTime(startTime - shortest.getArrivalTime());

			// record turnaround time
			shortest.setTurnaroundTime(shortest.getWaitingTime() + shortest.getBurstTime());

			// record completion time
			shortest.setCompletionTime(startTime + shortest.getBurstTime());

            // record response time (which is the same as waiting time for non-preemptive algorithms)
            shortest.setResponseTime(shortest.getWaitingTime());

			// save process in execution records
			executionRecords.add(new ExecutionRecord(shortest.getId(), startTime, currentTime));

			// remove the process from the temp list
			temp.remove(shortest);
		}

		return executionRecords;
	}
}
