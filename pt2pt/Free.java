/* 
 *
 * This file is a port from "free.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Free.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Free
{
  private final static int ITER = 100;

  public static void main (String args[]) throws MPIException
  {
    int me, rc, errorcodeClass;
    
    IntBuffer datain  = MPI.newIntBuffer(1),
              dataout = MPI.newIntBuffer(1);
    
    Request request;
    Prequest prequest;
    Comm comm;
    int pair_lo, pair_hi;

    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.getRank();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    pair_lo = (me / 2) * 2;
    pair_hi = ((me / 2) * 2) + 1;
    
    if (me == pair_lo)  {
      for (int i = 0; i < ITER; i++)  {
	dataout.put(0, i);
	request = comm.iSend(dataout, 1, MPI.INT, pair_hi, 0);
	request.free();
	if (!request.isNull()) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Request_free, " +
				    "request not set to NULL\n");
	comm.recv(datain, 1, MPI.INT, pair_hi, 0);
	if (datain.get(0) != i)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Recv, datain = " +
				    datain.get(0) + ", should be " + i + "\n");
      }
    } else if (me == pair_hi) {
      comm.recv(datain, 1, MPI.INT, pair_lo, 0);
      for (int i = 0; i < ITER - 1; i++)  {
	dataout.put(0, i);
	request = comm.iSend(dataout, 1, MPI.INT, pair_lo, 0);
	request.free();
	if (!request.isNull()) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Request_free, " +
				    "request not set to NULL\n");
	comm.recv(datain, 1, MPI.INT, pair_lo, 0);
      }
      dataout.put(0, ITER - 1);
      comm.send (dataout, 1, MPI.INT, pair_lo, 0);
    }

    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    
    /* The standard allows MPI_Request_free; in fact, this test MUST 
       be successful (see the discussion of MPI_Request_free in the standard)
       In fact, the example makes the use that is tested here. */
    request = MPI.COMM_WORLD.iRecv (datain, 1, MPI.INT, me, 0);

    try {
      request.free();
    }
    catch (MPIException ex)
    {
      errorcodeClass = ex.getErrorClass();
      if (errorcodeClass != MPI.SUCCESS)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Attempt to free receive " +
				    "not allowed\n");
    }
    
    prequest = MPI.COMM_WORLD.recvInit (dataout, 1, MPI.INT, me, 0);
    try {
      prequest.free();
    }
    catch (MPIException ex)
    {
      rc = ex.getErrorCode();
      if (rc != MPI.SUCCESS)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: not able to free " +
				    "inactive receive request\n");
    }

    prequest = MPI.COMM_WORLD.recvInit (dataout, 1, MPI.INT, me, 2);
    prequest.start();
    try {
      prequest.free();
    }
    catch (MPIException ex)
    {
      errorcodeClass = ex.getErrorClass();
      if (errorcodeClass != MPI.SUCCESS)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Attempt to free active " +
				    "persistent receive not allowed\n");
    }
    
    MPI.COMM_WORLD.send (dataout, 1, MPI.INT, me, 0);
    MPI.COMM_WORLD.send (dataout, 1, MPI.INT, me, 2);
    
    comm.barrier ();
    MPI.Finalize();
  }
}
