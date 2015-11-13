/* 
 *
 * This file is a port from "test2.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Test2.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Test2
{
  public static void main (String args[]) throws MPIException
  {
    int numtask, taskid;
    int outmsg[] = new int[1];
    IntBuffer inmsg = MPI.newIntBuffer(1);
    int type = 1;
    int source, rtype = type;
    int pair_lo, pair_hi;
    boolean flag;
    Request msgid;
        
    MPI.Init(args);
    taskid = MPI.COMM_WORLD.getRank();
    numtask = MPI.COMM_WORLD.getSize();
    inmsg.put(0, -1);
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    pair_lo = (taskid / 2) * 2;
    pair_hi = ((taskid / 2) * 2) + 1;
    
    if (taskid == pair_hi) {
      MPI.COMM_WORLD.barrier ();
      outmsg[0] = 5;
      type = 1;
      MPI.COMM_WORLD.send (outmsg, 1, MPI.INT, pair_lo, type);
    } else if (taskid == pair_lo) {
      source = MPI.ANY_SOURCE;
      rtype = MPI.ANY_TAG;
      msgid = MPI.COMM_WORLD.iRecv(inmsg, 1, MPI.INT, source, rtype);

      if(msgid.test())
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR -- something was wrong " +
				    "on the hi side\n");
      MPI.COMM_WORLD.barrier();
      Status status = msgid.waitStatus();
      if (inmsg.get(0) != 5 || status.getSource() != pair_hi ||
	  status.getTag() != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR -- something was wrong " +
				    "on the lo side\n");
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
