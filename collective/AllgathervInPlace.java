/* 
 *
 * This file is a port from "allgatherv_in_place.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: AllgathervInPlace.java		Author: S. Gross
 *
 */

import mpi.*;

public class AllgathervInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int in[], recvcounts[], disp[];
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    in = new int[MAXLEN * (tasks + 1) * tasks / 2];
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
	in[i] = 0xdeadbeef;
      }
      
      /* Set my values in the buffer */
      for (int k = 0; k < recvcounts[myself]; k++) {
	in[k + disp[myself]] = myself;
      }
      
      MPI.COMM_WORLD.allGatherv(in, recvcounts, disp, MPI.INT);
      
      for (int count = 0, k = 0; k < tasks; k++) {
	for (int i = 0; i < j * (k + 1); i++, ++count) {
	  if (in[count] != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"bad answer (" + in[count] +
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
