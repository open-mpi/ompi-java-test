/* 
 *
 * This file is a port from "create_group.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CommCreateGroup.java			Author: N. Graham
 *
 */

import mpi.*;
import java.nio.*;

public class CommCreateGroup {

	public static void main(String[] args) throws MPIException
	{
		int size, rank, i;
		int[] excl;
		Group worldGroup, evenGroup;
		Comm evenComm;

		MPI.Init(args);

		size = MPI.COMM_WORLD.getSize();
		rank = MPI.COMM_WORLD.getRank();

		if (size % 2 == 1) {
			System.err.printf("this program requires a multiple of 2 number of processes\n");
			MPI.COMM_WORLD.abort(1);
		}

		excl = new int[size / 2];
		assert(excl != null);

		/* exclude the odd ranks */
		for (i = 0; i < size / 2; i++)
			excl[i] = (2 * i) + 1;

		/* Create some groups */
		worldGroup = MPI.COMM_WORLD.getGroup();
		evenGroup = worldGroup.excl(excl);
		worldGroup.free();

		if (rank % 2 == 0) {
			/* Even processes create a group for themselves */
			evenComm = MPI.COMM_WORLD.createGroup(evenGroup, 0);
			evenComm.barrier();
			evenComm.free();
		}

		evenGroup.free();
		MPI.COMM_WORLD.barrier();

		if (rank == 0)
			System.out.printf(" No errors\n");

		MPI.Finalize();
	}

}