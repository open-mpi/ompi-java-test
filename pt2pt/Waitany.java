/* 
 *
 * This file is a port from "waitany.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Waitany.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Waitany
{
  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    MPI.Init(args);
    mainStatus();
    mainNoStatus();
    MPI.Finalize();
  }
  
  private static void mainStatus() throws MPIException, InterruptedException
  {
    int tasks, index;
    int me[] = new int[1];
    IntBuffer data = MPI.newIntBuffer(2000);
    Request req[];
    Status status;
    
    me[0] = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[2000];
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    if (me[0] != 0)
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      status = Request.waitAnyStatus(new Request[0]);
      if (status.getIndex() != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
      
      for (int i = 1; i < tasks; i++) {
	status = Request.waitAnyStatus(Arrays.copyOf(req, tasks));
	index = status.getIndex();
	if (index == MPI.UNDEFINED)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: index " +
				      "= MPI_UNDEFINED\n");
	if (!req[index].isNull())
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: reqest " +
				      "not set to NULL\n");
	if (data.get(index) != index)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: wrong " +
				      "data\n");
      }
      
      status = Request.waitAnyStatus(Arrays.copyOf(req, tasks));
      if (status.getIndex() != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    status = Request.waitAnyStatus(new Request[0]);
    if (status.getIndex() != MPI.UNDEFINED) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
    }
    
    MPI.COMM_WORLD.barrier();
  }
  
  private static void mainNoStatus() throws MPIException, InterruptedException
  {
    int tasks, index;
    int me[] = new int[1];
    IntBuffer data = MPI.newIntBuffer(2000);
    Request req[];
    
    me[0] = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[2000];
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    if (me[0] != 0)
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      index = Request.waitAny(new Request[0]);
      if (index != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
      
      for (int i = 1; i < tasks; i++) {
	index = Request.waitAny(Arrays.copyOf(req, tasks));
	if (index == MPI.UNDEFINED)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: index " +
				      "= MPI_UNDEFINED\n");
	if (!req[index].isNull())
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: reqest " +
				      "not set to NULL\n");
	if (data.get(index) != index)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Waitany: wrong " +
				      "data\n");
      }
      
      index = Request.waitAny(Arrays.copyOf(req, tasks));
      if (index != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    index = Request.waitAny(new Request[0]);
    if (index != MPI.UNDEFINED) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitany: index " +
				    "not = MPI_UNDEFINED\n");
    }
    
    MPI.COMM_WORLD.barrier();
  }
}
