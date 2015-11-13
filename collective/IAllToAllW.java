/*
 *
 * This file is a port from "alltoallw.c", with modifications
 * to test iAllToAllw instead, from the ibm regression test 
 * package found in the ompi-tests repository.  The formatting 
 * of the code is mainly the same as in the original file.
 *
 *
 * File: AllToAllW.java			Author: N. Graham
 *
 */

//package collective;

import java.nio.*;
import mpi.*;

public class IAllToAllW {
	public static void main(String[] args) throws MPIException
	{
		Comm comm;
		IntBuffer sBuf, rBuf;
		int rank, size, extent;
		int[] sendCounts, recvCounts, rDispls, sDispls;
		Datatype[] sDTypes, rDTypes;
		Request req;

		MPI.Init(args);

		comm = MPI.COMM_WORLD;

		/* Create the buffer */
		size = comm.getSize();
		rank = comm.getRank();

		sBuf = MPI.newIntBuffer(size * size);
		rBuf = MPI.newIntBuffer(size * size);

		/* Load up the buffers */
		for (int i = 0; i < (size*size); i++) {
			sBuf.put(i, (i + 100*rank));
			rBuf.put(i, -i);
		}

		/* Create and load the arguments to alltoallw */
		sendCounts = new int[size];
		recvCounts = new int[size];
		rDispls = new int[size];
		sDispls = new int[size];
		sDTypes = new Datatype[size];
		rDTypes = new Datatype[size];

		extent = 4; //MPI.INT.getExtent(); //getExtent returns 1, but a 4 is needed for these calculations

		for (int i = 0; i < size; i++) {
			sendCounts[i] = i;
			recvCounts[i] = rank;
			rDispls[i] = (i * rank * extent);
			sDispls[i] = (((i * (i+1))/2) * extent);
			sDTypes[i] = MPI.INT;
			rDTypes[i] = MPI.INT;
		}
		
		req = comm.iAllToAllw(sBuf, sendCounts, sDispls, sDTypes, rBuf, recvCounts, rDispls, rDTypes);
		req.waitFor();
		req.free();
		
		/* Check rbuf */
		for (int i = 0; i < size; i++) {
			int p = rDispls[i] / extent;
			for (int j = 0; j < rank; j++) {
				if (rBuf.get(p + j) != (i * 100 + (rank*(rank+1))/2 + j)) {
					System.out.println(i + " " + j + " " + size + " " + rank + " " + extent);
					OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
							"bad answer " + rBuf.get(p + j) + " (should be " + (i * 100 + (rank*(rank+1))/2 + j) + ")\n");
				}
			}
		}

		MPI.COMM_WORLD.barrier();
		MPI.Finalize();
		if(rank == 0) {
			System.out.println("Test completed.");
		}
	}
}