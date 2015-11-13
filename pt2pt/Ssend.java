/* Function:	- tests synchonicity of MPI.Ssend between two ranks
 *
 * This file is a port from "ssend.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Ssend.java			Author: S. Gross
 *
 */

import mpi.*;

public class Ssend
{
  private final static int WAIT_SECONDS = 10;	/* # seconds wait-time */

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int rank[] = new int[1],
        junk[] = new int[1];
    double time;

    MPI.Init(args);
    rank[0] = MPI.COMM_WORLD.getRank();
    junk[0] = -1;
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    if (rank[0] == 0) {
      MPI.COMM_WORLD.recv (junk, 1, MPI.INT, 1, 0);
      MPI.COMM_WORLD.sSend (rank, 1, MPI.INT, 1, 1);
      MPI.COMM_WORLD.send (rank, 1, MPI.INT, 1, 2);
    }
    else if (rank[0] == 1) {
      MPI.COMM_WORLD.send (rank, 1, MPI.INT, 0, 0);
      time = MPI.wtime();
      Status stat = null;

      do {
	stat = MPI.COMM_WORLD.iProbe (0, 2);
	if(stat != null)
	  break;
	Thread.sleep(1000);
      } while ((MPI.wtime() - time) < WAIT_SECONDS);
      
      MPI.COMM_WORLD.recv (junk, 1, MPI.INT, 0, 1);
      MPI.COMM_WORLD.recv (junk, 1, MPI.INT, 0, 2);

      if(stat != null) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "MPI_Ssend did not synchronize: " +
				    "tag 2 received before tag 1\n");
      }
    }

    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
