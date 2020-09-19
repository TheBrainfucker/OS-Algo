import java.util.*;
import java.io.File;

public class PriorityScheduling {
    // find average
    public static double avg(int a[][], int n) {
        // sum of array element
        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i][n];
        }
        return sum / a.length;
    }

    public static void priorityScheduling(int[][] process) {
        // [process, arrival, burst, priority, remaining, start, finish, response,
        // turnaround, waiting]

        // clone process array
        int[][] proc = new int[process.length][];
        for (int i = 0; i < process.length; i++) {
            int[] aMatrix = process[i];
            int aLength = aMatrix.length;
            proc[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, proc[i], 0, aLength);
        }

        // sort cloned array by arrival time
        Arrays.sort(proc, Comparator.comparingInt(a -> a[1]));

        // queue based on arival time
        Queue<int[]> q_arrival = new LinkedList<>();
        for (int i = 0; i < proc.length; i++) {
            q_arrival.add(proc[i]);
        }

        // running time = sorted first process's arrival time
        int time_elapsed = proc[0][1];

        // list based on priority
        ArrayList<int[]> li_priority = new ArrayList<>();

        // multiple process can arrive at the beginning
        while (q_arrival.size() != 0 && q_arrival.peek()[1] == time_elapsed) {
            li_priority.add(q_arrival.poll());
        }
        li_priority.sort(Comparator.comparingInt(a -> a[3]));// sort list by priority

        int[] temp_proc;
        // run as long as both arrival & waiting queues are not empty
        while (q_arrival.size() != 0 || li_priority.size() != 0) {
            if (li_priority.size() != 0) {
                temp_proc = li_priority.get(0); // get process from the list sorted by priority
                if (temp_proc[2] == temp_proc[4]) { // burst=remaining means 1st time for the process
                    temp_proc[5] = time_elapsed; // set starting time
                }
                time_elapsed++;
                for (; li_priority.size() != 0; time_elapsed++) { // time++ loop until list is empty
                    temp_proc[4]--; // as time_elapsed++, remaining time--
                    if (temp_proc[4] == 0) { // remaining time=0 means process is done
                        temp_proc[6] = time_elapsed; // set finish time to the process
                        temp_proc[7] = temp_proc[5] - temp_proc[1]; // response = start - arrival
                        temp_proc[8] = temp_proc[6] - temp_proc[1]; // turnaround = finish - arrival
                        temp_proc[9] = temp_proc[8] - temp_proc[2]; // waiting = turnaround - burst
                        li_priority.remove(0); // need to remove as it's done
                    }

                    // add other process to list whose arrival times are equal to the time elapsed
                    while (q_arrival.size() != 0 && q_arrival.peek()[1] == time_elapsed) {
                        li_priority.add(q_arrival.poll());
                    }
                    if (li_priority.size() != 0) { // new process can arrive or not
                        li_priority.sort(Comparator.comparingInt(a -> a[3])); // sort by priority
                        temp_proc = li_priority.get(0);
                        if (temp_proc[2] == temp_proc[4]) { // burst=remaining means 1st time for the process
                            temp_proc[5] = time_elapsed; // set starting time
                        }
                    }
                }
            }
            // when waiting queue is empty but other processes are yet to be arrived
            else if (q_arrival.size() != 0 || li_priority.size() == 0) {
                time_elapsed = (q_arrival.peek())[1]; // start agian from new process's arriving time
                while (q_arrival.size() != 0 && q_arrival.peek()[1] == time_elapsed) {
                    li_priority.add(q_arrival.poll());
                }
                li_priority.sort(Comparator.comparingInt(a -> a[3]));
            }
        }

        Arrays.sort(proc, Comparator.comparingInt(a -> a[0])); // sort by process number to print out

        // print out
        System.out.println();
        System.out.println(String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s", "Processes",
                "Arrival", "Burst", "Priority", "Start", "Finish", "Response", "Turnaround", "Waiting"));
        for (int i = 0; i < proc.length; i++) {
            System.out.println(String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s", (i + 1),
                    proc[i][1], proc[i][2], proc[i][3], proc[i][5], proc[i][6], proc[i][7], proc[i][8], proc[i][9]));
        }

        System.out.println();
        System.out.println("Avg turnaround time = " + avg(proc, 8)); // 8 is the index of turnaround in proc[] array
        System.out.println("Avg waiting time = " + avg(proc, 9)); // 9 is the index of waiting in proc[] array
        System.out.println();
    }

    public static void main(String[] agrs) {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("Type 0 to input from .txt file || Type 1 to input manually.");
        int select = sc.nextInt();
        if (select == 0) {
            try {// read from PrioritySchedulingInput.txt file
                 // 1st line contains total processes number
                 // 2nd line is time quantum
                 // 3rd line: arrival time followed by a space and burst time
                File fileName = new File("PrioritySchedulingInput.txt"); // CAREFUL WITH .txt FILE PATH
                // System.out.println(fileName.getAbsolutePath()); // print out
                // fileName.getAbsolutePath() if you're confused
                Scanner scan = new Scanner(fileName);
                String firstLine = scan.nextLine();
                int total_process = Integer.parseInt(firstLine);

                int[][] process = new int[total_process][10];

                for (int i = 0; i < total_process; i++) {
                    String line = scan.nextLine();
                    try {
                        String[] stringArray = line.split(" ", 3);// splitting by space

                        process[i][0] = i;// setting process number
                        process[i][1] = Integer.parseInt(stringArray[0]); // arrival
                        process[i][2] = Integer.parseInt(stringArray[1]); // burst
                        process[i][3] = Integer.parseInt(stringArray[2]); // priority
                        process[i][4] = process[i][2]; // setting remaining time = burst time
                    } catch (NumberFormatException e) {
                        e.getStackTrace();
                    }
                }
                scan.close();
                System.out.println();
                System.out.println("Preemptive Priority Scheduling ALGO");
                priorityScheduling(process);
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (select == 1) {
            System.out.println();
            System.out.print("Preemptive Priority Scheduling ALGO\nHow many processes: ");
            int total_process = sc.nextInt();

            int[][] process = new int[total_process][10];

            for (int i = 0; i < total_process; i++) {
                System.out.print("What's the arrival, burst time & priority of porcess " + (i + 1) + ": ");
                process[i][0] = i;// setting process number
                process[i][1] = sc.nextInt(); // arrival
                process[i][2] = sc.nextInt(); // burst
                process[i][3] = sc.nextInt(); // priority
                process[i][4] = process[i][2]; // setting remaining time = burst time
            }
            sc.close();
            priorityScheduling(process);
        } else {
            System.out.println("Choose either 0 or 1 -_-");
        }
    }
}
