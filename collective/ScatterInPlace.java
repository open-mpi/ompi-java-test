/* 
 *
 * This file is a port from "scatter_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ScatterInPlace.java		Author: S. Gross
 *
 */

import mpi.*;

public class ScatterInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int out[], in[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN];
    out = new int[MAXLEN * tasks];
    for(int j=1,root=0;j<=MAXLEN;j*=10,root=(root+1)%tasks)  {
      if(myself == root) {
	for(int i=0;i<j*tasks;i++) {
	  out[i] = i;
	}
	for(int i=0;i<j;i++) {
	  in[i] = i+myself*j;
	}
	
	MPI.COMM_WORLD.scatter(out, j, MPI.INT, root);
      } else {
	MPI.COMM_WORLD.scatter(in, j, MPI.INT, root);
      }
      
      for(int k=0;k<j;k++) {
	if(in[k] != k+myself*j) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "task " + myself + ": " +
				      " bad answer (" + in[k] +
				      ") at index " + k + " of " +
				      j + " (should be " +
				      (k + (myself * j)) + ")\n"); 
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
