/* 
 *
 * This file is a port from "iallgather.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Iallgather.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Iallgather
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself, tasks;
    IntBuffer out, in;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in  = MPI.newIntBuffer(MAXLEN * tasks);
    out = MPI.newIntBuffer(MAXLEN);
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int i = 0; i < j; i++) {
	out.put(i, myself);
      }

      request = MPI.COMM_WORLD.iAllGather(out, j, MPI.INT, in, j, MPI.INT);
      request.waitFor();
      request.free();

      for (int i = 0; i < tasks; i++)  {
	for (int k = 0; k < j; k++) {
	  if (in.get(k + i * j) != i) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(k + i * j) +
					") at index " + (k + i * j) +
					" of " + (j * tasks) +
					" (should be " + i + ")\n"); 
	    break; 
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
