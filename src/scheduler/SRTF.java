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
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 15-04-2026
 */


public class SRTF implements CpuScheduler {
    public List<ExecutionRecord> schedule(List<Process> processes) {
        List<ExecutionRecord> executionRecords = new ArrayList<>();



        return executionRecords;
    }
}
