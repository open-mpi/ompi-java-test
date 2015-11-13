/* 
 *
 * This file is a port from "reduce_scatter_in_place.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: ReduceScatterInPlace.java	Author: S. Gross
 *
 */

import mpi.*;

public class ReduceScatterInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int inout[], recvcounts[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    inout = new int[MAXLEN * tasks];
    recvcounts = new int[tasks];
    for (int j = 1; j <= MAXLEN;  j *= 10)  {
      for (int i = 0; i < tasks; i++) {
	recvcounts[i] = j;
      }
      for (int i = 0; i < j * tasks; i++) {
	inout[i] = i;
      }
      
      MPI.COMM_WORLD.reduceScatter(inout, recvcounts, MPI.INT, MPI.SUM);
      
      for (int k = 0; k < j; k++) {
	if (inout[k] != tasks * (myself * j + k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + inout[k] +
				      ") at index " + k + " of " +
				      j + " (should be " +
				      ((myself * j + k) * tasks) + ")\n");
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
