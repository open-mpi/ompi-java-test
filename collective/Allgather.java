/* 
 *
 * This file is a port from "allgather.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Allgather.java			Author: S. Gross
 *
 */

import mpi.*;

public class Allgather
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself, tasks;
    int out[], in[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN * tasks];
    out = new int[MAXLEN];
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int i = 0; i < j; i++) {
	out[i] = myself;
      }
      MPI.COMM_WORLD.allGather(out, j, MPI.INT, in, j, MPI.INT);
      
      for (int i = 0; i < tasks; i++)  {
	for (int k = 0; k < j; k++) {
	  if (in[k + i * j] != i) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"bad answer (" + in[k] +
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
