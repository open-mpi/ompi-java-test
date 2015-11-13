/* 
 *
 * This file is a port from "bcast.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Bcast.java			Author: S. Gross
 *
 */

import mpi.*;

public class Bcast
{
  private final static int MAXLEN = 100000;

  public static void main (String args[]) throws MPIException
  {
    int root, myself, tasks;
    int out[] = new int[MAXLEN];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    root = tasks - 1;
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      if (myself == root) {
	for (int i = 0; i < j; i++) {
	  out[i] = i;
	}
      }

      MPI.COMM_WORLD.bcast(out, j, MPI.INT, root);
      
      for (int k = 0; k < j; k++) {
	if (out[k] != k) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "bad answer (" + out[k] +
				      ") at index " + k +
				      " of " + j +
				      " (should be " + k + ")\n"); 
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
