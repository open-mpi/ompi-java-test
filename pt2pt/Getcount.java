/* 
 *
 * This file is a port from "getcount.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Getcount.java			Author: S. Gross
 *
 */

import mpi.*;

public class Getcount
{
  public static void main (String args[]) throws MPIException
  {
    int me, count;
    int data[] = new int[100];
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    
    /* Clear so we can bcheck clean */
    for (int i = 0; i < 100; ++i) {
      data[i] = 0;
    }
    
    if(me == 0)  {
      MPI.COMM_WORLD.send (data, 5, MPI.BYTE, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.CHAR, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.INT, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.FLOAT, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.DOUBLE, 1, 1);
      /*      MPI.COMM_WORLD.send (data, 0, 5, MPI.LONG_DOUBLE, 1, 1);*/
      MPI.COMM_WORLD.send (data, 5, MPI.SHORT, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.LONG, 1, 1);
      MPI.COMM_WORLD.send (data, 5, MPI.PACKED, 1, 1);
      /*
      MPI.COMM_WORLD.send (data, 0, 5, MPI.UNSIGNED_CHAR, 1, 1);
      MPI.COMM_WORLD.send (data, 0, 5, MPI.UNSIGNED_SHORT, 1, 1);
      MPI.COMM_WORLD.send (data, 0, 5, MPI.UNSIGNED, 1, 1);
      MPI.COMM_WORLD.send (data, 0, 5, MPI.UNSIGNED_LONG, 1, 1);
      */
    } else if(me == 1)  {
      status = MPI.COMM_WORLD.recv(data, 5, MPI.BYTE, 0, 1);
      count = status.getCount(MPI.BYTE);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.CHAR, 0, 1);
      count = status.getCount(MPI.CHAR);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.INT, 0, 1);
      count = status.getCount(MPI.INT);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.FLOAT, 0, 1);
      count = status.getCount(MPI.FLOAT);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.DOUBLE, 0, 1);
      count = status.getCount(MPI.DOUBLE);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      /*
      status = MPI.COMM_WORLD.Recv(data, 0, 5, MPI.LONG_DOUBLE, 0, 1);
      count = status.Get_count(MPI.LONG_DOUBLE);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      */
      status = MPI.COMM_WORLD.recv(data, 5, MPI.SHORT, 0, 1);
      count = status.getCount(MPI.SHORT);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.LONG, 0, 1);
      count = status.getCount(MPI.LONG);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.recv(data, 5, MPI.PACKED, 0, 1);
      count = status.getCount(MPI.PACKED);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      /*
      status = MPI.COMM_WORLD.Recv(data, 0, 5, MPI.UNSIGNED_CHAR, 0, 1);
      count = status.Get_count(MPI.UNSIGNED_CHAR);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.Recv(data, 0, 5, MPI.UNSIGNED_SHORT, 0, 1);
      count = status.Get_count(MPI.UNSIGNED_SHORT);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.Recv(data, 0, 5, MPI.UNSIGNED, 0, 1);
      count = status.Get_count(MPI.UNSIGNED);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      status = MPI.COMM_WORLD.Recv(data, 0, 5, MPI.UNSIGNED_LONG, 0, 1);
      count = status.Get_count(MPI.UNSIGNED_LONG);
      if(count != 5) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Get_count, count = " +
				    count + " should be 5\n");
      */
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
