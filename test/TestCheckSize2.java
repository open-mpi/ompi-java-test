/* Test program for ompitestCheckSize.
 *
 * mpijavac TestCheckSize2.java
 * mpiexec -np 1 java TestCheckSize2
 * mpiexec -np 2 java TestCheckSize2
 *
 * File: TestCheckSize2.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestCheckSize2
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    MPI.Finalize();
  }
}
