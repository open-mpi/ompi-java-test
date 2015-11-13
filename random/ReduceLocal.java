/* 
 *
 * This file is a port from reduce_local".c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ReduceLocal.java		Author: S. Gross
 *
 */

import mpi.*;

public class ReduceLocal
{
  public static void main (String args[]) throws MPIException
  {
    int src[] = new int [] { 1, 2, 3 };
    int dest[] = new int [] { 0, 0, 0 };

    MPI.Init(args);
    
    MPI.COMM_WORLD.reduceLocal(src, dest, src.length, MPI.INT, MPI.SUM);
    for (int i = 0; i < src.length; ++i) {
      if (dest[i] != src[i]) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "REDUCE_LOCAL MPI_SUM failed; " +
				    "dest[" + i + "] = " + dest[i] +
				    ", expected " + src[i] + "\n");
      }
    }
    
    MPI.Finalize();
  }
}
