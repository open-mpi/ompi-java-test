/* 
 *
 * This file is a port from "mprobe.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Mprobe.java			Author: S. Gross
 *
 */

import mpi.*;

public class Mprobe
{
  public static void main (String args[]) throws MPIException
  {
    int me, cnt, src, tag, tasks;
    int data[] = new int[1];
    Comm comm;
    Status status;
    
    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.getRank();
    tasks = comm.getSize();
    Message msg = new Message();
    
    /* probe for specific source, tag */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = msg.mProbe(i, i, comm);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(1): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(1): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(1): cnt = " +
				      cnt + ", should be 1\n");
	msg.mRecv(data, cnt, MPI.INT);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(1), data = " +
				      data[0] + ", should be " + i +
				      "\n");
      }
    }
    
    /* probe for specific source, tag = MPI_ANY_TAG */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = msg.mProbe(i, MPI.ANY_TAG, comm);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(2): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(2): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(2): cnt = " +
				      cnt + ", should be 1\n");
	msg.mRecv(data, cnt, MPI.INT);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(2), data = " +
				      data[0] + ", should be " + i +
				      "\n");
      }
    }
    
    /* probe for specific tag, source = MPI_ANY_SOURCE */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = msg.mProbe(MPI.ANY_SOURCE, i, comm);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(3): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(3): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(3): cnt = " +
				      cnt + ", should be 1\n");
	msg.mRecv(data, cnt, MPI.INT);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(3), data = " +
				      data[0] + ", should be " + i +
				      "\n");
      }
    }
    
    /* probe for source = MPI_ANY_SOURCE, tag = MPI_ANY_TAG */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = msg.mProbe(MPI.ANY_SOURCE, MPI.ANY_TAG, comm);
	src = status.getSource();
	tag = status.getTag();
	if(src != tag)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(4): tag = " +
				      tag + ", should be " + src + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Mprobe(4): cnt = " +
				      cnt + ", should be 1\n");
	msg.mRecv(data, cnt, MPI.INT);
	if(data[0] != src)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(4), data = " +
				      data[0] + ", should be " + src +
				      "\n");
      }
    }
    
    /* Probe for source = MPI_PROC_NULL */
    status = msg.mProbe(MPI.PROC_NULL, 30, comm);
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
    status = msg.mRecv(data, 1, MPI.INT);
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
    
    comm.barrier ();
    MPI.Finalize();
  }
}
