package model;

/**
 * This is record to store the execution details of a process,
 * it will be used to keep track of the order and timing of process execution in the scheduling algorithms.
 *
 * @param processId the id of the process that is being executed
 * @param startTime the time at which the process starts execution
 * @param endTime the time at which the process finishes execution
 *
 * @author Abdelhalim Yasser
 * @version 1.0
 * @since 15-04-2026
 */


public record ExecutionRecord(int processId, int startTime, int endTime) {}
