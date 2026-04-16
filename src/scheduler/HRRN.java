package scheduler;

import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Highest Response Ratio Next (HRRN) scheduling algorithm.
 * The process with the highest response ratio is selected for execution next, which helps to reduce waiting time and improve overall system performance.
 * The HRRN algorithm is non-preemptive, meaning that once a process starts executing, it will run to completion before the next process is scheduled.
 * The HRRN algorithm schedules processes based on the highest response ratio, which is calculated as:
 * Response Ratio = (Waiting Time + Burst Time) / Burst Time
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 15-04-2026
 */

public class HRRN implements CpuScheduler {
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();



        return executionRecords;
    }
}
