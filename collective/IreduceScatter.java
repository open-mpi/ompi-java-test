/* 
 *
 * This file is a port from "ireduce_scatter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IreduceScatter.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IreduceScatter
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks, recvcounts[];
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    IntBuffer in  = MPI.newIntBuffer(MAXLEN * tasks),
              out = MPI.newIntBuffer(MAXLEN * tasks * tasks);

    recvcounts = new int[tasks];
    for (int i = 0; i < MAXLEN * tasks; i++)
      out.put(i, 1);
    
    for(int j=1;j<=MAXLEN*tasks;j*=10)  {
      for(int i=0;i<tasks;i++) {
	recvcounts[i] = j;
      }
      for(int i=0;i<j*tasks;i++) {
	out.put(i, i);
      }
      
      request = MPI.COMM_WORLD.iReduceScatter(out, in, recvcounts,
                                              MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();
      
      for(int k=0;k<j;k++) {
	if(in.get(k) != tasks*(myself*j+k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + in.get(k) +
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
