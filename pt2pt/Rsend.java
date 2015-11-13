/*  Function:	- tests synchonicity of MPI.Rsend between two ranks
 *
 * This file is a port from "rsend.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Rsend.java			Author: S. Gross
 *
 */

import mpi.*;

public class Rsend
{
  public static void main (String args[]) throws MPIException
  {
    int buf[], len, me;
    Status status;
    
    buf = new int[10];
    len = buf.length; 
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    
    /* We need at least 2 to run */
    
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    
    /* Clear out so that we can be bcheck clean */
    
    for (int i = 0; i < len; ++i) {
      buf[i]  = 0;
    }

    MPI.COMM_WORLD.barrier ();
    if (me == 0) {
      for (int i = 0; i < 1000000; ++i) {
	;
      }
      MPI.COMM_WORLD.rSend (buf, len, MPI.CHAR, 1, 1);
    } else if (me == 1) {
      status = MPI.COMM_WORLD.recv (buf, len, MPI.CHAR, 0, 1);
    }
    
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
