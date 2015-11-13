/* 
 *
 * This file is a port from "ibarrier.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Ibarrier.java			Author: S. Gross
 *
 */

import mpi.*;

public class Ibarrier
{
  private final static int NREQS = 100;

  public static void main (String args[]) throws MPIException
  {
    double t1, t2;
    
    MPI.Init(args);
    
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);

    /* Do one barrier */
    t1 = MPI.wtime();
    Request request = MPI.COMM_WORLD.iBarrier();
    request.waitFor();
    request.free();
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
    
    /* Do a bunch of barriers */
    Request req[] = new Request[NREQS];

    for (int i = 0; i < NREQS; ++i) {
      req[i] = MPI.COMM_WORLD.iBarrier();
    }

    Request.waitAll(req);
    
    for (int i = 0; i < NREQS; ++i) {
      req[i].free();
    }

    MPI.Finalize();
  }
}
