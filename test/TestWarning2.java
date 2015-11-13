/* Test program for ompitestWarning.
 *
 * mpijavac TestWarning2.java
 * mpiexec -np 1 java TestWarning2
 *
 * File: TestWarning2.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestWarning2
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Warning message between MPI.Init() " +
				  "and MPI.Finalize()");
    MPI.Finalize();
  }
}
