/* 
 *
 * This file is a port from "ireduce_in_place.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IreduceInPlace.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IreduceInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    Request request;
    
    IntBuffer in  = MPI.newIntBuffer(MAXLEN),
              out = MPI.newIntBuffer(MAXLEN);

    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    for (int j = 10; j <= MAXLEN; j *= 10)  {
      for (int root = 0; root < tasks; ++root) {
	for (int i = 0; i < j; i++) {
	  out.put(i, i);
	}
	
	if(myself == root) {
	  for (int i = 0; i < j; i++) {
	    in.put(i, i);
	  }

	  request = MPI.COMM_WORLD.iReduce(in, j, MPI.INT, MPI.SUM, root);
	  request.waitFor();
          request.free();

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
	} else {
	  request = MPI.COMM_WORLD.iReduce(
                    out, in, j, MPI.INT, MPI.SUM, root);
	  request.waitFor();
          request.free();
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
