/* 
 *
 * This file is a port from "gather_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: GatherInPlace.java			Author: S. Gross
 *
 */

import mpi.*;

public class GatherInPlace
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
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int root = 0 ; root < tasks; ++root) {
	for (int i = 0; i < j; i++) {
	  out[i] = i;
	  in[i] = -1;
	}
	
	if (myself == root) {
	  for (int i = 0; i < j; i++) {
	    in[myself * j + i] = i;
	  }
	  MPI.COMM_WORLD.gather(in, j, MPI.INT, root);
	  
	  for (int i = 0; i < tasks; i++)  {
	    for (int k = 0; k < j; k++) {
	      if (in[i * j + k] != k) {  
		OmpitestError.ompitestError(OmpitestError.getFileName(),
					    OmpitestError.getLineNumber(),
					    " bad answer (" +
					    in[i * j + k] +
					    ") at index " + (i * j + k) +
					    " of " + (j * tasks) +
					    " (should be " + k + ")\n"); 
		break; 
	      }
	    }
	  }
	} else {
	  MPI.COMM_WORLD.gather(out, j, MPI.INT, root);
	}
      }
    }
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
