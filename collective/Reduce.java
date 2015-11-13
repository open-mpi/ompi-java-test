/* 
 *
 * This file is a port from "reduce.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Reduce.java			Author: S. Gross
 *
 */

import mpi.*;

public class Reduce
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int root,myself,tasks;
    int out[] = new int[MAXLEN],
	in[] = new int[MAXLEN];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    root = tasks/2;
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int i = 0; i < j; i++) {
	out[i] = i;
      }
      
      MPI.COMM_WORLD.reduce(out, in, j, MPI.INT, MPI.SUM, root);
      
      if (myself == root)  {
	for (int k = 0; k < j; k++) {
	  if (in[k] != k * tasks) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in[k] +
					") at index " + k + " of " + j +
					" (should be " + (k * tasks) +
					")\n"); 
	    break;
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
