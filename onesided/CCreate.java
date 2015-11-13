/* 
 *
 * This file is a port from "c_create.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CCreate.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CCreate
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    //MPI_Alloc_mem(sizeof(int), MPI.INFO_NULL, &buffer);
    IntBuffer buffer = MPI.newIntBuffer(1);

    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);

    win.free();
    //MPI_Free_mem(buffer);
    MPI.Finalize();
  }
}
