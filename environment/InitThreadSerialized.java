/* 
 *
 * This file is a port from "init_thread_serialized.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: InitThreadSerialized.java	Author: S. Gross
 *
 */

import mpi.*;

public class InitThreadSerialized
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
      int provided = MPI.InitThread(args, MPI.THREAD_SERIALIZED);
 
      if (provided < MPI.THREAD_SERIALIZED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: MPI_Init_thread returned " +
				    "less than MPI_THREAD_SERIALIZED\n");
      MPI.Finalize();
      System.exit(0);
    }
  }
}
