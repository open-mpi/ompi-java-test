/* 
 *
 * This file is a port from "improbe.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Improbe.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Improbe
{
  public static void main (String args[]) throws MPIException
  {
    int me,cnt,src,tag;
    int data[] = new int[1];
    IntBuffer flag = MPI.newIntBuffer(1);
    Comm comm;
    Status status;
    Request req;

    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.getRank();
    Message msg = new Message();

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
 
    if(me == 0) {
      data[0] = 7;
      comm.send (data, 1, MPI.INT, 1, 1);
    } else if(me == 1)  {
      for(;;)  {
	status = msg.imProbe(0, 1, comm);
	if(status != null) break;
      }
      src = status.getSource();
      if(src != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Improbe: src = " +
				    src + ", should be 0\n");
      tag = status.getTag();
      if(tag != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Improbe: tag = " +
				    tag + ", should be 1\n");
      cnt = status.getCount(MPI.INT);
      if(cnt != 1) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Improbe: cnt = " +
				    cnt + ", should be 1\n");
      msg.mRecv(data, cnt, MPI.INT);
      if(data[0] != 7) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Recv, data = " +
				    data[0] + ", should be 7\n");
    }

    /* Probe for source = MPI_PROC_NULL */
    status = msg.imProbe(MPI.PROC_NULL, 30, comm);
    if(!msg.isNoProc()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Mprobe: should have " +
				  "gotten MESSAGE_NO_PROC\n");
    }
    if (MPI.PROC_NULL != status.getSource() ||
	MPI.ANY_TAG != status.getTag()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Mprobe: should have " +
				  "gotten \"PROC_NULL\" status\n");
    }
    cnt = status.getCount(MPI.CHAR);
    if (0 != cnt) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Mprobe: should have " +
				  "gotten 0 count in status\n");
    }

    /* Now receive that probed MPI_MESSAGE_NO_PROC */
    req = msg.imRecv(flag, 1, MPI.INT);
    status = req.waitStatus();
    req.free();
    if (MPI.PROC_NULL != status.getSource() ||
	MPI.ANY_TAG != status.getTag()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Mrecv: should have " +
				  "gotten \"PROC_NULL\" status\n");
    }
    cnt = status.getCount(MPI.CHAR);
    if (0 != cnt) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Mrecv: should have " +
				  "gotten 0 count in status\n");
    }

    comm.barrier();
    MPI.Finalize();
  }
}
