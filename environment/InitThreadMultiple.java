/* 
 *
 * This file is a port from "init_thread_multiple.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: InitThreadMultiple.java	Author: S. Gross
 *
 */

import mpi.*;

public class InitThreadMultiple
{
  public static void main (String args[]) throws MPIException
  {
    if (OmpitestConfig.OMPITEST_HAVE_MPI_THREADS == 0) {
      int rank;

      MPI.Init(args);

      rank = MPI.COMM_WORLD.getRank();
      if (0 == rank) {
        System.out.printf("Skipping test because this test was " +
			  "compiled without MPI thread support\n");
      }
      MPI.Finalize();
      System.exit(77);
    } else {
      int rank, err;

      int provided = MPI.InitThread(args, MPI.THREAD_MULTIPLE);

      if (provided < MPI.THREAD_SINGLE || 
	  provided > MPI.THREAD_MULTIPLE)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: MPI_Init_thread returned " +
				    "an illegal value\n");
      rank = MPI.COMM_WORLD.getRank();
      if (0 == rank) {
	System.out.printf("PASS: MPI_Init_thread with " +
			  "MPI_THREAD_MULTIPLE returned " +
			  "MPI_THREAD_%s\n",
	  (provided == MPI.THREAD_SINGLE) ? "SINGLE" :
          (provided == MPI.THREAD_FUNNELED) ? "FUNNELED" :
          (provided == MPI.THREAD_SERIALIZED) ? "SERIALIZED" : "MULTIPLE");
      }
      MPI.Finalize();
      System.exit(0);
    }
  }
}
