/* 
 *
 * This file is a port from "c_fence_put_1.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CFencePut1.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CFencePut1
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    IntBuffer buffer = MPI.newIntBuffer(2);

    buffer.put(0, rank);
    buffer.put(1, 0);

    Win win = new Win(buffer, 2, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    win.fence(MPI.MODE_NOPRECEDE|MPI.MODE_NOSTORE);
    win.put(buffer, 1, MPI.INT, (rank + 1) % size, 1, 1, MPI.INT);
    win.fence(MPI.MODE_NOSUCCEED|MPI.MODE_NOPUT);

    if (buffer.get(1) != (rank + size - 1) % size) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Put appears to have failed. " +
				  "Found " + buffer.get(1) +
				  ", expected " +
				  ((rank + size - 1) % size) + ".");
    }

    win.free();
    MPI.Finalize();
  }
}
