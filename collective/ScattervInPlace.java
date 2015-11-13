/* 
 *
 * This file is a port from "scatterv_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ScattervInPlace.java		Author: S. Gross
 *
 */

import mpi.*;

public class ScattervInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int out[], in[];
    int displs[] = new int[128],
	rcounts[] = new int[128];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN];
    out = new int[MAXLEN * tasks];
    for(int j=1,root=0;j<=MAXLEN;j*=10,root=(root+1)%tasks)  {
      for(int i=0;i<j*tasks;i++) {
	out[i] = i;
      }
      for(int i = 0; i < tasks; i++) {
	rcounts[i] = j;
	displs[i] = i * j;
      }
      for(int i=0;i<j;i++) {
	in[i] = i+myself*j;
      }

      if(myself == root)
          MPI.COMM_WORLD.scatterv(out, rcounts, displs, MPI.INT, root);
      else
          MPI.COMM_WORLD.scatterv(in, j, MPI.INT, root);

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
