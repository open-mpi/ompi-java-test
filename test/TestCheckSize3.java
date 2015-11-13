/* Test program for ompitestCheckSize.
 *
 * mpijavac TestCheckSize3.java
 * mpiexec -np 1 java TestCheckSize3
 * mpiexec -np 2 java TestCheckSize3
 *
 * File: TestCheckSize3.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestCheckSize3
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, false);
    MPI.Finalize();
  }
}
