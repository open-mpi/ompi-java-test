/* 
 *
 * This file is a port from "c_put.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CPut.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class CPut
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    
    /*
    // MPI_Alloc_mem requires Unsafe.
    MPI_Alloc_mem(sizeof(int) * size, MPI.INFO_NULL, &winArea);
    MPI_Alloc_mem(sizeof(int) * size, MPI.INFO_NULL, &putvals);
    */
    IntBuffer winArea = MPI.newIntBuffer(size),
              putVals = MPI.newIntBuffer(size);
    
    Win win = new Win(winArea, size, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    
    /* Set all the target areas to be -1 */
    for (int i = 0; i < size; ++i) {
      winArea.put(i, -1);
      putVals.put(i, rank);
    }
    win.fence(0);
    
    /* Do a put to all other processes */
    for (int i = 0; i < size; ++i)
      win.put(slice(putVals, i), 1, MPI.INT, i, rank, 1, MPI.INT);
    win.fence(0);
    
    /* Check to see that we got the right values */
    for (int i = 0; i < size; ++i)
      if (winArea.get(i) != i)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Rank " + rank + " got winArea[" +
				    i + " ]=" + winArea.get(i) +
				    " when expecting " + i + "\n");
    
    win.free();
    //MPI_Free_mem(winArea);
    //MPI_Free_mem(putvals);
    MPI.Finalize();
  }
}
