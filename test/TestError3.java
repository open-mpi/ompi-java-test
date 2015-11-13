/* Test program for ompitestError.
 *
 * mpijavac TestError3.java
 * mpiexec -np 1 java TestError2
 *
 * File: TestError3.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestError3
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    MPI.Finalize();
    OmpitestError.ompitestError(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				"Error message after MPI.Finalize()");
  }
}
