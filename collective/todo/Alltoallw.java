/* Program to test Alltoallw(), uses only MPI_INT type. Have to
 * update it so that it uses other datatypes as well.
 *
 * This file is a port from "alltoallw.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Alltoallw.java			Author: S. Gross
 *
 */

import mpi.*;

public class Alltoallw
{
  public static void main (String args[]) throws MPIException
  {
    int rank, size;
    /* MPI_Aint extent
     * int *p;
     */
    int err, toterr, extent, p;
    int sbuf[], rbuf[], sendcounts[], recvcounts[],
	sdispls[], rdispls[];
    Datatype sdtypes[], rdtypes[];
    Comm comm;
   
    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    rank = comm.getRank();
    size = comm.getSize();
    err = 0;
    
    /* Create the buffer */
    sbuf = new int[size * size];
    rbuf = new int[size * size];
    
    /* Load up the buffers */
    for (int i = 0; i < size*size; i++) {
      sbuf[i] = i + 100*rank;
      rbuf[i] = -i;
    }
    
    /* Create and load the arguments to alltoallw */
    sendcounts = new int[size];
    recvcounts = new int[size];
    rdispls = new int[size];
    sdispls = new int[size];
    sdtypes = new Datatype[size];
    rdtypes = new Datatype[size];

    extent = MPI.INT.getExtent();
    for (int i = 0; i < size; i++) {
      sendcounts[i] = i;
      recvcounts[i] = rank;
      rdispls[i]    = i * rank * extent;
      sdispls[i]    = ((i * (i+1))/2) * extent;
      sdtypes[i] = MPI.INT;
      rdtypes[i] = MPI.INT;
    }

    MPI_Alltoallw( sbuf, sendcounts, sdispls, sdtypes,
		   rbuf, recvcounts, rdispls, rdtypes, comm );

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
					") (should be " +
					(i*100 + (rank*(rank+1))/2 + j) +
					")\n");
	}
      }
    }
   
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
