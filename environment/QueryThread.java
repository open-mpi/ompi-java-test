/* 
 *
 * This file is a port from "query_thread.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: QueryThread.java		Author: S. Gross
 *
 */

import mpi.*;

public class QueryThread
{
  public static void main (String args[]) throws MPIException
  {
    int provided = MPI.InitThread(args, MPI.THREAD_SINGLE);

    if (provided < MPI.THREAD_SINGLE)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Init_thread returned " +
				  "less than MPI_THREAD_SINGLE\n");

    if (provided != MPI.queryThread())
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Query_thread returned " +
				  "a different value than " +
				  "MPI_Init_thread\n");
    MPI.Finalize();
  }
}
