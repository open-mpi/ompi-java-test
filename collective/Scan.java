/* 
 *
 * This file is a port from "scan.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Scan.java			Author: S. Gross
 *
 */

import mpi.*;

public class Scan
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
	out[i] = i;
      }
      MPI.COMM_WORLD.scan(out,in,j,MPI.INT,MPI.SUM);
      
      for(int k=0;k<j;k++) {
	if(in[k] != k*(myself+1)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + in[k] +
				      ") at index " + k + " of " +
				      j + " (should be " +
				      (k * (myself + 1)) + ")\n"); 
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
