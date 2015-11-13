/* 
 *
 * This file is a port from "osu_latency.c" from the osu
 * benchmark package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OSULatency.java			Author: N. Graham
 *
 */

import java.nio.*;
import mpi.*;

public class OSULatency {
	private static final String BENCHMARK = "OSU MPI Latency Test";
	private static final int MESSAGE_ALIGNMENT = 64;
	private static final int MAX_MSG_SIZE = (1 << 22);
	private static final int MYBUFSIZE = (MAX_MSG_SIZE + MESSAGE_ALIGNMENT);
	private static final int SKIP_LARGE = 10;
	private static final int LOOP_LARGE = 100;
	private static final int LARGE_MESSAGE_SIZE = 8192;
	private static final String HEADER = "# " + BENCHMARK + "\n";
	private static final int FIELD_WIDTH = 20;
	private static final int FLOAT_PRECISION = 2;

	public static void main(String[] args) throws MPIException
	{
		CharBuffer sBufOriginal = MPI.newCharBuffer(MYBUFSIZE);
		CharBuffer rBufOriginal = MPI.newCharBuffer(MYBUFSIZE);
		int skip = 1000;
		int loop = 10000;
		int myID, numProcs, i;
		int size;
		Status reqStat;
		CharBuffer sBuf, rBuf;
		int alignSize;
		double tStart = 0.0, tEnd = 0.0;

		MPI.Init(args);
		numProcs = MPI.COMM_WORLD.getSize();
		myID = MPI.COMM_WORLD.getRank();

		if(numProcs != 2) {
			if(myID == 0) {
				System.err.printf("This test requires exactly two processes\n");
			}

			MPI.Finalize();

			System.out.println("EXIT_FAILURE");
		}

		alignSize = MESSAGE_ALIGNMENT;

		/**************Allocating Memory*********************/

		sBuf = MPI.slice(sBufOriginal, (alignSize - 1) / alignSize * alignSize);

		rBuf = MPI.slice(rBufOriginal, (alignSize - 1) / alignSize * alignSize);


		/**************Memory Allocation Done*********************/

		if(myID == 0) {
			System.out.printf(HEADER);

			System.out.printf("%-10s          %-10s\n", "# Size", "Latency (us)");
		}

		for(size = 0; size <= MAX_MSG_SIZE; size = ((size != 0) ? size * 2 : 1)) {
			/* touch the data */

			for(i = 0; i < size; i++) {
				sBuf.put(i, 'a');
				rBuf.put(i, 'b');
			}


			if(size > LARGE_MESSAGE_SIZE) {
				loop = LOOP_LARGE;
				skip = SKIP_LARGE;
			}

			MPI.COMM_WORLD.barrier();

			if(myID == 0) {
				for(i = 0; i < loop + skip; i++) {
					if(i == skip) 
						tStart = MPI.wtime();

					MPI.COMM_WORLD.send(sBuf, size, MPI.BYTE, 1, 1);

					reqStat = MPI.COMM_WORLD.recv(rBuf, size, MPI.BYTE, 1, 1);
				}
				tEnd = MPI.wtime();
			}

			else if(myID == 1) {
				for(i = 0; i < loop + skip; i++) {
					MPI.COMM_WORLD.recv(rBuf, size, MPI.BYTE, 0, 1);

					MPI.COMM_WORLD.send(sBuf, size, MPI.BYTE, 0, 1);
				}
			}

			if(myID == 0) {
				double latency = (tEnd - tStart) * 1e6 / (2.0 * loop);

				System.out.printf("%-10d          %-10.2f\n", size, latency);
			}
		}
		MPI.Finalize();

		if(myID == 0) {
			System.out.println("EXIT_SUCCESS");
		}
	}
}
/* vi: set sw=4 sts=4 tw=80: */
