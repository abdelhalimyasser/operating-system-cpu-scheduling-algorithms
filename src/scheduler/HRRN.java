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



        return executionRecords;
    }
}
