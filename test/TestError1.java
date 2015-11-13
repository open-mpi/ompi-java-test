/* Test program for ompitestError.
 *
 * mpijavac TestError1.java
 * mpiexec -np 1 java TestError1
 *
 * File: TestError1.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestError1
{
  public static void main (String args[]) throws MPIException
  {
    OmpitestError.ompitestError(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				"Error message before MPI.Init()");
    MPI.Init (args);
    MPI.Finalize();
  }
}
