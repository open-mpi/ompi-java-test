/* 
 *
 * This file is a port from "interf.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Interf.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Interf
{
  public static void main (String args[]) throws MPIException
  {
    int me,tasks;
    IntBuffer val1 = MPI.newIntBuffer(1),
              val2 = MPI.newIntBuffer(1);
    Comm my_comm;
    Request request1,request2;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    val1.put(0, -1);
    val2.put(0, -1);

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    my_comm = MPI.COMM_WORLD.clone();
    if(me == 0)  {
      val1.put(0, 1);
      MPI.COMM_WORLD.send (val1, 1, MPI.INT, 1, 1);
      val2.put(0, 2);
      my_comm.send (val2, 1, MPI.INT, 1, 1);
    } else if(me == 1)  {
      request1 = my_comm.iRecv(val1, 1, MPI.INT, 0, 1);
      request2 = MPI.COMM_WORLD.iRecv(val2, 1, MPI.INT, 0, 1);
      request1.waitFor();
      request2.waitFor();
      if(val1.get(0) != 2 || val2.get(0) != 1)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR, messages were exchanged " +
				    "between different communicators\n");
    }
    my_comm.barrier();
    my_comm.free();
    MPI.Finalize();
  }
}
