/*  Function:	- tests synchonicity of MPI.Rsend between two ranks
 *
 * This file is a port from "rsend2.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Rsend2.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Rsend2
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, bytes;
    Request req[];

    IntBuffer me   = MPI.newIntBuffer(1),
              data = MPI.newIntBuffer(1000);

    MPI.Init(args);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[1000];

    for(int i = 0; i < tasks; i++)
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);
    MPI.COMM_WORLD.barrier ();

    for(int i = 0; i < tasks; i++)
      req[2*i] = MPI.COMM_WORLD.irSend(me, 1, MPI.INT, i, 1);
    Request.waitAll(Arrays.copyOf(req, 2 * tasks));
    
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
