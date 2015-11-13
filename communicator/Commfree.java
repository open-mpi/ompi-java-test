/* 
 *
 * This file is a port from "commfree.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Commfree.java			Author: S. Gross
 *
 */

import mpi.*;

public class Commfree
{
  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    Comm comm;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    for(int i = 0; i < 100; i++) {
      comm = (Comm) (MPI.COMM_WORLD.clone());
      comm.free();
      if(!comm.isNull())  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: comm not nulled\n");
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
