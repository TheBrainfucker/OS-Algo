import java.util.*;
import java.io.File;

public class RoundRobin {
	// find average
	public static double avg(int a[][], int n) {
		// sum of array element
		double sum = 0;

		for (int i = 0; i < a.length; i++) {
			sum += a[i][n];
		}
		return sum / a.length;
	}

	public static void roundRobin(int[][] process, int quantum) {
		// [process, arrival, burst, remaining, start, finish, response, turnaround,
		// waiting]

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

		// waiting queue
		Queue<int[]> q_wait = new LinkedList<>();

		int[] temp_proc = q_arrival.poll();
		q_wait.add(temp_proc);

		// run as long as both arrival & waiting queues are not empty
		while (q_arrival.size() != 0 || q_wait.size() != 0) {
			if (q_wait.size() != 0) {
				// dequeue process from waiting queue
				temp_proc = q_wait.poll();
				if (temp_proc[2] == temp_proc[3]) { // burst=remaining means 1st time for the process
					temp_proc[4] = time_elapsed; // set starting time
				}

				// when remaining time < quantum, process finishes here
				if (temp_proc[3] < quantum) {
					time_elapsed += temp_proc[3]; // increase elapsed time according to remaining time
					temp_proc[3] = 0; // remaining time of process becomes zero
					temp_proc[5] = time_elapsed; // set finish time to the process
					temp_proc[6] = temp_proc[4] - temp_proc[1]; // response = start - arrival
					temp_proc[7] = temp_proc[5] - temp_proc[1]; // turnaround = finish - arrival
					temp_proc[8] = temp_proc[7] - temp_proc[2]; // waiting = turnaround - burst

					// add other process to waiting queue whose arrival times are less or equal than
					// the time
					// elapsed
					while (q_arrival.size() != 0 && q_arrival.peek()[1] <= time_elapsed) {
						q_wait.add(q_arrival.poll());
					}
				}

				// when remaining time == quantum, process finishes here
				else if (temp_proc[3] == quantum) {
					time_elapsed += temp_proc[3]; // increase elapsed time according to quantum
					temp_proc[3] = 0; // remaining time of process becomes zero
					temp_proc[5] = time_elapsed; // set finish time to the process
					temp_proc[6] = temp_proc[4] - temp_proc[1]; // response = start - arrival
					temp_proc[7] = temp_proc[5] - temp_proc[1]; // turnaround = finish - arrival
					temp_proc[8] = temp_proc[7] - temp_proc[2]; // waiting = turnaround - burst

					// add other process to waiting queue whose arrival times are less or equal than
					// the time
					// elapsed
					while (q_arrival.size() != 0 && q_arrival.peek()[1] <= time_elapsed) {
						q_wait.add(q_arrival.poll());
					}
				}

				// when remaining time > quantum, process is unable to finish here
				else {
					time_elapsed += quantum; // increase elapsed time according to quantum
					temp_proc[3] -= quantum; // decrease remaing time of process

					while (true) {
						// add other process to waiting queue whose arrival times are less than the time
						// elapsed
						if (q_arrival.size() != 0 && (q_arrival.peek())[1] < time_elapsed) {
							q_wait.add(q_arrival.poll());
						} else
							break;
					}

					// add other process to waiting queue whose arrival times are equal to the time
					// elapsed, but processes with lower process number gets priority while multiple
					// process are needed to be added to waiting queue at the same time
					if (q_arrival.size() != 0 && (q_arrival.peek())[1] == time_elapsed) {
						for (int i = 0; i < process.length; i++) {
							if (process[i][1] == time_elapsed || temp_proc[0] == i) {
								q_wait.add(process[i]);
							}
						}
					} else {// if none of the above cases are true then simply add the present process to
							// waiting queue
						q_wait.add(temp_proc);
					}
				}
			}
			// when waiting queue is empty but other processes are yet to be arrived
			else if (q_arrival.size() != 0 || q_wait.size() == 0) {
				time_elapsed = (q_arrival.peek())[1];// start agian from new process's arriving time
				q_wait.add(q_arrival.poll());
			}
		}

		// sort by process number to print out
		Arrays.sort(proc, Comparator.comparingInt(a -> a[0]));

		// print out
		System.out.println();
		System.out.println(String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s", "Processes", "Arrival",
				"Burst", "Start", "Finish", "Response", "Turnaround", "Waiting"));
		for (int i = 0; i < proc.length; i++) {
			System.out.println(String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s", (i + 1), proc[i][1],
					proc[i][2], proc[i][4], proc[i][5], proc[i][6], proc[i][7], proc[i][8]));
		}

		System.out.println();
		System.out.println("Avg turnaround time = " + avg(proc, 7)); // 7 is the index of turnaround in proc[] array
		System.out.println("Avg waiting time = " + avg(proc, 8)); // 8 is the index of waiting in proc[] array
		System.out.println();
	}

	public static void main(String[] agrs) {
		Scanner sc = new Scanner(System.in);
		System.out.println();
		System.out.println("Type 0 to input from .txt file || Type 1 to input manually.");
		int select = sc.nextInt();
		if (select == 0) {
			try {// read from RoundRobinInput.txt file
					// 1st line contains total processes number
					// 2nd line is time quantum
					// 3rd line: arrival time followed by a space and burst time
					// CAREFUL WITH .txt FILE PATH
				File fileName = new File("Round Robin/RoundRobinInput.txt");
				// print out fileName.getAbsolutePath() if you're confused
				// System.out.println(fileName.getAbsolutePath());
				Scanner scan = new Scanner(fileName);
				String firstLine = scan.nextLine();
				String secondtLine = scan.nextLine();
				int total_process = Integer.parseInt(firstLine);
				int quantum = Integer.parseInt(secondtLine);

				int[][] process = new int[total_process][9];

				for (int i = 0; i < total_process; i++) {
					String line = scan.nextLine();
					try {
						String[] stringArray = line.split(" ", 2);// splitting by space

						process[i][0] = i;// setting process number
						process[i][1] = Integer.parseInt(stringArray[0]); // arrival
						process[i][2] = Integer.parseInt(stringArray[1]); // burst
						process[i][3] = process[i][2]; // setting remaining time = burst time
					} catch (NumberFormatException e) {
						e.getStackTrace();
					}
				}
				scan.close();
				System.out.println();
				System.out.println("ROUND ROBIN ALGO");
				roundRobin(process, quantum); // calling roundRobin method
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else if (select == 1) {
			System.out.println();
			System.out.print("ROUND ROBIN ALGO\nHow many processes: ");
			int total_process = sc.nextInt();

			int[][] process = new int[total_process][9];

			for (int i = 0; i < total_process; i++) {
				System.out.print("What's the arrival and burst time of porcess " + (i + 1) + ": ");
				process[i][0] = i;// setting process number
				process[i][1] = sc.nextInt(); // arrival
				process[i][2] = sc.nextInt(); // burst
				process[i][3] = process[i][2]; // setting remaining time = burst time
			}
			System.out.print("What's the time quantum: ");
			int quantum = sc.nextInt();
			sc.close();
			roundRobin(process, quantum); // calling roundRobin method
		} else {
			System.out.println("-_-");
		}
	}
}
