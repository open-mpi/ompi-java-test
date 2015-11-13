/* Test program for ompitestError.
 *
 * mpijavac TestError4.java
 * mpiexec -np 1 java TestError4
 *
 * File: TestError4.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestError4
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestError(MPI.COMM_WORLD, "MPI.COMM_WORLD",
				OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				"Error message between MPI.Init() " +
				"and MPI.Finalize()");
    MPI.Finalize();
  }
}
