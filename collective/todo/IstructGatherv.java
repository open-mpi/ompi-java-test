/* 
 *
 * This file is a port from "istruct_gatherv.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IstructGatherv.java			Author: S. Gross
 *
 */

import mpi.*;

public class IstructGatherv
{
  private final static int STRIDE = 13;
  private final static int SIZEOF_INT = 4;

  public static void main (String args[]) throws MPIException
  {
    int myrank, gsize, root = 0;
    // make Java happy and initialize everything
    int rbuf[] = new int[0],
	displs[] = new int[0],
	rcounts[] = new int[0],
	sendarray[];
    //    MPI_Aint disp[2];
    int blocklen[] = new int[2],
	disp[] = new int[2],
	num[] = new int[1];
    Datatype type[] = new Datatype[2];
    Datatype stype;
    Request request;
    
    MPI.Init(args);
    myrank = MPI.COMM_WORLD.getRank();
    gsize = MPI.COMM_WORLD.getSize();
    
    /* allocate the structure to send */
    sendarray = new int[gsize * STRIDE];
    /* initialize the structure */
    num[0] = myrank + 1;
    for(int i = 0; i < gsize*STRIDE; i++)
      sendarray[i] = -1;
    for(int i = 0; i < num[0]; i++)
      sendarray[STRIDE * i] = myrank;

    /* allocate displs and rcounts on the root process */
    if(myrank == root) {
      displs  = new int[gsize];
      rcounts = new int[gsize];
    }
    
    /* gather the counts */
    request = MPI.COMM_WORLD.iGather(num, 1, MPI.INT, rcounts, 1,
				     MPI.INT, root);
    request.waitFor();
    request.free();
    
    /* compute displacements and allocate receive buffer on the root
     * process
     */
    if(myrank == root) {
      displs[0] = 0;
      for(int i = 1; i < gsize; i++) { 
	displs[i] = displs[i-1] + rcounts[i-1];
      }
      /* the total number expected is
       * (displs[gsize-1]+rcounts[gsize-1])
       */
      rbuf = new int[displs[gsize-1] + rcounts[gsize-1]];
    }
    /* form a type to describe the send structure */
    blocklen[0] = 1;
    disp[0] = 0;
    type[0] = MPI.INT;
    blocklen[1] = 1;
    disp[1] = STRIDE * SIZEOF_INT;
    type[1] = MPI.UB;
    stype = Datatype.createStruct(blocklen, disp, type);
    stype.commit();
    
    /* gatherv */
    request = MPI.COMM_WORLD.iGatherv(sendarray, num[0], stype, rbuf,
				      rcounts, displs, MPI.INT, root);
    request.waitFor();
    request.free();
    
    /* check data on root process */
    if(myrank == 0) {
      for(int t = 0, i = 0; i < gsize; i++)
	for(int j = 0; j < i+1; j++, t++) {
	  if(rbuf[t] != i) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR: rbuf[" + t + "] = " +
					rbuf[t] + " instead of " + i +
					"\n");
	  }
	}
    }
    
    stype.free();
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
