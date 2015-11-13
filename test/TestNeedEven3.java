/* Test program for ompitestNeedEven.
 *
 * mpijavac TestNeedEven3.java
 * mpiexec -np 1 java TestNeedEven3
 *
 * File: TestNeedEven3.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestNeedEven3
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());
    MPI.Finalize();
  }
}
