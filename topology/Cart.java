/* 
 *
 * This file is a port from "cart.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Cart.java			Author: S. Gross
 *
 */

import mpi.*;

public class Cart
{
  /* "MAXDIMS = 10;" and "MAXCOMMS = 20;" in the original C file */
  private final static int MAXDIMS = 2;
  private final static int MAXCOMMS = 20;

  public static void main (String args[]) throws MPIException
  {
    int rank, tasks;
    Comm only6;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* We need at least 6 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    6, true);
    
    /* To keep the test simple yet flexible, make processes with MCW
     * rank >= 6 wait in a barrier and then finalize
     */
    only6 = MPI.COMM_WORLD.split((rank < 6 ? 1 : 0), 0);
    if (rank < 6) {
      cart_test1(only6);
      if (((MPI.VERSION == 2) && (MPI.SUBVERSION >= 1)) ||
	  (MPI.VERSION > 2)) {
	/* MPI-2.1 allows for 0-dimensional cartesian comms */
	cart_test2(only6);
      }
      cart_test3(only6);
    }
    MPI.COMM_WORLD.barrier();
    only6.free();
    MPI.Finalize();
  }
  
  
  
  static void cart_test1(Comm input_comm) throws MPIException
  {
    int tasks, me, type, ndims, rank, src, dest;
    int cnt = 0;
    int dims[] = new int[MAXDIMS],
	coords[] = new int[MAXDIMS];
    boolean periods[] = new boolean[MAXDIMS];
    Group gid;
    CartParms topoParams;
    ShiftParms shiftParams;
    CartComm comm;
    CartComm comms[] = new CartComm[MAXCOMMS];
   
    gid = input_comm.getGroup();
    tasks = gid.getSize();
    
    /* test non-periodic topology */
    dims[0] = 0;
    dims[1] = 0;
    CartComm.createDims(tasks, dims);
    if (dims[0] != 3 || dims[1] != 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Dims_create, dims = (" +
				  dims[0] + "," + dims[1] +
				  "), should be (3,2)\n");
    }
    periods[0] = false;
    periods[1] = false;
    comm = ((Intracomm)input_comm).createCart(dims, periods, false);
    comms[cnt++] = comm;
    me = comm.getRank();

    type = comm.getTopology();
    if (type != MPI.CART) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Topo_test, type = " +
				  type + ", should be " + MPI.CART + "\n");
    }
    
    /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
     * MPI_Cart_get()
     *
     * First call MPI_Cartdim_get(comm, &ndims);
     */
    topoParams = comm.getTopo();
    ndims = topoParams.getDimCount();
    if (ndims != 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cartdim_get, ndims = " +
				  ndims + ", should be 2\n");
    }
    
    /* Second call to 
     * MPI_Cart_get(comm, MAXDIMS, dims, periods, coords);
     * which isn't necessary, because "getTopo()" determined all
     * values.
     */
    if ((topoParams.getDim(0) != 3) || (topoParams.getDim(1) != 2)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get, dims = (" +
				  topoParams.getDim(0) + "," +
				  topoParams.getDim(1) +
				  ", should be (3,2)\n");
    }
    if ((topoParams.getPeriod(0) != false) ||
	(topoParams.getPeriod(1) != false)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "WRONG PERIODS!\n");
    }
    coords[0] = topoParams.getCoord(0);
    coords[1] = topoParams.getCoord(1);
    if ((coords[0] != (me / 2)) || (coords[1] != (me % 2))) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_get, coords = (" +
				  coords[0] + "," + coords[1] +
				  "), should be (" + (me / 2) + "," +
				  (me % 2) + ")\n");
    }

    rank = comm.getRank(coords);
    if (rank != me) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_rank, rank = " +
				  rank + ", should be " + me + "\n");
    }

    coords = comm.getCoords(rank);
    if (coords[0] != me / 2 || coords[1] != me % 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_coords, coords = (" +
				  coords[0] + "," + coords[1] +
				  "), should be (" + (me / 2) + "," +
				  (me % 2) + ")\n");
    }

    shiftParams = comm.shift(0, 5);
    if ((shiftParams.getRankSource() != MPI.PROC_NULL) ||
	(shiftParams.getRankDest() != MPI.PROC_NULL)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src/dest = (" +
				  shiftParams.getRankSource() + "," +
				  shiftParams.getRankDest() +
				  "), should be (" +
				  MPI.PROC_NULL + "," + MPI.PROC_NULL +
				  ")\n");
    }
    
    shiftParams = comm.shift(0, 1);
    if (me / 2 < 2 && shiftParams.getRankDest() != me + 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, dest = " +
				  shiftParams.getRankDest() +
				  ", should be " + (me + 2) + "\n");
    }
    
    if (me / 2 > 0 && shiftParams.getRankSource() != me - 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src = " +
				  shiftParams.getRankSource() +
				  ", should be " + (me - 2) + "\n");
    }
    
    shiftParams = comm.shift(1, -1);
    if ((me % 2) != 0 && shiftParams.getRankDest() != me - 1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, dest = " +
				  shiftParams.getRankDest() +
				  ", should be " + (me - 1) + "\n");
    }
    if ((me % 2) != 0 && shiftParams.getRankSource() != MPI.PROC_NULL) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src = " +
				  shiftParams.getRankSource() +
				  ", should be " + MPI.PROC_NULL +
				  "\n");
    }
    if (me % 2 == 0 && shiftParams.getRankSource() != me + 1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src = " +
				  shiftParams.getRankSource() +
				  ", should be " + (me + 1) + "\n");
    }
    if (me % 2 == 0 && shiftParams.getRankDest() != MPI.PROC_NULL) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, dest = " +
				  shiftParams.getRankDest() +
				  ", should be " + MPI. PROC_NULL +
				  "\n");
    }
    
    /* test periodic topology */
    dims[0] = 2;
    dims[1] = 0;
    CartComm.createDims(tasks, dims);
    if (dims[0] != 2 || dims[1] != 3) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Dims_create, dims = (" +
				  dims[0] + "," + dims[1] +
				  "), should be (2,3)\n");
    }
    
    periods[0] = true;
    periods[1] = true;
    comm = ((Intracomm)input_comm).createCart(dims, periods, false);
    comms[cnt++] = comm;
    me = comm.getRank();
    coords[0] = me / 3;
    coords[1] = me % 3;
    rank = comm.getRank(coords);
    if (rank != me) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_rank, rank = " +
				  rank + ", should be " + me + "\n");
    }
    
    coords = comm.getCoords(rank);
    if (coords[0] != me / 3 || coords[1] != me % 3) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_coords, coords = (" +
				  coords[0] + "," + coords[1] +
				  "), should be (" + (me / 3) + "," +
				  (me % 3) + ")\n");
    }
    
    shiftParams = comm.shift(0, 5);
    if (shiftParams.getRankSource() != (me + 3) % 6 ||
	shiftParams.getRankDest() != (me + 3) % 6) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src/dest = (" +
				  shiftParams.getRankSource() + "," +
				  shiftParams.getRankDest() +
				  "), should be (" +
				  (me + 3) + "," + (me + 3) + ")\n");
    }
    
    shiftParams = comm.shift(1, -1);
    if (shiftParams.getRankDest() !=
	(me - 1) + 3 * ((me % 3 == 0) ? 1 : 0)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, dest = " +
				  shiftParams.getRankDest() +
				  ", should be " +
				  ((me - 1 + 3) % 3) + "\n");
    }
    if (shiftParams.getRankSource() !=
	(me + 1) - 3 * ((me % 3 == 2) ? 1 : 0)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_shift, src = " +
				  shiftParams.getRankSource() +
				  ", should be " +
				  ((me + 1 + 3) % 3) + "\n");
    }
    
    dims[0] = 1;
    comm = ((Intracomm)input_comm).createCart(dims, periods, false);
    comms[cnt++] = comm;
    
    input_comm.barrier();
    
    for (int i = 0; i < cnt; i++) {
      if (!comms[i].isNull()) {
	comms[i].free();
      }
    }
    gid.free();
  }



  static void cart_test2(Comm input_comm) throws MPIException
  {
    /* MPI-2.1 allows for 0-dimensional cartesian comms */
    int type, rank, ndims;
    int coords[] = new int[1];
    boolean remain_dims[] = new boolean[4],
	    periods[] = new boolean[1];
    CartParms topoParams;
    CartComm comm, comm2;
    
    /* input_comm rank 0 should get a size 1 comm; all others should
     * get MPI_COMM_NULL
     */
    rank = input_comm.getRank();
    comm = ((Intracomm)input_comm).createCart(new int[0], new boolean[0], false);
    if (0 == rank && comm.isNull()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_create; did not"+
				  "make 0-dim communicator\n");
    } else if (0 != rank && !comm.isNull()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_create; non-rank-0 " +
				  "got something other than " +
				  "MPI_COMM_NULL\n");
    }
    
    if (0 == rank) {
      /* Should have a cartesian topology */
      type = comm.getTopology();
      if (type != MPI.CART) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Topo_test, type = " +
				    type + ", should be " + MPI.CART +
				    " (CART)\n");
      }
      
      /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
       * MPI_Cart_get()
       *
       * First call MPI_Cartdim_get(comm, &ndims);
       */
      topoParams = comm.getTopo();
      /* Should return ndims == 0 */
      ndims = topoParams.getDimCount();
      if (0 != ndims) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cartdim_get; didn't "+
				    "get 0 back (got " + ndims +
				    " instead)\n");
      }
      
      /* Should return 0 in ndims, and leave periods and coords
       * alone
       */
      /* Not possible in Java, because "period" is boolean and 
       * "periods" and "coords" are private variables of class
       * CartParms.java, which can only be accessed via get-methods
       *
       *      periods = 37;
       *      coords = 92;
       *      MPI_Cart_get(comm, 1, &ndims, &periods, &coords);
       *      if (0 != ndims) {
       *	OmpitestError.ompitestError(OmpitestError.getFileName(),
       *				    OmpitestError.getLineNumber(),
       *				    "ERROR in MPI_Cart_get; didn't get " +
       *				    "0==ndims back (got " + ndims +
       *				    " instead)\n");
       *      } else if (37 != periods || 92 != coords) {
       *	OmpitestError.ompitestError(OmpitestError.getFileName(),
       *				    OmpitestError.getLineNumber(),
       *				    "ERROR in MPI_Cart_get; didn't " +
       *				    "leave periods and coords " +
       *				    "arguments alone\n");
       *      }
       */
      
      /* Should return rank of 0 */
      rank = comm.getRank(new int[0]);
      if (0 != rank) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cart_rank; didn't " +
				    "get 0 back (got " + rank +
				    " instead)\n");
      }
      
      /* Should leave coords argument alone */
      /* Not possible in Java, because "coords" is a private variable
       * of class CartParms.java, which can only be accessed via
       * get-methods and "getCoords()" will return the coordinates
       * of a process rank and therefore overwrite a local value.
       *
       *      coords = 17;
       *      MPI_Cart_coords(comm, 0, 1, &coords);
       *      if (17 != coords) {
       *	OmpitestError.ompitestError(OmpitestError.getFileName(),
       *				    OmpitestError.getLineNumber(),
       *				    "ERROR in MPI_Cart_coords; didn't " +
       *				    "leave coords arguments alone\n");
       *      }
       */
      
      /* Calling cart_sub on a 0-dim cart comm should result in
       * another 0-dim cart comm
       */
      remain_dims[0] = false;
      remain_dims[1] = false;
      remain_dims[2] = false;
      remain_dims[3] = false;
      comm2 = comm.sub(remain_dims);
      type = comm2.getTopology();
      if (type != MPI.CART) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Topo_test, type = " +
				    type + ", should be " + MPI.CART +
				    " (CART)\n");
      }
      
      /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
       * MPI_Cart_get()
       *
       * First call MPI_Cartdim_get(comm, &ndims);
       */
      topoParams = comm2.getTopo();
       /* Should return ndims == 0 */
      ndims = topoParams.getDimCount();
      if (0 != ndims) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cartdim_get; didn't " +
				    "get 0 back (got " + ndims +
				    " instead)\n");
      }
      
      comm2.free();
      comm.free();
    }
    
    /* Now make a non-zero-dim cart comm and do a cart_sub with
     * remain_dims all equal to false, so we should get a 0 dim
     * cart comm back
     */
    {
      int size;
      int dims[] = new int[MAXDIMS];
      CartComm comm3;
      
      dims[0] = 0;
      dims[1] = 0;
      size = input_comm.getSize();
      CartComm.createDims(size, dims);
      periods = new boolean[MAXDIMS];
      periods[0] = false;
      periods[1] = false;
      comm = ((Intracomm)input_comm).createCart(dims, periods, false);
      remain_dims[0] = false;
      remain_dims[1] = false;
      remain_dims[2] = false;
      remain_dims[3] = false;
      comm2 = comm.sub(remain_dims);
      if (comm2.isNull()) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cart_create; did " +
				    "not make 0-dim communicator\n");
      }
      
      /* Should be a cart comm */
      type = comm2.getTopology();
      if (type != MPI.CART) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Topo_test, type = " +
				    type + ", should be " + MPI.CART +
				    " (CART)\n");
      }
      
      /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
       * MPI_Cart_get()
       *
       * First call MPI_Cartdim_get(comm, &ndims);
       */
      topoParams = comm2.getTopo();
      /* Should return ndims == 0 */
      ndims = topoParams.getDimCount();
      if (0 != ndims) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cartdim_get; didn't " +
				    "get 0 back (got " + ndims +
				    " instead)\n");
      }
      
      /* Try to dup the comm */
      comm3 = comm2.clone();
      
      /* Should be a cart comm */
      type = comm3.getTopology();
      if (type != MPI.CART) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Topo_test, type = " +
				    type + ", should be " + MPI.CART +
				    " (CART)\n");
      }
      
      /* Java uses Cartcomm.getTopo() for MPI_Cartdim_get() and
       * MPI_Cart_get()
       *
       * First call MPI_Cartdim_get(comm, &ndims);
       */
      topoParams = comm3.getTopo();
      /* Should return ndims == 0 */
      ndims = topoParams.getDimCount();
      if (0 != ndims) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Cartdim_get; didn't " +
				    "get 0 back (got " + ndims +
				    " instead)\n");
      }
      
      comm2.free();
      comm.free();
    }
  }



  /* Test to ensure periodic cart comms work properly */
  static void cart_test3(Comm input_comm) throws MPIException
  {
    final int DIMS = 2;

    int rank, size, gsize, grank, uprank, dowrank;
    int dimlens[] = new int[DIMS],
	mycs[] = new int[DIMS],
	upcs[] = new int[DIMS],
	dowcs[] = new int[DIMS];
    boolean wrap[] = new boolean[DIMS];
    boolean reorder;
    CartComm gcomm;
    
    rank = input_comm.getRank();
    size = input_comm.getSize();
    
    dimlens[0] = size;
    dimlens[1] = 1;
    
    wrap[0] = true;
    wrap[1] = true;
    reorder = false;
    
    gcomm = ((Intracomm)input_comm).createCart(dimlens, wrap, reorder);
    grank = gcomm.getRank();
    gsize = gcomm.getSize();
    mycs = gcomm.getCoords(grank);
    
    upcs[0] = mycs[0] - 1;
    upcs[1] = mycs[1];
    
    dowcs[0] = mycs[0] + 1;
    dowcs[1] = mycs[1];

    uprank = gcomm.getRank(upcs);
    if (uprank != (rank + size - 1) % size) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_rank: should have " +
				  "gotten " + ((rank + size - 1) % size) +
				  ", but got " + uprank + "\n");
    }
    dowrank = gcomm.getRank(dowcs);
    if (dowrank != (rank + 1) % size) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Cart_rank: should have " +
				  "gotten " + ((rank + 1) % size) +
				  ", but got " + uprank + "\n");
    }
    
    gcomm.free();
  }
}
