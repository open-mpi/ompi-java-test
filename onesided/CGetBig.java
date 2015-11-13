/* 
 *
 * This file is a port from "c_get_big.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CGetBig.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class CGetBig
{
  private final static int MSG_SIZE = 1024 * 256;

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    
    /*
    // MPI_Alloc_mem requires Unsafe.
    MPI_Alloc_mem(sizeof(int) * MSG_SIZE, MPI.INFO_NULL, &winArea);
    MPI_Alloc_mem(sizeof(int) * size * MSG_SIZE, MPI.INFO_NULL, &rcvArea);
    */
    IntBuffer winArea = MPI.newIntBuffer(MSG_SIZE),
              rcvArea = MPI.newIntBuffer(size * MSG_SIZE);

    Win win = new Win(winArea, MSG_SIZE, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    
    /* Have every assign their "get" area to be their rank value */
    for (int i = 0 ; i < MSG_SIZE ; ++i) {
      winArea.put(i, rank);
    }
    
    /* Have everyone get from everyone else */
    win.fence(0);
    for (int i = 0; i < size; ++i) {
      for (int j = 0 ; j < MSG_SIZE ; ++j) {
	rcvArea.put((i * MSG_SIZE) + j, -1);
      }
      win.get(slice(rcvArea, i * MSG_SIZE),
              MSG_SIZE, MPI.INT, i, 0, MSG_SIZE, MPI.INT);
    }
    win.fence(0);
    
    /* Check to see that we got the right value */
    for (int i = 0; i < size; ++i) {
      for (int j = 0 ; j < MSG_SIZE ; ++j) {
	if (rcvArea.get((i * MSG_SIZE) + j) != i) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "Rank " + rank + " got rcvArea[" +
				      (i * MSG_SIZE + j) + "] = " +
				      rcvArea.get((i * MSG_SIZE) + j) +
				      " when expecting " + i + "\n");
	}
      }
    }
    
    win.free();
    //MPI_Free_mem(winArea);
    //MPI_Free_mem(rcvArea);
    MPI.Finalize();
  }
}
