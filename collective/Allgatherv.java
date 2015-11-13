/* 
 *
 * This file is a port from "allgatherv.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Allgatherv.java		Author: S. Gross
 *
 */

import mpi.*;

public class Allgatherv
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself, tasks;
    int count, sendcount;
    int out[], in[], recvcounts[], disp[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN * (tasks + 1) * tasks / 2];
    out = new int[MAXLEN * tasks];
    recvcounts = new int[tasks];
    disp = new int[tasks];
    for (int i = 0; i < MAXLEN * tasks; i++) {
      out[i] = 1;
    }
    for(int j = 1; j <= MAXLEN; j *= 10)  {
      int scan_size = 0;
      for(int i = 0; i < tasks; i++) {  
	disp[i] = scan_size;
	recvcounts[i] = j * (i + 1);
	scan_size += recvcounts[i];
      }
      sendcount = recvcounts[myself];
      for (int k = 0; k < sendcount; k++) {
	out[k] = myself;
      }
      MPI.COMM_WORLD.allGatherv(out, sendcount, MPI.INT, in,
				recvcounts, disp, MPI.INT);
      
      count = 0;
      for(int k = 0; k < tasks; k++) {
	for (int i = 0; i < j*(k+1); i++) {
	  if(in[count] != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"bad answer (" + in[count] +
					") at index " + count +
					" of " + (j * tasks) +
					" (should be " + k + ")\n"); 
	    break;
	  }
	  count++;
	}
      }
      
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
