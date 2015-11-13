/* 
 *
 * This file is a port from "igather.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Igather.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Igather
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

    for(int j=1,root=0;j<=MAXLEN;j*=10,root=(root+1)%tasks)  {
      for(int i=0;i<j;i++) {
	out.put(i, i);
      }
      
      request = MPI.COMM_WORLD.iGather(out,j, MPI.INT, in,j, MPI.INT, root);
      request.waitFor();
      request.free();
      
      if(myself == root)  {
	for(int i=0;i<tasks;i++)  {
	  for(int k=0;k<j;k++) {
	    if(in.get(i*j+k) != k) {  
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
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
