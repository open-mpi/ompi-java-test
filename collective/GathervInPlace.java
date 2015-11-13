/* 
 *
 * This file is a port from "gatherv_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: GathervInPlace.java			Author: S. Gross
 *
 */

import mpi.*;

public class GathervInPlace
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
    
    in = new int[MAXLEN * tasks];
    out = new int[MAXLEN * tasks];
    for(int j=1,root=0;j<=MAXLEN;j*=10,root=(root+1)%tasks)  {
      for(int i=0;i<j;i++) {
	out[i] = i;
	in[myself*j+i] = i;
      }
      if(myself == root) {
	for(int i = 0; i < tasks; i++) {
	  rcounts[i] = j;
	  displs[i] = i * j;
	}
	MPI.COMM_WORLD.gatherv(in, rcounts, displs, MPI.INT, root);
      } else {
	MPI.COMM_WORLD.gatherv(out, j, MPI.INT, root);
      }
      
      if(myself == root)  {
	for(int i=0;i<tasks;i++)  {
	  for(int k=0;k<j;k++) {
	    if(in[i*j+k] != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in[i * j + k] +
					") at index " + (i * j + k) +
					" of " + (j * tasks) +
					" (should be " + k + ")\n"); 
	      break; 
	    }
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
