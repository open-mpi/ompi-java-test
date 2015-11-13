/* Test program for ompitestWarning.
 *
 * mpijavac TestWarning4.java
 * mpiexec -np 1 java TestWarning4
 *
 * File: TestWarning4.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestWarning4
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init (args);
    OmpitestError.ompitestWarning(MPI.COMM_WORLD, "MPI.COMM_WORLD",
				  OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Warning message between MPI.Init() " +
				  "and MPI.Finalize()");
    MPI.Finalize();
  }
}
