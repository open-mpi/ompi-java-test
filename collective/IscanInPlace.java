/* 
 *
 * This file is a port from iscan_in_place".c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IscanInPlace.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IscanInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    int myself = MPI.COMM_WORLD.getRank(),
        tasks  = MPI.COMM_WORLD.getSize();

    IntBuffer out = MPI.newIntBuffer(MAXLEN),
              in  = MPI.newIntBuffer(MAXLEN);

    for(int j=1;j<=MAXLEN;j*=10)  {
      for(int i=0;i<j;i++) {
	in.put(i, i);
      }

      Request request = MPI.COMM_WORLD.iScan(in, j, MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();

      for(int k=0;k<j;k++) {
	if(in.get(k) != k*(myself+1)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + in.get(k) +
				      ") at index " + k +
				      " of " + j + " (should be " +
				      (k * (myself + 1)) + ")\n"); 
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
