/* 
 *
 * This file is a port from "ialltoall.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Ialltoall.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Ialltoall
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    IntBuffer out, in;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in  = MPI.newIntBuffer(MAXLEN * tasks);
    out = MPI.newIntBuffer(MAXLEN * tasks);
    for (int i = 0; i < MAXLEN * tasks; ++i) {
      out.put(i, myself);
    }
    
    for (int j = 1; j <= MAXLEN; j *= 10) {
      
      request = MPI.COMM_WORLD.iAllToAll(out, j, MPI.INT, in, j, MPI.INT);
      request.waitFor();
      request.free();
      
      for (int i = 0; i < tasks; ++i)  {
	for (int k = 0; k < j; ++k) {
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
