/* 
 *
 * This file is a port from "osu_bibw.c" from the osu
 * benchmark package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OSUBIBW.java			Author: N. Graham
 *
 */

import mpi.*;
import java.nio.*;

public class OSUBIBW {

	private static String BENCHMARK = "OSU MPI Bi-Directional Bandwidth Test";
	private static int MAX_REQ_NUM = 1000;
	private static int MAX_ALIGNMENT = 65536;
	private static int MAX_MSG_SIZE = (1<<22);
	private static int MYBUFSIZE = (MAX_MSG_SIZE + MAX_ALIGNMENT);
	private static int LOOP_LARGE = 20;
	private static int WINDOW_SIZE_LARGE = 64;
	private static int SKIP_LARGE = 2;
	private static int LARGE_MESSAGE_SIZE = 8192;
	private static CharBuffer sBuf = MPI.newCharBuffer(MYBUFSIZE);
	private static CharBuffer rBuf = MPI.newCharBuffer(MYBUFSIZE);
	private static Request[] sendRequest = new Request[WINDOW_SIZE_LARGE];
	private static Request[] recvRequest = new Request[WINDOW_SIZE_LARGE];
	private static Status[] reqstat = new Status[MAX_REQ_NUM];
	private static String HEADER = "# " + BENCHMARK + "\n";
	private static int FIELD_WIDTH = 20;
	private static int FLOAT_PRECISION = 2;

	public static void main(String[] args) throws MPIException {
		int myID, numProcs;
		int size;
		double tStart = 0.0, tEnd = 0.0, t = 0.0;
		int loop = 100;
		int window_size = 64;
		int skip = 10;

		MPI.Init(args);
		numProcs = MPI.COMM_WORLD.getSize();
		myID = MPI.COMM_WORLD.getRank();

		if(numProcs != 2) {
			if(myID == 0) {
				System.err.printf("This test requires exactly two processes\n");
			}

			MPI.Finalize();
			System.exit(-1);
		}

		if(myID == 0) {
			System.out.printf(HEADER);

			System.out.printf("%-20s%-20s\n", "# Size", "Bi-Bandwidth (MB/s)");
		}

		for(size = 1; size <= MAX_MSG_SIZE; size *= 2) {
			/* touch the data */

			for(int i = 0; i < size; i++) {
				sBuf.put(i, 'a');
				rBuf.put(i, 'b');
			}


			if(size > LARGE_MESSAGE_SIZE) {
				loop = LOOP_LARGE;
				skip = SKIP_LARGE;
				window_size = WINDOW_SIZE_LARGE;
			}
			if(myID == 0) {
				for(int i = 0; i < loop + skip; i++) {
					if(i == skip) {
						tStart = MPI.wtime();
					}

					for(int j = 0; j < window_size; j++) {
						recvRequest[j] = MPI.COMM_WORLD.iRecv(rBuf, size, MPI.BYTE, 1, 10);
					}
					
					for(int j = 0; j < window_size; j++) {
						sendRequest[j] = MPI.COMM_WORLD.iSend(sBuf, size, MPI.BYTE, 1, 100);
					}
					reqstat = Request.waitAllStatus(sendRequest);
					reqstat = Request.waitAllStatus(recvRequest);
				}

				tEnd = MPI.wtime();
				t = tEnd - tStart;

			}

			else if(myID == 1) {
				for(int i = 0; i < loop + skip; i++) {
					for(int j = 0; j < window_size; j++) {
						recvRequest[j] = MPI.COMM_WORLD.iRecv(rBuf, size, MPI.BYTE, 0, 100);
					}

					for (int j = 0; j < window_size; j++) {
						sendRequest[j] = MPI.COMM_WORLD.iSend(sBuf, size, MPI.BYTE, 0, 10);
					}

					reqstat = Request.waitAllStatus(sendRequest);
					reqstat = Request.waitAllStatus(recvRequest);
				}
			}

			if(myID == 0) {
				double tmp = size / 1e6 * loop * window_size * 2;

				System.out.printf("%-10d%20.2f\n", size, tmp / t);
			}
		}

		MPI.Finalize();
	}

	/* vi: set sw=4 sts=4 tw=80: */

}
