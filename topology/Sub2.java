/* 
 *
 * This file is a port from "sub2.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Sub2.java			Author: S. Gross
 *
 */

import mpi.*;

public class Sub2
{
  /* "MAXDIMS = 10;" and "MAXCOMMS = 20;" in the original C file */
  private final static int MAXDIMS = 2;
  private final static int MAXCOMMS = 20;

  public static void main (String args[]) throws MPIException
  {
    int me, tasks, size, rank, ndims;
    int cnt = 0;
    int dims[] = new int[MAXDIMS];
    boolean periods[] = new boolean[MAXDIMS],
	    remain[] = new boolean[MAXDIMS];
    CartParms topoParams;
    Comm parent;
    CartComm comm, subcomm;
    CartComm comms[];


    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    comms = new CartComm[MAXCOMMS];
    
    /* We need at least 4 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    4, true);
    
    /* To keep the test simple yet flexible, if (tasks % 2) == 1, make
     * the last process go block in an MPI_Barrier and not take part
     * in this test.
     */
    if (tasks % 2 == 1) {
      parent = MPI.COMM_WORLD.split((me != tasks - 1 ? 1 : 0), 0);
    } else {
      parent = MPI.COMM_WORLD;
    }
    tasks = parent.getSize();
    if (tasks == 1) {
      MPI.COMM_WORLD.barrier();
      MPI.Finalize();
      System.exit(0);
    }
    
    /* At this point, we know that tasks is even */
    dims[0] = 2;
    dims[1] = tasks / 2;
    periods[0] = true;
    periods[1] = false;
    comm = ((Intracomm)parent).createCart(dims, periods, false);
    comms[cnt++] = comm;
    
    /* Keep the 2nd dimension */
    remain[0] = false;
    remain[1] = true;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != tasks / 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be " +
				  (tasks / 2) + "\n");
    }
    rank = subcomm.getRank();
    if (rank != me % (tasks / 2)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " +
				  (me % (tasks / 2)) + "\n");
    }
    /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
     * MPI_Cart_get()
     *
     * First call MPI_Cartdim_get(subcomm, &ndims);
     */
    topoParams = subcomm.getTopo();
    ndims = topoParams.getDimCount();
    if (1 != ndims) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cartdim_get: ndims = " +
				  ndims + ", should be 1\n");
    }
    /* Second call to 
     * MPI_Cart_get(subcomm, ndims, dims_out, periods_out, coords_out);
     * which isn't necessary, because "getTopo()" determined all
     * values.
     */
    if (topoParams.getDim(0) != dims[1]) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get: dims[0] = " +
				  topoParams.getDim(0) + ", should be " +
				  dims[1] + "\n");
    }
    if (topoParams.getPeriod(0) != periods[1]) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get: periods[0] = " +
				  topoParams.getPeriod(0) + ", should be " +
				  periods[1] + "\n");
    }
    
    /* Keep the 1st dimension */
    remain[0] = true;
    remain[1] = false;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be 2\n");
    }
    rank = subcomm.getRank();
    if (rank != ((me < tasks / 2) ? 0 : 1)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " +
				  ((me < tasks / 2) ? 0 : 1) + "\n");
    }
    /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
     * MPI_Cart_get()
     *
     * First call MPI_Cartdim_get(subcomm, &ndims);
     */
    topoParams = subcomm.getTopo();
    ndims = topoParams.getDimCount();
    if (1 != ndims) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cartdim_get: ndims = " +
				  ndims + ", should be 1\n");
    }
    /* Second call to 
     * MPI_Cart_get(subcomm, ndims, dims_out, periods_out, coords_out);
     * which isn't necessary, because "getTopo()" determined all
     * values.
     */
    if (topoParams.getDim(0) != dims[0]) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get: dims[0] = " +
				  topoParams.getDim(0) + ", should be " +
				  dims[0] + "\n");
    }
    if (topoParams.getPeriod(0) != periods[0]) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get: periods[0] = " +
				  topoParams.getPeriod(0) + ", should be " +
				  periods[0] + "\n");
    }
    
    /* Keep neither dimension -- Per MPI-2.2, get a zero-dimension
     * communicator back
     */
    remain[0] = false;
    remain[1] = false;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != 1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be 1\n");
    }
    rank = subcomm.getRank();
    if (rank != 0) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be 0\n");
    }
    /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
     * MPI_Cart_get()
     *
     * First call MPI_Cartdim_get(subcomm, &ndims);
     */
    topoParams = subcomm.getTopo();
    ndims = topoParams.getDimCount();
    if (0 != ndims) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cartdim_get: ndims = " +
				  ndims + ", should be 0\n");
    }
    
    /* Keep both dimensions; should effective be a dup of the original
     * communicator
     */
    remain[0] = true;
    remain[1] = true;
    subcomm = comm.sub(remain);
    comms[cnt++] = subcomm;
    size = subcomm.getSize();
    if (size != tasks) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: size = " +
				  size + ", should be " +
				  tasks + "\n");
    }
    rank = subcomm.getRank();
    if (rank != me) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_sub: rank = " +
				  rank + ", should be " + me + "\n");
    }
    /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
     * MPI_Cart_get()
     *
     * First call MPI_Cartdim_get(subcomm, &ndims);
     */
    topoParams = subcomm.getTopo();
    ndims = topoParams.getDimCount();
    if (2 != ndims) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cartdim_get: ndims = " +
				  ndims + ", should be 2\n");
    }
    /* Second call to 
     * MPI_Cart_get(subcomm, ndims, dims_out, periods_out, coords_out);
     * which isn't necessary, because "getTopo()" determined all
     * values.
     */
    for (int i = 0; i < ndims; ++i) {
      if (topoParams.getDim(i) != dims[i]) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cart_get: dims[" + i +
				    "] = " + topoParams.getDim(i) +
				    ", should be " + dims[i] + "\n");
      }
      if (topoParams.getPeriod(i) != periods[i]) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cart_get: periods[" +
				    i + "] = " + topoParams.getPeriod(i) +
				    ", should be " + periods[i] + "\n");
      }
    }
    
    /* All done */
    parent.barrier();
    for (int i = 0; i < cnt; i++) {
      comms[i].free();
    }
    if (MPI.COMM_WORLD != parent) {
      parent.free();
    }
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
