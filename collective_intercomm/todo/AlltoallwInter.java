/* 
 *
 * This file is a port from "alltoallw_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: AlltoallwInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class AlltoallwInter
{
  final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int size, rank, color, local_rank;
    int port_length[] = new int[1];
    byte port_name[];
    Comm comm;
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
      if (local_rank == 0) {
	port_name = new byte[MPI.MAX_PORT_NAME];
	MPI_Open_port(MPI.INFO_NULL, port_name);
	// port_length = strlen(port_name) + 1;
	port_length[0] = (new String(port_name)).length() + 1;
	
	MPI.COMM_WORLD.send (port_length, 1, MPI.INT, rank + 1, 1);
	MPI.COMM_WORLD.send (port_name, port_length[0], MPI.BYTE,
			     rank + 1, 1);
      }
      
      MPI_Comm_accept(port_name, MPI.INFO_NULL, 0, comm, &intercomm);
      intercomm_alltoallw(intercomm, local_rank);
      MPI_Comm_disconnect(&intercomm);
      
      if (local_rank == 0) {
	MPI_Close_port(port_name);
      }
      
    } else {
      /* odd ranks */
      local_rank = comm.getRank();
      if (local_rank == 0) {
	MPI.COMM_WORLD.recv(port_length, 1, MPI.INT, rank - 1, 1);
	
	/* allocate memory for the the port name */
	port_name = new byte[port_length[0]];
	MPI.COMM_WORLD.recv(port_name, port_length[0], MPI.BYTE,
			    rank - 1, 1);
      }
      
      /* connect to the server */
      MPI_Comm_connect(port_name, MPI.INFO_NULL, 0, comm, &intercomm);
      intercomm_alltoallw(intercomm, local_rank);
      MPI_Comm_disconnect(&intercomm);
    }

    comm.free();
    MPI.Finalize();
  }
  
  
  
  private static void intercomm_alltoallw(Intercomm inter, int rank)
    throws MPIException
  {
    int sbuf[], rbuf[], sendcounts[], recvcounts[], rdispls[], sdispls[];
    int size;
    /* MPI_Aint extent;
     * int *p;
     */
    int extent, p;
    Datatype sdtypes[], rdtypes[];
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
    
    /* Create and load the arguments to alltoallw */
    sendcounts = new int[size];
    recvcounts = new int[size];
    rdispls    = new int[size];
    sdispls    = new int[size];
    sdtypes    = new Datatype[size];
    rdtypes    = new Datatype[size];

    extent = MPI.INT.getExtent();
    for (int i = 0; i < size; i++) {
      sendcounts[i] = i;
      recvcounts[i] = rank;
      rdispls[i]    = i * rank * extent;
      sdispls[i]    = ((i * (i+1))/2) * extent;
      sdtypes[i] = MPI.INT;
      rdtypes[i] = MPI.INT;
    }

    MPI_Alltoallw(sbuf, sendcounts, sdispls, sdtypes,
		  rbuf, recvcounts, rdispls, rdtypes, inter);
    
    /* Check rbuf */
    for (int i = 0; i < size; i++) {
      /* p = rbuf + (rdispls[i] / extent);
       * for (int j = 0; j < rank; j++) {
       *   if (p[j] != i * 100 + (rank*(rank+1))/2 + j) {
       *   ...
       */
      for (int j = 0; j < rank; j++) {
        p = (rdispls[i] / extent) + j;
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
