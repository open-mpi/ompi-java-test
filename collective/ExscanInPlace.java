/* 
 *
 * This file is a port from "exscan_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File:  ExscanInPlace.java			Author: S. Gross
 *
 */

import mpi.*;

public class  ExscanInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int out[] = new int[MAXLEN],
	in[] = new int[MAXLEN];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    for(int j=1;j<=MAXLEN;j*=10)  {
      for(int i=0;i<j;i++) {
	in[i] = i;
      }
      
      MPI.COMM_WORLD.exScan(in, j, MPI.INT, MPI.SUM);
      
      if (myself != 0)
	for(int k=0;k<j;k++) {
	  if(in[k] != k*(myself)) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in[k] +
					") at index " + k + " of " + j +
					" (should be " + (k * myself) +
					")\n"); 
	    break; 
	  }
	}
      
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
