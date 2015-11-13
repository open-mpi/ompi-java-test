/* 
 *
 * This file is a port from "sub.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Sub.java			Author: S. Gross
 *
 */

import mpi.*;

public class Sub
{
  /* "MAXDIMS = 10;" and "MAXCOMMS = 20;" in the original C file */
  private final static int MAXDIMS = 2;
  private final static int MAXCOMMS = 20;

  public static void main (String args[]) throws MPIException
  {
    int me, tasks, size, rank;
    int cnt = 0;
    int dims[] = new int[MAXDIMS];
    boolean periods[] = new boolean[MAXDIMS],
	    remain[] = new boolean[MAXDIMS];
    Comm mcw;
    CartComm comm, subcomm;
    CartComm comms[];
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    comms = new CartComm[MAXCOMMS];

    /* We need at least 6 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    6, true);
    
    /* To keep the test simple yet flexible, make processes with MCW
     * rank >= 6 wait in a barrier and then finalize
     */
    mcw = MPI.COMM_WORLD.split((me < 6 ? 1 : 0), 0);
    tasks = mcw.getSize();
    if (me >= 6) {
      MPI.COMM_WORLD.barrier();
      MPI.Finalize();
      System.exit(0);
    }
    
    dims[0] = 2;
    dims[1] = 3;
    periods[0] = false;
    periods[1] = false;
    comm = ((Intracomm)mcw).createCart(dims, periods, false);
    comms[cnt++] = comm;
    
    remain[0] = false;
    remain[1] = true;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != 3)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be 3\n");
    rank = subcomm.getRank();
    if (rank != me % 3)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " + me + "\n");
    
    remain[0] = false;
    remain[1] = false;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != 1)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be 1\n");
    rank = subcomm.getRank();
    if (rank != 0)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be 0\n");
    
    remain[0] = true;
    remain[1] = true;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != tasks)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be " + tasks + "\n");
    rank = subcomm.getRank();
    if (rank != me)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " + me + "\n");
    
    remain[0] = true;
    remain[1] = false;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != 2)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be 2\n");
    rank = subcomm.getRank();
    if (rank != me / 3)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " + (me / 3) + "\n");
    
    mcw.barrier();
    for (int i = 0; i < cnt; i++) {
      comms[i].free();
    }
    mcw.free();
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
