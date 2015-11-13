/* Test program for ompitestCheckSize.
 *
 * mpijavac TestCheckSize4.java
 * mpiexec -np 1 java TestCheckSize4
 *
 * File: TestCheckSize4.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestCheckSize4
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    MPI.Finalize();
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, false);
  }
}
