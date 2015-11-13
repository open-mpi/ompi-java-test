/* Test program for ompitestCheckSize.
 *
 * mpijavac TestCheckSize1.java
 * mpiexec -np 1 java TestCheckSize1
 *
 * File: TestCheckSize1.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestCheckSize1
{
  public static void main (String args[]) throws MPIException
  {
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    MPI.Init (args);
    MPI.Finalize();
  }
}
