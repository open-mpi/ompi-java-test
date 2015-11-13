/* 
 *
 * This file is a port from "iprobe.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Iprobe.java			Author: S. Gross
 *
 */

import mpi.*;

public class Iprobe
{
  public static void main (String args[]) throws MPIException
  {
    int me, cnt, src, tag;
    int data[] = new int[1];
    Comm comm;
    Status status;
    
    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.getRank();
    data[0] = -1;
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    
    if(me == 0) {
      data[0] = 7;
      comm.send (data, 1, MPI.INT, 1, 1);
    } else if(me == 1)  {
      for(;;)  {
	status = comm.iProbe (0, 1);
	if(status != null) {
	  break;
	}
      }
      src = status.getSource();
      if(src != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Probe: src = " +
				    src + " should be 0\n");
      tag = status.getTag();
      if(tag != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Probe: tag = " +
				    tag + " should be 1\n");
      cnt = status.getCount(MPI.INT);
      if(cnt != 1) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Probe: cnt = " +
				    cnt + " should be 1\n");
      comm.recv(data, cnt, MPI.INT, src, tag);
      if(data[0] != 7) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Recv, data = " +
				    data[0] + " should be 7\n");
    }
    comm.barrier ();
    MPI.Finalize();
  }
}
