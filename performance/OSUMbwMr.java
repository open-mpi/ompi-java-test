/* 
 *
 * This file is a port from "osu_mbw_mr.c" from the osu
 * benchmark package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OSUMbwMr.java			Author: N. Graham
 *
 */

import mpi.*;
import java.nio.*;

public class OSUMbwMr {
	private static String BENCHMARK = "OSU MPI Multiple Bandwidth / Message Rate Test";
	private static int DEFAULT_WINDOW = 64;
	private static int ITERS_SMALL = 100;
	private static int WARMUP_ITERS_SMALL = 10;
	private static int ITERS_LARGE = 20;
	private static int WARMUP_ITERS_LARGE = 2;
	private static int LARGE_THRESHOLD = 8192;
	private static int[] windowSizeS = {8, 16, 32, 64, 128};
	private static int windowSizeS_COUNT = 5;
	private static int MAX_MSG_SIZE = (1<<22);
	private static int MAX_ALIGNMENT = 65536;
	private static int MY_BUF_SIZE = (MAX_MSG_SIZE + MAX_ALIGNMENT);
	private static CharBuffer sBuf = MPI.newCharBuffer(MY_BUF_SIZE);
	private static CharBuffer rBuf = MPI.newCharBuffer(MY_BUF_SIZE);
	private static Request[] request;
	private static Status[] reqStat;
	private static String HEADER = "# " + BENCHMARK + "\n";

	public static void main(String[] args) throws MPIException
	{
		int numprocs, rank;
		int pairs, printRate;
		int windowSize, window_varied;
		int currSize;

		MPI.Init(args);
		
		numprocs = MPI.COMM_WORLD.getSize();
		rank = MPI.COMM_WORLD.getRank();
		
		/* default values */
		pairs            = numprocs / 2;
		windowSize      = DEFAULT_WINDOW;
		window_varied    = 0;
		printRate       = 1;

		for(int i = 0; i < args.length; i++) {
		//while((c = getopt(argc, argv, "p:w:r:vh")) != -1) {
			String c = args[i];
			
			switch (c) {
			case "p":
				pairs = Integer.parseInt(args[i].split("=")[1]);

				if(pairs > (numprocs / 2)) {
					if(0 == rank) {
						usage();
					}
					MPI.Finalize();
					System.exit(0);
				}
				break;

			case "w":
				windowSize = Integer.parseInt(args[i].split("=")[1]);
				break;

			case "v":
				window_varied = 1;
				break;

			case "r":
				printRate = Integer.parseInt(args[i].split("=")[1]);

				if(0 != printRate && 1 != printRate) {
					if(0 == rank) {
						usage();
					}
					MPI.Finalize();
					System.exit(0);
				}
				break;

			default:
				if(0 == rank) {
					usage();
				}
				MPI.Finalize();
				System.exit(0);
			}
		}

		if(numprocs < 2) {
			if(rank == 0) {
				System.err.printf("This test requires at least two processes\n");
			}

			MPI.Finalize();

			System.err.println("Failure");
		}

		if(rank == 0) {
			System.out.printf(HEADER);

			if(window_varied != 0) {
				System.out.printf("# [ pairs: %d ] [ window size: varied ]\n", pairs);
				System.out.printf("\n# Uni-directional Bandwidth (MB/sec)\n");
			}

			else {
				System.out.printf("# [ pairs: %d ] [ window size: %d ]\n", pairs, windowSize);

				if(printRate != 0) {
					System.out.printf("%-26s          %-14s          %-20s\n", "# Size", "MB/s", "Messages/s");
				}

				else {
					System.out.printf("%-26s          %-14s\n", "# Size", "MB/s");
				}
			}
		}

		/* More than one window size */

		if(window_varied != 0) {
			int window_array[] = windowSizeS;
			double[][] bandwidthResults;
			int logVal = 1, tmpMessageSize = MAX_MSG_SIZE;

			for(int i = 0; i < windowSizeS_COUNT; i++) {
				if(window_array[i] > windowSize) {
					windowSize = window_array[i];
				}
			}

			request = new Request[windowSize];
			reqStat = new Status[windowSize];

			while((tmpMessageSize >>= 1) != 0) {
				logVal++;
			}

			bandwidthResults = new double[logVal][];

			for(int i = 0; i < logVal; i++) {
				bandwidthResults[i] = new double[windowSizeS_COUNT];
			}

			if(rank == 0) {
				System.out.printf("#      ");

				for(int i = 0; i < windowSizeS_COUNT; i++) {
					System.out.printf("  %10d", window_array[i]);
				}

				System.out.printf("\n");
			}
			
			currSize = 1;
			for(int j = 0; currSize <= MAX_MSG_SIZE; currSize *= 2, j++) {
				if(rank == 0) {
					System.out.printf("%-7d", currSize);
				}

				for(int i = 0; i < windowSizeS_COUNT; i++) {
					bandwidthResults[j][i] = calcBw(rank, currSize, pairs,
							window_array[i], sBuf, rBuf);

					if(rank == 0) {
						System.out.printf("  %10.2f", bandwidthResults[j][i]);
					}
				}

				if(rank == 0) {
					System.out.printf("\n");
				}
			}

			if(rank == 0 && printRate != 0) {
				System.out.printf("\n# Message Rate Profile\n");
				System.out.printf("#      ");

				for(int i = 0; i < windowSizeS_COUNT; i++) {
					System.out.printf("  %10d", window_array[i]);
				}       

				System.out.printf("\n");
				
				currSize = 1;
				for(int c = 0; currSize <= MAX_MSG_SIZE; currSize *= 2) { 
					System.out.printf("%-7d", currSize); 

					for(int i = 0; i < windowSizeS_COUNT; i++) {
						double rate = 1e6 * bandwidthResults[c][i] / currSize;

						System.out.printf("  %10.2f", rate);
					}       

					System.out.printf("\n");
					c++;    
				}
			}
		}

		else {
			/* Just one window size */
			request = new Request[windowSize];
			reqStat = new Status[windowSize];

			for(currSize = 1; currSize <= MAX_MSG_SIZE; currSize *= 2) {
				double bw, rate;

				bw = calcBw(rank, currSize, pairs, windowSize, sBuf, rBuf);

				if(rank == 0) {
					rate = 1e6 * bw / currSize;

					if(printRate != 0) {
						System.out.printf("%-10d          %20.2f          %20.2f\n", currSize, bw, rate);
					}

					else {
						System.out.printf("%-10d          %20.2f\n", currSize, bw);
					}
				} 
			}
		}

		MPI.Finalize();
	}

	private static void usage() {
		System.out.printf("Options:\n");
		System.out.printf("  -r=<0,1>         Print uni-directional message rate (default 1)\n");
		System.out.printf("  -p=<pairs>       Number of pairs involved (default np / 2)\n");
		System.out.printf("  -w=<window>      Number of messages sent before acknowledgement (64, 10)\n");
		System.out.printf("                   [cannot be used with -v]\n");
		System.out.printf("  -v               Vary the window size (default no)\n");
		System.out.printf("                   [cannot be used with -w]\n");
		System.out.printf("  -h               Print this help\n");
		System.out.printf("\n");
		System.out.printf("  Note: This benchmark relies on block ordering of the ranks.  Please see\n");
		System.out.printf("        the README for more information.\n");
	}

	private static double calcBw(int rank, int size, int num_pairs, int windowSize, CharBuffer sBuf, CharBuffer rBuf) throws MPIException
	{
		double t_start = 0, t_end = 0, bw = 0;
		double[] t = new double[1];
		double[] maxTime = new double[1];
		int target;
		int loop, skip;
		int mult = (DEFAULT_WINDOW / windowSize) > 0 ? (DEFAULT_WINDOW /
				windowSize) : 1;

		for(int i = 0; i < size; i++) {
			sBuf.put(i, 'a');
			rBuf.put(i, 'b');
		}

		if(size > LARGE_THRESHOLD) {
			loop = ITERS_LARGE * mult;
			skip = WARMUP_ITERS_LARGE * mult;
		}

		else {
			loop = ITERS_SMALL * mult;
			skip = WARMUP_ITERS_SMALL * mult;
		}

		MPI.COMM_WORLD.barrier();

		if(rank < num_pairs) {
			target = rank + num_pairs;

			for(int i = 0; i < loop + skip; i++) {
				if(i == skip) {
					MPI.COMM_WORLD.barrier();
					t_start = MPI.wtime();
				}

				for(int j = 0; j < windowSize; j++) {
					request[j] = MPI.COMM_WORLD.iSend(sBuf, size, MPI.BYTE, target, 100);
				}
				
				reqStat = Request.waitAllStatus(request);
				reqStat[0] = MPI.COMM_WORLD.recv(rBuf, 4, MPI.BYTE, target, 101);
			}

			t_end = MPI.wtime();
			t[0] = t_end - t_start;
		}

		else if(rank < num_pairs * 2) {
			target = rank - num_pairs;

			for(int i = 0; i < loop + skip; i++) {
				if(i == skip) {
					MPI.COMM_WORLD.barrier();
				}

				for(int j = 0; j < windowSize; j++) {
					request[j] = MPI.COMM_WORLD.iRecv(rBuf, size, MPI.BYTE, target, 100);
				}

				reqStat = Request.waitAllStatus(request);
				MPI.COMM_WORLD.send(sBuf,  4,  MPI.BYTE, target, 101);
			}
		}

		else {
			MPI.COMM_WORLD.barrier();
		}

		MPI.COMM_WORLD.reduce(t, maxTime, 1, MPI.DOUBLE, MPI.MAX, 0);

		if(rank == 0) {
			double tmp = num_pairs * size / 1e6;

			tmp = tmp * loop * windowSize;
			bw = tmp / maxTime[0];

			return bw;
		}

		return 0;
	}
}
/* vi: set sw=4 sts=4 tw=80: */
