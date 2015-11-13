/*
 *
 * This file is a modified version of Rsend.java that has been repurposed
 * to test for the setElementsX and getElementsX methods in Status.java.
 *
 *
 * File: SetElementsX.java			Author: N. Graham
 *
 */

import mpi.*;

public class SetElementsX {

	public static void main(String[] args) throws MPIException {
		int buf[], len, me;
		Status status;
		Count numElements = new Count(10);

		buf = new int[10];
		len = buf.length; 
		MPI.Init(args);
		me = MPI.COMM_WORLD.getRank();

		/* We need at least 2 to run */

		OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				2, true);

		if(me == 0) {
			MPI.COMM_WORLD.rSend (buf, len, MPI.CHAR, 1, 1);
		} else if (me == 1) {
			status = MPI.COMM_WORLD.recv (buf, len, MPI.CHAR, 0, 1);

			status.setElementsX(MPI.INT, numElements);

			if(status.getElementsX(MPI.INT).equals(numElements)) {
				System.out.println("Test Passed");
			} else {
				System.out.println("Test Failed");
			}
		}

		MPI.COMM_WORLD.barrier ();
		MPI.Finalize();
	}

}
