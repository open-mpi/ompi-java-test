/* 
 *
 * This file is a port from "c_get.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CGet.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class CGet
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    
    /*
    // MPI_Alloc_mem requires Unsafe.
    MPI_Alloc_mem(sizeof(int), MPI.INFO_NULL, &winArea);
    MPI_Alloc_mem(sizeof(int) * size, MPI.INFO_NULL, &rcvArea);
    */
    IntBuffer winArea = MPI.newIntBuffer(1),
              rcvArea = MPI.newIntBuffer(size);

    Win win = new Win(winArea, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    
    /* Have every assign their "get" area to be their rank value */
    winArea.put(0, rank);
    win.fence(0);
    
    /* Have everyone get from everyone else */
    for (int i = 0; i < size; ++i) {
      rcvArea.put(i, -1);
      win.get(slice(rcvArea, i), 1, MPI.INT, i, 0, 1, MPI.INT);
    }
    win.fence(0);
    
    /* Check to see that we got the right value */
    for (int i = 0; i < size; ++i) 
      if (rcvArea.get(i) != i)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Rank " + rank + " got rcvArea[" +
				    i + "] = " + rcvArea.get(i) +
				    " when expecting " + i + "\n");
    
    win.free();
    //MPI_Free_mem(winArea);
    //MPI_Free_mem(rcvArea);
    MPI.Finalize();
  }
}
