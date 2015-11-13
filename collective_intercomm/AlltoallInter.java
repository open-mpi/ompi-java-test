/* 
 *
 * This file is a port from "alltoall_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: AlltoallInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class AlltoallInter
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    int size, rank, color, local_rank;
    int  portLength[] = new int[1];
    char portBuffer[];
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
      String portName = Intracomm.openPort();
      portLength[0] = portName.length();
      portBuffer = new char[MPI.MAX_PORT_NAME];
      portName.getChars(0, portName.length(), portBuffer, 0);

      MPI.COMM_WORLD.send(portLength, 1, MPI.INT, rank + 1, 1);
      MPI.COMM_WORLD.send(portBuffer, portLength[0], MPI.CHAR, rank + 1, 1);
      intercomm = comm.accept(portName, 0);

      local_rank = comm.getRank();
      intercomm_alltoall(intercomm, local_rank);
      
      intercomm.disconnect();
      Intracomm.closePort(portName);
    }
    else {
      /* odd ranks */
      MPI.COMM_WORLD.recv(portLength, 1, MPI.INT, rank - 1, 1);

      /* allocate memory for the the port name */
      portBuffer = new char[portLength[0]];
      MPI.COMM_WORLD.recv(portBuffer, portLength[0], MPI.CHAR, rank - 1, 1);
      String portName = new String(portBuffer);

      /* connect to the server */
      intercomm = comm.connect(portName, 0);
      local_rank = comm.getRank();
      intercomm_alltoall(intercomm, local_rank);
      intercomm.disconnect();
    }

    comm.free();
    MPI.Finalize();
  }

  private static void intercomm_alltoall(Intercomm inter, int rank)
    throws MPIException
  {
    int in[], out[];
    int myself,tasks;

    /* Possibly something is wrong here, because "rank" is a parameter
     * and "myself" isn't used
     */
    rank = inter.getRank();
    tasks = inter.getSize();

    in  = new int[MAXLEN * tasks];
    out = new int[MAXLEN * tasks];
    
    for (int i = 0; i < MAXLEN * tasks; ++i) {
      out[i] = rank;
    }
    
    for (int j = 10000; j <= MAXLEN; j *= 10) {
      
      inter.allToAll(out, j, MPI.INT, in, j, MPI.INT);
      
      for (int i = 0; i < tasks; ++i)  {
	for (int k = 0; k < j; ++k) {
	  if (in[k + i * j] != i) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in[k + i * j] +
					") at index " + (k + i * j) +
					" of " + (j * tasks) +
					" (should be " + i + ")\n"); 
	    break; 
	  }
	}
      }
    }
  }
}
