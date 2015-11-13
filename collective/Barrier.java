/* 
 *
 * This file is a port from "barrier.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Barrier.java			Author: S. Gross
 *
 */

import mpi.*;

public class Barrier
{
  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int me, tasks;
    double t1, t2;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    
    Thread.sleep(1000);
    
    t1 = MPI.wtime();
    MPI.COMM_WORLD.barrier();
    t2 = MPI.wtime();
    
    if (t2 < t1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Wtime reports that we got " +
				  "out of the barrier before we " +
				  "got in!\n" +
				  "We entered the barrier at: " +
				  t1 + "\n" +
				  "We exited the barrier at:  " +
				  t2 + "\n");
    }
    
    MPI.Finalize();
  }
}
