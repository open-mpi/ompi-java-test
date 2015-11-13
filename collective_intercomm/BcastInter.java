/* 
 *
 * This file is a port from "bcast_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: BcastInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class BcastInter
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
      intercomm_bcast(intercomm, local_rank);
      intercomm.disconnect();

      if(local_rank == 0) {
        Intracomm.closePort(portName);
      }
    } else {
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
      intercomm_bcast(intercomm, -1);
      intercomm.disconnect();
    }

    comm.free();
    MPI.Finalize();
  }

  private static void intercomm_bcast(Intercomm inter, int rank)
    throws MPIException
  {
    int size;
    int out[] = new int[MAXLEN];
    
    for (int j = 1; j <= MAXLEN; j *= 10)  {
      if (rank == 0) {
	for (int i = 0; i < j; i++) {
	  out[i] = i;
	}
	inter.bcast(out, j, MPI.INT, MPI.ROOT);
      } else if (rank != -1) {
	inter.bcast(out, j, MPI.INT, MPI.PROC_NULL);
      } else {
	inter.bcast(out, j, MPI.INT, 0);
	for (int k = 0; k < j; k++) {
	  if (out[k] != k) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + out[k] +
					") at index " + k +
					" of " + j +
					" (should be " + k + ")\n"); 
	    break; 
	  }
	}
      }
    }
  }
}
