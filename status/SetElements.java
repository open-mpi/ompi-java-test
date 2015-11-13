/*
 *
 * This file is a modified version of Rsend.java that has been repurposed
 * to test for the setElements method in Status.java.
 *
 *
 * File: SetElements.java			Author: N. Graham
 *
 */

import mpi.*;

public class SetElements {

	public static void main(String[] args) throws MPIException {
		int buf[], len, me;
		Status status;
		int numElements = 10;

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

			status.setElements(MPI.INT, numElements);

			if(status.getElements(MPI.INT) == numElements) {
				System.out.println("Test Passed");
			} else {
				System.out.println("Test Failed");
			}
		}

		MPI.COMM_WORLD.barrier ();
		MPI.Finalize();
	}

}
