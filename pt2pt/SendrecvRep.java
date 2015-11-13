/* 
 *
 * This file is a port from "SendrecvRep.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: SendrecvRep.java		Author: S. Gross
 *
 */

import mpi.*;

public class SendrecvRep
{
  public static void main (String args[]) throws MPIException
  {
    int buf[] = new int[1000];
    int src, dest, sendtag, recvtag, tasks, me;
    Status status;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    
    if(me < 2) {
      dest = 1 - me;
      src = dest;
      sendtag = me;
      recvtag = src;
      for(int i = 0; i < 100; i++) {
	buf[i] = me;
      }
      status = MPI.COMM_WORLD.sendRecvReplace(buf, 100, MPI.INT,
					       dest, sendtag,
					       src, recvtag);
      for(int i = 0; i < 100; i++)
	if(buf[i] != src) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Sendrecv: " +
				      "incorrect data\n"); 
	  break;
	}
      if(status.getSource() != src)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Sendrecv: " +
				    "incorrect source\n");
      if(status.getTag() != recvtag)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Sendrecv: " +
				    "incorrect tag (" + status.getTag() +
				    ")\n");
    }
    if (me == 0) {
      src = tasks - 1;
    } else {
      src = me - 1;
    }
    if (me == (tasks - 1)) {
      dest = 0;
    } else {
      dest = me + 1;
    }
    sendtag = me;
    recvtag = src;
    for(int i = 0; i < 100; i++) {
      buf[i] = me;
    }
    status = MPI.COMM_WORLD.sendRecvReplace(buf, 100, MPI.INT,
				     dest, sendtag,
				     src, recvtag);
    for(int i = 0; i < 100; i++) 
      if(buf[i] != src) { 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Sendrecv: " +
				    "incorrect data\n"); 
	break;
      }
    if(status.getSource() != src)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Sendrecv: " +
				  "incorrect source\n");
    if(status.getTag() != recvtag)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Sendrecv: " +
				  "incorrect tag (" + status.getTag() +
				  ")\n");
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
