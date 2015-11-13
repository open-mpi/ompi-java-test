/* 
 *
 * This file is a port from "alltoall.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Alltoall.java			Author: S. Gross
 *
 */

import mpi.*;

public class Alltoall
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int out[], in[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN * tasks];
    out = new int[MAXLEN * tasks];
    for (int i = 0; i < MAXLEN * tasks; ++i) {
      out[i] = myself;
    }
    for (int j = 1; j <= MAXLEN; j *= 10) {
      
      MPI.COMM_WORLD.allToAll(out, j, MPI.INT, in, j, MPI.INT);
      
      for (int i = 0; i < tasks; ++i)  {
	for (int k = 0; k < j; ++k) {
	  if (in[k + i * j] != i) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in[k + i * j] +
					") at index " + (k + i * j) +
					" of " + (j * tasks) +
					" (should be " + i + ")\n"); 
	    break; 
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
