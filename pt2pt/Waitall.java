/* 
 *
 * This file is a port from "waitall.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Waitall.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Waitall
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, bytes;
    IntBuffer me   = MPI.newIntBuffer(1),
              data = MPI.newIntBuffer(1000);
    Request req[];
    
    MPI.Init(args);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[1000];

    for (int i = 0; i < tasks; i++) {
      req[2*i] = MPI.COMM_WORLD.iSend(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);
    }
    Request.waitAll(Arrays.copyOf(req, 2 * tasks));
    
    for (int i = 0; i < tasks; i++) {
      req[2*i] = MPI.COMM_WORLD.iSend(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);
    }
    Request.waitAll(Arrays.copyOf(req, 2 * tasks));
    
    /* Also try giving a 0 count and ensure everything is ok */
    Request.waitAll(new Request[0]);
    
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
