/* Test program for ompitestWarning.
 *
 * mpijavac TestWarning1.java
 * mpiexec -np 1 java TestWarning1
 *
 * File: TestWarning1.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestWarning1
{
  public static void main (String args[]) throws MPIException
  {
    OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Warning message before MPI.Init()");
    MPI.Init (args);
    MPI.Finalize();
  }
}
