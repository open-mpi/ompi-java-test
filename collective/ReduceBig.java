/* 
 *
 * This file is a port from "reduce_big.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ReduceBig.java			Author: S. Gross
 *
 */

import mpi.*;

public class ReduceBig
{
  private final static int MAXLEN = 10000;
  private final static int MAXITERS = 10;

  public static void main (String args[]) throws MPIException
  {
    int count = 0, max;
    int myself,tasks;
    int out[] = new int[MAXLEN],
	in[] = new int[MAXLEN];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    max = 0;
    for (int root = 1; root < tasks; ++root) {
      for (int j = 1; j <= MAXLEN; j *= 10)  {
	max += MAXITERS;
      }
    }
    
    for (int root = 1; root < tasks; ++root) {
      for (int j = 1; j <= MAXLEN; j *= 10)  {
	for (int iter = 0; iter < MAXITERS; ++iter) {
	  for (int i = 0; i < j; i++) {
	    out[i] = i;
	  }
	  for (int i = 0; i < MAXLEN; i++) {
	    in[i] = 0;
	  }
	  
	  MPI.COMM_WORLD.reduce(out, in, j, MPI.INT, MPI.SUM, root);
	  
	  ++count;
	  if (myself == root)  {
	    for (int k = 0; k < j; k++) {
	      if (in[k] != k * tasks) {  
		OmpitestError.ompitestError(OmpitestError.getFileName(),
					    OmpitestError.getLineNumber(),
					    "*** FAIL test $" + count +
					    " of " + max +
					    ": bad answer (" + in[k] +
					    ") at index " + k + " of " +
					    j + " (should be " +
					    (k * tasks) + ")\n"); 
		break;
	      }
	    }
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
