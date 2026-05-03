import contract.CpuScheduler;
import model.ExecutionRecord;
import model.Process;
import scheduler.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting OS Scheduler Engine - Multi-threaded Execution \n");

        // Define a common workload of processes for all threads to ensure a fair comparison
        List<Process> baseWorkload = Arrays.asList(
                new Process(1, 3, 10, 0),
                new Process(2, 1, 1, 1),
                new Process(3, 4, 2, 2),
                new Process(4, 2, 3, 3)
        );

        System.out.println("Base Workload (Same for all threads):");
        baseWorkload.forEach(p -> System.out.println(
                "PID: " + p.getId() + " | Arrival: " + p.getArrivalTime() + 
                " | Burst: " + p.getBurstTime() + " | Priority: " + p.getPriority()
        ));
        System.out.println("--------------------------------------------------\n");

        // Initialize all schedulers
        CpuScheduler fcfs = new FCFS();
        CpuScheduler sjf = new SJF();
        CpuScheduler srtf = new SRTF();
        CpuScheduler hrrn = new HRRN();
        CpuScheduler rr = new RoundRobin(2); // Time Quantum = 2
        CpuScheduler priorityNonPrem = new PriorityScheduling("non-prem");
        CpuScheduler priorityPrem = new PriorityScheduling("prem");

        // Create a thread for each scheduling algorithm
        Thread t1 = createSchedulerThread("FCFS", fcfs, baseWorkload);
        Thread t2 = createSchedulerThread("SJF (Non-Preemptive)", sjf, baseWorkload);
        Thread t3 = createSchedulerThread("SRTF (Preemptive SJF)", srtf, baseWorkload);
        Thread t4 = createSchedulerThread("HRRN", hrrn, baseWorkload);
        Thread t5 = createSchedulerThread("Round Robin (Q=2)", rr, baseWorkload);
        Thread t6 = createSchedulerThread("Priority (Non-Preemptive)", priorityNonPrem, baseWorkload);
        Thread t7 = createSchedulerThread("Priority (Preemptive)", priorityPrem, baseWorkload);

        // Start all threads
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();

        try {
            // Wait for all threads to finish before printing the final message
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            t6.join();
            t7.join();
            System.out.println("\nAll scheduling algorithms completed successfully.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a thread for a specific scheduler.
     */
    private static Thread createSchedulerThread(String algoName, CpuScheduler scheduler, List<Process> workload) {
        return new Thread(() -> {
            List<Process> threadWorkload = workload.stream().map(Process::new).collect(Collectors.toList());

            List<ExecutionRecord> records = scheduler.schedule(threadWorkload);

            printResults(algoName, threadWorkload, records);
        });
    }

    /**
     * Synchronized method to print results nicely to the console.
     * Synchronization ensures that threads don't interrupt each other while printing.
     */
    private static synchronized void printResults(String algoName, List<Process> processedList, List<ExecutionRecord> records) {
        System.out.println("==================================================");
        System.out.println("Algorithm: " + algoName);
        System.out.println("==================================================");

        // Print Gantt Chart (Execution Records)
        System.out.print("Gantt Chart: ");
        for (ExecutionRecord record : records) {
            System.out.print("[P" + record.processId() + " : " + record.startTime() + "->" + record.endTime() + "] ");
        }
        System.out.println("\n");

        double totalWT = 0;
        double totalTAT = 0;
        double totalRT = 0;

        System.out.printf("%-5s %-10s %-15s %-15s %-15s\n", "PID", "Wait Time", "Turnaround Time", "Completion Time", "Response Time");
        for (Process p : processedList) {
            System.out.printf("P%-4d %-10d %-15d %-15d %-15d\n", p.getId(), p.getWaitingTime(), p.getTurnaroundTime(), p.getCompletionTime(), p.getResponseTime());
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
            totalRT += p.getResponseTime();
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", (totalWT / processedList.size()));
        System.out.printf("Average Turnaround Time: %.2f\n", (totalTAT / processedList.size()));
        System.out.printf("Average Response Time: %.2f\n", (totalRT / processedList.size()));
        System.out.println("--------------------------------------------------\n");
    }
}