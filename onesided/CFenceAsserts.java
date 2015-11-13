/* 
 *
 * This file is a port from "c_fence_asserts.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CFenceAsserts.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CFenceAsserts
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    IntBuffer buffer = MPI.newIntBuffer(1);

    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    win.fence(MPI.MODE_NOPRECEDE|MPI.MODE_NOSTORE);
    win.fence(MPI.MODE_NOSUCCEED|MPI.MODE_NOPUT);
    win.free();

    MPI.Finalize();
  }
}
