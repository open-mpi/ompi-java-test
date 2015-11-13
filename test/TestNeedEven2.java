/* Test program for ompitestNeedEven.
 *
 * mpijavac TestNeedEven2.java
 * mpiexec -np 1 java TestNeedEven2
 * mpiexec -np 2 java TestNeedEven2
 *
 * File: TestNeedEven2.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestNeedEven2
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());
    MPI.Finalize();
  }
}
