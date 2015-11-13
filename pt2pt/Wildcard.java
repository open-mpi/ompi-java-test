/* 
 *
 * This file is a port from "wildcard.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Wildcard.java			Author: S. Gross
 *
 */

import mpi.*;

public class Wildcard
{
  private final static int ITER = 10;

  public static void main (String args[]) throws MPIException
  {
    int me, tasks, tag, expected;
    int val[] = new int[1];
    Status status;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    val[0] = -1;
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    if (me == 0) {
      for (int i = 0; i < (tasks - 1) * ITER; i++) {
	status = MPI.COMM_WORLD.recv (val, 1, MPI.INT,
				      MPI.ANY_SOURCE, i / (tasks - 1));
	expected = status.getSource() * 1000 + status.getTag();
	if (val[0] != expected)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR, val = " + val[0] +
				    ", should be " + expected + "\n");
      }
    } else {
      for (int i = 0; i < ITER; i++) {
	tag = i;
	val[0] = me * 1000 + tag;
	MPI.COMM_WORLD.send (val, 1, MPI.INT, 0, tag);
      }
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
