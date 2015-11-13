/* 
 *
 * This file is a port from "ireduce.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Ireduce.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Ireduce
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int root, myself,tasks;
    Request request;
    
    IntBuffer in  = MPI.newIntBuffer(MAXLEN),
              out = MPI.newIntBuffer(MAXLEN);

    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    root = tasks/2;
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int i = 0; i < j; i++) {
	out.put(i, i);
      }

      request = MPI.COMM_WORLD.iReduce(out, in, j, MPI.INT, MPI.SUM, root);
      request.waitFor();
      request.free();

      if (myself == root)  {
	for (int k = 0; k < j; k++) {
	  if (in.get(k) != k * tasks) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(k) +
					") at index " + k + " of " +
					j + " (should be " +
					(k * tasks) + ")\n"); 
	    break;
	  }
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
