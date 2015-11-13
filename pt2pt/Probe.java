/* 
 *
 * This file is a port from "probe.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Probe.java			Author: S. Gross
 *
 */

import mpi.*;

public class Probe
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
    data[0] = -1;
    
    /* probe for specific source, tag */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = comm.probe(i, i);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(1): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(1): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(1): cnt = " +
				      cnt + ", should be 1\n");
	comm.recv(data, cnt, MPI.INT, src, tag);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(1), data = " +
				      data[0] + ", should be " + i + "\n");
      }
    }
    
    /* probe for specific source, tag = MPI_ANY_TAG */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = comm.probe(i, MPI.ANY_TAG);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(2): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(2): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(2): cnt = " +
				      cnt + ", should be 1\n");
	comm.recv(data, cnt, MPI.INT, src, tag);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(2), data = " +
				      data[0] + ", should be " + i + "\n");
      }
    }
    
    /* probe for specific tag, source = MPI_ANY_SOURCE */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i = 1; i < tasks; i++)  {
	status = comm.probe(MPI.ANY_SOURCE, i);
	src = status.getSource();
	if(src != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(3): src = " +
				      src + ", should be " + i + "\n");
	tag = status.getTag();
	if(tag != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(3): tag = " +
				      tag + ", should be " + i + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(3): cnt = " +
				      cnt + ", should be 1\n");
	comm.recv(data, cnt, MPI.INT, src, tag);
	if(data[0] != i) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(3), data = " +
				      data[0] + ", should be " + i + "\n");
      }
    }
    
    /* probe for source = MPI_ANY_SOURCE, tag = MPI_ANY_TAG */
    if(me != 0) {
      data[0] = me;
      comm.send (data, 1, MPI.INT, 0, me);
    } else  {
      for(int i=1;i<tasks;i++)  {
	status = comm.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
	src = status.getSource();
	tag = status.getTag();
	if(src != tag)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(4): tag = " +
				      tag + ", should be " + src + "\n");
	cnt = status.getCount(MPI.INT);
	if(cnt != 1) 
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Probe(4): cnt = " +
				      cnt + ", should be 1\n");
	comm.recv(data, cnt, MPI.INT, src, tag);
	if(data[0] != src)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Recv(4), data = " +
				      data[0] + ", should be " + src + "\n");
      }
    }
    
    comm.barrier ();
    MPI.Finalize();
  }
}
