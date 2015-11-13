/* Test program for ompitestWarning.
 *
 * mpijavac TestWarning3.java
 * mpiexec -np 1 java TestWarning2
 *
 * File: TestWarning3.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestWarning3
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    MPI.Finalize();
    OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Warning message after MPI.Finalize()");
  }
}
