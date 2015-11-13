/* 
 *
 * This file is a port from "reduce_scatter_block.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: ReduceScatterBlock.java	Author: S. Gross
 *
 */

import mpi.*;

public class ReduceScatterBlock
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks,recvcount;
    int out[], in[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN * tasks];
    out = new int[MAXLEN * tasks * tasks];
    for (int i = 0; i < MAXLEN * tasks; i++) {
      out[i] = 1;
    }
    
    for(int j=1;j<=MAXLEN*tasks;j*=10)  {
      recvcount = j;

      for(int i=0;i<j*tasks;i++) {
	out[i] = i;
      }
      
      MPI.COMM_WORLD.reduceScatterBlock(out, in, recvcount,
					MPI.INT, MPI.SUM);
      
      for(int k=0;k<j;k++) {
	if(in[k] != tasks*(myself*j+k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + in[k] +
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
