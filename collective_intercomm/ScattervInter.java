/* 
 *
 * This file is a port from "scatterv_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ScattervInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class ScattervInter
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int size, rank, color, local_rank;
    int portLength[] = new int[1];
    char portBuffer[];
    String portName = null;
    Intracomm comm;
    Intercomm intercomm;
    
    MPI.Init(args);

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    /* Even rank gets 1 while odd ranks get 0 value for color */
    color = ((rank % 2) != 0) ? 0 : 1;
    comm = MPI.COMM_WORLD.split(color, rank);
    
    if (color != 0) {
      /* even ranks */
      local_rank = comm.getRank();
      
      if(local_rank == 0) {
        portName = Intracomm.openPort();
        portLength[0] = portName.length();
        portBuffer = new char[MPI.MAX_PORT_NAME];
        portName.getChars(0, portName.length(), portBuffer, 0);
        MPI.COMM_WORLD.send(portLength, 1, MPI.INT, rank + 1, 1);
        MPI.COMM_WORLD.send(portBuffer, portLength[0], MPI.CHAR, rank + 1, 1);
      }

      intercomm = comm.accept(portName, 0);
      intercomm_scatterv(intercomm, local_rank, -1);
      intercomm.disconnect();

      if(local_rank == 0) {
        Intracomm.closePort(portName);
      }
    }
    else {
      /* odd ranks */
      local_rank = comm.getRank();

      if(local_rank == 0) {
        MPI.COMM_WORLD.recv(portLength, 1, MPI.INT, rank - 1, 1);
        /* allocate memory for the the port name */
        portBuffer = new char[portLength[0]];
        MPI.COMM_WORLD.recv(portBuffer, portLength[0], MPI.CHAR, rank - 1, 1);
        portName = new String(portBuffer);
      }
      
      /* connect to the server */
      intercomm = comm.connect(portName, 0);
      intercomm_scatterv(intercomm, -1, local_rank);
      intercomm.disconnect();
    }
    comm.free();
    MPI.Finalize();
  }

  private static void intercomm_scatterv(Intercomm inter, int rank, int crank)
    throws MPIException
  {
    int out[], in[], disps[], counts[];
    int tasks, count;
    
    if (rank != -1)
      tasks = inter.getSize();
    else
      tasks = inter.getRemoteSize();

    in  = new int[MAXLEN];
    out = new int[MAXLEN * tasks];
    disps = new int[tasks];
    counts = new int[tasks];
    
    for(int j = 1, root = 0; j <= MAXLEN; j *= 10, root = (root+1)%tasks) {
      if(rank == root) {
	for(int i = 0; i < j*tasks; i++) {
	  out[i] = i;
	}
	count = 0;
	for(int i = 0; i < tasks; ++i) {
	  disps[i] = count;
	  counts[i] = j;
	  count += j;
	}
	inter.scatterv(out, counts, disps, MPI.INT,
				in, j, MPI.INT, MPI.ROOT);
      } else if (rank == -1) {
	inter.scatterv(out, counts, disps, MPI.INT,
				in, j, MPI.INT, root);
	for(int k = 0; k < j; k++) {
	  if(in[k] != k+crank*j) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"task " + crank + ": bad " +
					"answer (" + in[k] +
					") at index " + k +
					" of " + j + " (should be " +
					(k+crank*j) + ")\n");
	    break; 
	  }
	}
      } else {
	inter.scatterv(out, counts, disps, MPI.INT,
				in, j, MPI.INT, MPI.PROC_NULL);
      }
    }
  }
}
