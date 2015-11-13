/* 
 *
 * This file is a port from "iallgatherv_in_place.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: IallgathervInPlace.java	Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IallgathervInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks, recvcounts[], disp[];
    IntBuffer in;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = MPI.newIntBuffer(MAXLEN * (tasks + 1) * tasks / 2);
    recvcounts = new int[tasks];
    disp = new int[tasks];
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      int scan_size = 0;
      for (int i = 0; i < tasks; i++) {  
	disp[i] = scan_size;
	recvcounts[i] = j * (i + 1);
	scan_size += recvcounts[i];
      }
      
      /* Reset in buffer to garbage */
      for (int i = 0; i < MAXLEN * (tasks+1) * tasks / 2; ++i) {
	in.put(i, 0xdeadbeef);
      }
      
      /* Set my values in the buffer */
      for (int k = 0; k < recvcounts[myself]; k++) {
	in.put(k + disp[myself], myself);
      }
      
      request = MPI.COMM_WORLD.iAllGatherv(in, recvcounts, disp, MPI.INT);
      request.waitFor();
      request.free();
      
      for (int count = 0, k = 0; k < tasks; k++) {
	for (int i = 0; i < j * (k + 1); i++, ++count) {
	  if (in.get(count) != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(count) +
					") at index " + count +
					" of " + (j * tasks) +
					" (should be " + k + ")\n"); 
	    break;
	  }
	}
      }
    }
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
