/* 
 *
 * This file is a port from "init_thread.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: InitThread.java		Author: S. Gross
 *
 */

import mpi.*;

public class InitThread
{
  public static void main (String args[]) throws MPIException
  {
    int provided = MPI.InitThread(args, MPI.THREAD_SINGLE);

    if (provided < MPI.THREAD_SINGLE)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Init_thread returned " +
				  "less than MPI_THREAD_SINGLE\n");
    MPI.Finalize();
  }
}
