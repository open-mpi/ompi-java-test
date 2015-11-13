/* 
 *
 * This file is a port from "test3.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Test3.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Test3
{
  public static void main (String args[]) throws MPIException
  {
    IntBuffer out = MPI.newIntBuffer(1),
              in  = MPI.newIntBuffer(1);

    int myself, tasks;
    Request req1, req2;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    in.put(0, -1);
    out.put(0, 1);
    
    if (myself < 2) {
      if (myself == 0) {
	req1 = MPI.COMM_WORLD.iSend(out, 1, MPI.INT, 1, 1);
	req2 = MPI.COMM_WORLD.iRecv(in, 1, MPI.INT, 1, 2);
	for (;;) {
          if(req1.test())
	    break;
	}
	for (;;) {
          if(req2.test())
	    break;
	}
      } else if (myself == 1) {
	MPI.COMM_WORLD.send (out, 1, MPI.INT, 0, 2);
	MPI.COMM_WORLD.recv(in, 1, MPI.INT, 0, 1);
      }
      if (in.get(0) != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR IN TASK " + myself +
				    " (" + in.get(0) + ")\n");
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
