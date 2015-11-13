/* 
 *
 * This file is a port from "igather_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IgatherInPlace.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IgatherInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    IntBuffer in  = MPI.newIntBuffer(MAXLEN * tasks),
              out = MPI.newIntBuffer(MAXLEN * tasks);

    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int root = 0 ; root < tasks; ++root) {
	for (int i = 0; i < j; i++) {
	  out.put(i, i);
	  in.put(i, -1);
	}
	
	if (myself == root) {
	  for (int i = 0; i < j; i++) {
	    in.put(myself * j + i, i);
	  }

	  request = MPI.COMM_WORLD.iGather(in, j, MPI.INT, root);
	  request.waitFor();
          request.free();

	  for (int i = 0; i < tasks; i++)  {
	    for (int k = 0; k < j; k++) {
	      if (in.get(i * j + k) != k) {  
		OmpitestError.ompitestError(OmpitestError.getFileName(),
					    OmpitestError.getLineNumber(),
					    " bad answer (" +
					    in.get(i * j + k) +
					    ") at index " + (i * j + k) +
					    " of " + (j * tasks) +
					    " (should be " + k + ")\n"); 
		break; 
	      }
	    }
	  }
	} else {
	  request = MPI.COMM_WORLD.iGather(out, j, MPI.INT, root);
	  request.waitFor();
          request.free();
	}
      }
    }
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
