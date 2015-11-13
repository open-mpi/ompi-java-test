/* 
 *
 * This file is a port from "ireduce_scatter_in_place.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: IreduceScatterInPlace.java	Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IreduceScatterInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks, recvcounts[];
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    recvcounts = new int[tasks];
    IntBuffer inout = MPI.newIntBuffer(MAXLEN * tasks);

    for (int j = 1; j <= MAXLEN;  j *= 10)  {
      for (int i = 0; i < tasks; i++) {
	recvcounts[i] = j;
      }
      for (int i = 0; i < j * tasks; i++) {
	inout.put(i, i);
      }
      
      request = MPI.COMM_WORLD.iReduceScatter(inout, recvcounts,
                                              MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();
      
      for (int k = 0; k < j; k++) {
	if (inout.get(k) != tasks * (myself * j + k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + inout.get(k) +
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
