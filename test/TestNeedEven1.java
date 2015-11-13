/* Test program for ompitestNeedEven.
 *
 * mpijavac TestNeedEven1.java
 * mpiexec -np 1 java TestNeedEven1
 *
 * File: TestNeedEven1.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestNeedEven1
{
  public static void main (String args[]) throws MPIException
  {
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());
    MPI.Init (args);
    MPI.Finalize();
  }
}
