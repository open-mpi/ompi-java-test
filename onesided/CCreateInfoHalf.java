/* 
 *
 * This file is a port from "c_create_info_half.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: CCreateInfoHalf.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CCreateInfoHalf
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    IntBuffer buffer = MPI.newIntBuffer(1);

    Info info = new Info();
    if (rank % 2 != 0) {
        info.set("no_locks", "false");
    }
    Win win = new Win(buffer, 1, 1, info, MPI.COMM_WORLD);
    win.free();
    info.free();
    MPI.Finalize();
  }
}
