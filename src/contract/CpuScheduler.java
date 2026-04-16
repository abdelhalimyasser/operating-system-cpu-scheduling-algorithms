package contract;

import model.ExecutionRecord;
import model.Process;

import java.util.List;

/**
 * This interface defines the contract for CPU scheduling algorithms.
 * Any class that implements this interface must provide an implementation for the schedule method,
 * which takes a list of processes and returns a list of execution records indicating the order and timing of process execution.
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 15-04-2026
 */


public interface CpuScheduler {
    List<ExecutionRecord> schedule(List<Process> processes);
}
