/* 
 *
 * This file is a port from "bsend_free.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: BsendFree.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class BsendFree
{
  private final static int ITER = 2 ;
  private final static int SIZE = 1000000;
  private final static int SMALL = 2000;

  public static void main (String args[]) throws MPIException
  {
    int me;
    int pair_lo, pair_hi;
    int datain[] = new int[SMALL];
    IntBuffer dataout = MPI.newIntBuffer(SMALL);
    byte buf[] = new byte[SIZE];
    Status status;
    Request request;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    pair_lo = (me / 2) * 2;
    pair_hi = ((me / 2) * 2) + 1;

    /* Clear the buffer to prevent unitialized reads */
    
    for (int i = 0; i < SMALL; i++)
      dataout.put(i, i);
    
    MPI.attachBuffer(buf);
    if (me == pair_lo)  {
      for (int i = 0; i < ITER; i++) {
	dataout.put(i, i);
	request = MPI.COMM_WORLD.ibSend(slice(dataout, i), SMALL - 10,
					MPI.INT, pair_hi, 0);
	request.free(); 
      }
      /* This test relies on progress occurring. If you have a
         transport where MPI_Recv doesn't call opal_progress(), this
         could deadlock. */
      MPI.COMM_WORLD.barrier ();
    } else if (me == pair_hi) {
      /* This test relies on progress occurring. If you have a
         transport where MPI_Recv doesn't call opal_progress(), this
         could deadlock. */
      MPI.COMM_WORLD.barrier ();
      for (int i = 0; i < ITER; i++) {
	status = MPI.COMM_WORLD.recv(datain, SMALL - 10, MPI.INT,
				     pair_lo, 0);
	if (datain[0] != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR " + i + " " +
				      datain[0] + "\n");
      }
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
