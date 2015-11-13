/* Test program for ompitestProgress.
 *
 * mpijavac TestProgress.java
 * mpiexec -np 1 java TestProgress
 *
 * File: TestProgress.java		Author: S. Gross
 *
 */

import mpi.*;

public class TestProgress
{
  final static int TOTAL = 40;		/* total number of steps	*/
  final static int SLEEP = 200;		/* 200 ms			*/

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    MPI.Init (args);
    OmpitestProgress.ompitestProgressStart(TOTAL);
    for (int i = 0; i < TOTAL; ++i) {
      OmpitestProgress.ompitestProgress(i);
      Thread.sleep(SLEEP);
    }
    OmpitestProgress.ompitestProgressEnd();
    MPI.Finalize();
  }
}
