/* 
 *
 * This file is a port from "c_put_big.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CPutBig.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class CPutBig
{
  private final static int MSG_SIZE = 1024 * 256;

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    
    /*
    // MPI_Alloc_mem requires Unsafe.
    MPI_Alloc_mem(sizeof(int) * size * MSG_SIZE, MPI.INFO_NULL, &winArea);
    MPI_Alloc_mem(sizeof(int) * MSG_SIZE, MPI.INFO_NULL, &putvals);
    */
    IntBuffer winArea = MPI.newIntBuffer(size * MSG_SIZE),
              putVals = MPI.newIntBuffer(MSG_SIZE);
    
    Win win = new Win(winArea, size * MSG_SIZE, 1,
                      MPI.INFO_NULL, MPI.COMM_WORLD);

    /* Set all the target areas to be -1 */
    for (int i = 0 ; i < MSG_SIZE ; ++i)
      putVals.put(i, rank);
    for (int i = 0; i < size * MSG_SIZE; ++i)
      winArea.put(i, -1);
    win.fence(0);
    
    /* Do a put to all other processes */
    for (int i = 0; i < size; ++i) {
      win.put(putVals, MSG_SIZE, MPI.INT, i, 
	      rank * MSG_SIZE, MSG_SIZE, MPI.INT);
    }
    win.fence(0);
    
    win.free();
    
    /* Check to see that we got the right values */
    for (int i = 0; i < size; ++i)
      for (int j = 0 ; j < MSG_SIZE ; ++j)
	if (winArea.get(i * MSG_SIZE + j) != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "Rank " + rank + " got winArea[" +
				      (i * MSG_SIZE + j) + " ]=" +
				      winArea.get(i * MSG_SIZE + j) +
				      " when expecting " + i + "\n");
    
    //MPI_Free_mem(winArea);
    //MPI_Free_mem(putVals);
    MPI.Finalize();
  }
}
