/* 
 *
 * This file is a port from "abort.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Abort.java			Author: S. Gross
 *
 */

import mpi.*;

public class Abort
{
  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int me, tasks;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    if (me == 0) {
      System.out.printf("****************************************" +
			"**********************************\n" +
			"This program tests MPI_ABORT and generates " +
			"error messages\n" +
			"ERRORS ARE EXPECTED AND NORMAL IN THIS " +
			"PROGRAM!!\n" +
			"****************************************" +
			"**********************************\n");
      MPI.COMM_WORLD.abort(MPI.ERR_TYPE); 
    }
    
    /* Avoid a compiler warning... */
    
    me = -1;
    while (me == -1)
      Thread.sleep(1);
  }
}
