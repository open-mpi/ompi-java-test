/* 
 *
 * This file is a port from "c_create_size.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CCreateSize.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CCreateSize
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    /*
    if (rank == 0) {
        buffer = new int[0];
    } else {
        MPI_Alloc_mem(sizeof(int) * rank, MPI.INFO_NULL, &buffer);
    }
    */

    IntBuffer buffer = MPI.newIntBuffer(rank);
    Win win = new Win(buffer, rank, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    win.free();
    
    /*
    if (rank != 0)
      MPI_Free_mem(buffer);
    */
    
    MPI.Finalize();
  }
}
