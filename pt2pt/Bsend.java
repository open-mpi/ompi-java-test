/* 
 *
 * This file is a port from "bsend.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Bsend.java			Author: S. Gross
 *
 */

import mpi.*;

public class Bsend
{
  private final static int BIGSIZE = 100000;
  private final static int SMALLSIZE = 1000;

  public static void main (String args[]) throws MPIException
  {
    int me;
    int pair_lo, pair_hi;
    byte oldbuf[],
      data[] = new byte[BIGSIZE + MPI.BSEND_OVERHEAD],
      buf1[] = new byte[SMALLSIZE + MPI.BSEND_OVERHEAD],
      buf2[] = new byte[2 * BIGSIZE + MPI.BSEND_OVERHEAD];
    Status status;
    
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
    
    if (me == pair_lo) {
      
      /* First test */
      
      MPI.attachBuffer(buf1);
      for (int i = 0; i < BIGSIZE; ++i) {
	data[i] = 0;
      }
      MPI.COMM_WORLD.bSend(data, SMALLSIZE, MPI.BYTE, pair_hi, 1);
      oldbuf = MPI.detachBuffer();
      if (oldbuf != buf1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Buffer_detach, " +
				    "wrong buffer returned\n");
      if (oldbuf.length != (SMALLSIZE + MPI.BSEND_OVERHEAD))
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Buffer_detach, " +
				    "wrong size returned\n");
      
      MPI.attachBuffer(buf2);
      MPI.COMM_WORLD.bSend(data, SMALLSIZE + 1, MPI.BYTE, pair_hi, 1);
      
      /* End of first test */
      
      MPI.COMM_WORLD.barrier ();
      
      /* Second test */ 
      /* test to see if large array is REALLY being buffered */
      
      for (int i = 0; i < BIGSIZE; ++i)
	data[i] = 1;
      MPI.COMM_WORLD.bSend(data, BIGSIZE, MPI.BYTE, pair_hi, 1);
      status = MPI.COMM_WORLD.recv(data, BIGSIZE, MPI.BYTE, pair_hi, 2);
      for (int i = 0; i < BIGSIZE; i++)
	if (data[i] != 2)  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR, incorrect data value, " +
				      "task 0 [position " + i +
				      " expect " + 2 + " get " +
				      data[i] + "]\n");
      oldbuf = MPI.detachBuffer();
    } else if (me == pair_hi) {
      status = MPI.COMM_WORLD.recv(data, SMALLSIZE, MPI.BYTE,
				   pair_lo, 1);
      status = MPI.COMM_WORLD.recv(data, SMALLSIZE + 1, MPI.BYTE,
				   pair_lo, 1);
      
      MPI.COMM_WORLD.barrier ();
      
      /* Second test */
      /* test to see if large array is REALLY being buffered */
      
      MPI.attachBuffer(buf2);
      for (int i = 0; i < BIGSIZE; ++i)
	data[i] = 2;
      MPI.COMM_WORLD.bSend(data, BIGSIZE, MPI.BYTE, pair_lo, 2);
      status = MPI.COMM_WORLD.recv(data, BIGSIZE, MPI.BYTE, pair_lo, 1);
      for (int i = 0; i < BIGSIZE; i++)
	if (data[i] != 1)  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR, incorrect data value, " +
				      "task 1 [position " + i +
				      " expect " + 1 + " get " +
				      data[i] + "]\n");
      oldbuf = MPI.detachBuffer();
    }
    
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
