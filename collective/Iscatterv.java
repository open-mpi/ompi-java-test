/* 
 *
 * This file is a port from "iscatterv.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Iscatterv.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Iscatterv
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int displs[] = new int[128],
	rcounts[] = new int[128];
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    IntBuffer in  = MPI.newIntBuffer(MAXLEN),
              out = MPI.newIntBuffer(MAXLEN * tasks);

    for(int j=1,root=0;j<=MAXLEN;j*=10,root=(root+1)%tasks) {
      if(myself == root) {
	for(int i=0;i<j*tasks;i++) {
	  out.put(i, i);
	}
	
	for(int i = 0; i < tasks; i++) {
	  rcounts[i] = j;
	  displs[i] = i * j;
	}
      }   
      
      request = MPI.COMM_WORLD.iScatterv(out, rcounts, displs, MPI.INT,
                                         in, j, MPI.INT, root);
      request.waitFor();
      request.free();
      
      for(int k=0;k<j;k++) {
	if(in.get(k) != k+myself*j) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "task " + myself  + ":" +
				      " bad answer (" + in.get(k) +
				      ") at index " + k +
				      " of " + j + " (should be " +
				      (k + myself * j) + ")\n"); 
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
