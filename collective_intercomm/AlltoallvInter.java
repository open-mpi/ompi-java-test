/* 
 *
 * This file is a port from "alltoallv_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: AlltoallvInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class AlltoallvInter
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
      intercomm_alltoallv(intercomm, local_rank);
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
      intercomm_alltoallv(intercomm, local_rank);
      intercomm.disconnect();
    }

    comm.free();
    MPI.Finalize();
  }
  
  
  
  private static void intercomm_alltoallv(Intercomm inter, int rank)
    throws MPIException
  {
    int sbuf[], rbuf[], sendcounts[], recvcounts[], rdispls[], sdispls[];
    int size;
    /* int *p; */
    int p;
    Comm comm;
    
    /* Create the buffer */
    size = inter.getSize();
    sbuf = new int[size * size];
    rbuf = new int[size * size];
    
    /* Initialize buffers */
    for (int i = 0; i < size * size; i++) {
      sbuf[i] = i + 100*rank;
      rbuf[i] = -i;
    }
    
    /* Create and load the arguments to alltoallv */
    sendcounts = new int[size];
    recvcounts = new int[size];
    rdispls    = new int[size];
    sdispls    = new int[size];

    for (int i = 0; i < size; i++) {
      sendcounts[i] = i;
      recvcounts[i] = rank;
      rdispls[i]    = i * rank;
      sdispls[i]    = ((i * (i+1))/2);
    }

    inter.allToAllv(sbuf, sendcounts, sdispls, MPI.INT,
		    rbuf, recvcounts, rdispls, MPI.INT);
    
    /* Check rbuf */
    for (int i = 0; i < size; i++) {
      /* p = rbuf + rdispls[i];
       * for (int j = 0; j < rank; j++) {
       *   if (p[j] != i * 100 + (rank*(rank+1))/2 + j) {
       */
      for (int j = 0; j < rank; j++) {
	p = rdispls[i] + j;
	if (rbuf[p] != i * 100 + (rank*(rank+1))/2 + j) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + rbuf[p] +
				      " (should be " +
				      (i * 100 + (rank*(rank+1))/2 + j) +
				      ")\n"); 
	}
      }
    }
  }
}
