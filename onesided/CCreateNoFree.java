/* 
 *
 * This file is a port from "c_create_no_free.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CCreateNoFree.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CCreateNoFree
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    IntBuffer buffer = MPI.newIntBuffer(1);
    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    MPI.Finalize();
  }
}
