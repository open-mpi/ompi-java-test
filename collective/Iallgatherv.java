/* 
 *
 * This file is a port from "iallgatherv.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Iallgatherv.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Iallgatherv
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int count, sendcount, myself, tasks, disp[], recvcounts[];
    IntBuffer out, in;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in  = MPI.newIntBuffer(MAXLEN * (tasks + 1) * tasks / 2);
    out = MPI.newIntBuffer(MAXLEN * tasks);
    recvcounts = new int[tasks];
    disp = new int[tasks];
    for (int i = 0; i < MAXLEN * tasks; i++) {
      out.put(i, 1);
    }
    
    for(int j=1;j<=MAXLEN;j*=10)  {
      int scan_size = 0;
      for(int i=0;i<tasks;i++) {  
	disp[i] = scan_size;
	recvcounts[i] = j * (i + 1);
	scan_size += recvcounts[i];
      }
      sendcount = recvcounts[myself];
      for (int k=0; k<sendcount; k++) {
	out.put(k, myself);
      }

      request = MPI.COMM_WORLD.iAllGatherv(out, sendcount, MPI.INT,
                                           in, recvcounts, disp, MPI.INT);
      request.waitFor();
      request.free();
      
      count = 0;
      for(int k=0; k<tasks;k++) {
	for (int i = 0; i < j*(k+1); i++) {
	  if(in.get(count) != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(count) +
					") at index " + count +
					" of " + (j * tasks) +
					" (should be " + k + ")\n"); 
	    break;
	  }
	  count++;
	}
      }
      
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
