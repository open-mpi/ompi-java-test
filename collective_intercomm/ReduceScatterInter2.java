/* 
 *
 * This file is a port from "reduce_scatter_inter2.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of the
 * code is mainly the same as in the original file.
 *
 *
 * File: ReduceScatterInter2.java	Author: S. Gross
 *
 */

import mpi.*;

public class ReduceScatterInter2
{
  public static void main (String args[]) throws MPIException
  {
    int size;
    
    MPI.Init(args);

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    
    /* Run test with all procs */
    run_test(MPI.COMM_WORLD);
    
    /* If we have more than two procs, then reduce the size of the comm
     * by 1 and run the test again (to force running the test on both
     * odd and even comm sizes)
     */
    size = MPI.COMM_WORLD.getSize();
    if (size > 2) {
      int color, rank;
      Comm smaller;
      
      rank = MPI.COMM_WORLD.getRank();
      color = (rank != (size - 1)) ? 0 : MPI.UNDEFINED;
      smaller = MPI.COMM_WORLD.split(color, rank);
      if (!smaller.isNull()) {
	run_test(smaller);
	smaller.free();
      }
    }
    
    MPI.Finalize();
  }
  
  
  
  private static void run_test(Comm comm) throws MPIException
  {
    int i;
    int rank;
    int error;
    int tag = 1;
    
    int lrank;                  /* rank within local group of intercommunicator */
    int lsize;                  /* size of local group of intercommunicator */
    int rsize;                  /* size of remote group of intercommunicator */
    
    int inValues[], outValues[], counts[];
    Comm sub_comm;
    Intercomm inter_comm;
    
    rank = comm.getRank();
    sub_comm = ((Intracomm) comm).split(rank % 2, rank);
    if (0 == rank % 2) {
      inter_comm = comm.createIntercomm(sub_comm, 0, 1, tag);
    } else {
      inter_comm = comm.createIntercomm(sub_comm, 0, 0, tag);
    }
    
    lrank = inter_comm.getRank();
    lsize = inter_comm.getSize();
    rsize = inter_comm.getRemoteSize();
    
    inValues = new int[lsize * rsize];
    outValues = new int[rsize];
    counts = new int[lsize];
    for (i = 0; i < lsize * rsize; i++) {
      inValues[i] = rank;
    }
    for (i = 0; i < rsize; i++) {
      outValues[i] = 0;
    }
    for (i = 0; i < lsize; i++) {
      counts[i] = rsize;
    }
    inter_comm.setErrhandler(MPI.ERRORS_RETURN);
    try {
      inter_comm.reduceScatter(inValues, outValues, counts,
			       MPI.INT, MPI.SUM);
    }
    catch (MPIException ex)
    {
      error = ex.getErrorClass();
      if (MPI.SUCCESS != error) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Ran into error: " + error + "\n");
      }
    }

    int success = 1;
    if (rank % 2 == 0) {
      int checkValue = rsize * rsize;
      for (i = 0; i < rsize; i++) {
	if (outValues[i] != checkValue) {
	  success = 0;
	}
      }
    } else {
      int checkValue = (rsize - 1) * rsize;
      for (i = 0; i < rsize; i++) {
	if (outValues[i] != checkValue) {
	  success = 0;
	}
      }
    }
    if (0 == success) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Check value did not succeed\n");
    }
  }
}
