/* Test program for ompitestError.
 *
 * mpijavac TestError2.java
 * mpiexec -np 1 java TestError2
 *
 * File: TestError2.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestError2
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestError(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				"Error message between MPI.Init() " +
				"and MPI.Finalize()");
    MPI.Finalize();
  }
}
