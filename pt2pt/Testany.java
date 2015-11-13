/* 
 *
 * This file is a port from "testany.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Testany.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Testany
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
    int tasks, index, done;
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
	req[i - 1] = MPI.COMM_WORLD.iRecv(slice(data, i-1), 1, MPI.INT, i, 1);

      status = Request.testAnyStatus(new Request[0]);
      if (status == null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: flag " +
				    "is not \"true\" (1)\n");
      if (status.getIndex() != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: index " +
				    "not = MPI_UNDEFINED (1)\n");
      
      done = 0;
      while (done < tasks - 1) {
	status = Request.testAnyStatus(Arrays.copyOf(req, tasks - 1));
	if(status != null) {
	  index = status.getIndex();
	  if (index == MPI.UNDEFINED)
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testany: " +
					"index = MPI_UNDEFINED " +
					"(done = " + done + ")\n");
	  else if (!req[index].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testany: " +
					"request not set to NULL " +
					"(done = " + done + ")\n");
	  else if (data.get(index) != index + 1)
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testany: " +
					"wrong data -- " + data.get(index) +
					" != " + (index + 1) + 
					" (index = " + index +
					", done = " + done + ")\n");
	  done++;
	}
      }

      status = Request.testAnyStatus(Arrays.copyOf(req, tasks - 1));
      if(status == null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: flag " +
				    "is not \"true\" (2)\n");
      if (status.getIndex() != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: index " +
				    "not = MPI_UNDEFINED (2)\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    status = Request.testAnyStatus(new Request[0]);
    if(status == null || status.getIndex() != MPI.UNDEFINED) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: flag " +
				    "not \"true\" // index not = " +
				    "MPI_UNDEFINED (2)\n");
    }
    
    MPI.COMM_WORLD.barrier ();
  }

  private static void mainNoStatus() throws MPIException, InterruptedException
  {
    int tasks, index, done;
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
	req[i - 1] = MPI.COMM_WORLD.iRecv(slice(data, i-1), 1, MPI.INT, i, 1);

      index = Request.testAny(new Request[0]);

      if (index != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: index " +
				    "not = MPI_UNDEFINED (1)\n");
      
      done = 0;
      while (done < tasks - 1) {
	index = Request.testAny(Arrays.copyOf(req, tasks - 1));
	if(index != MPI.UNDEFINED) {
	  if (!req[index].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testany: " +
					"request not set to NULL " +
					"(done = " + done + ")\n");
	  else if (data.get(index) != index + 1)
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testany: " +
					"wrong data -- " + data.get(index) +
					" != " + (index + 1) + 
					" (index = " + index +
					", done = " + done + ")\n");
	  done++;
	}
      }

      index = Request.testAny(Arrays.copyOf(req, tasks - 1));

      if (index != MPI.UNDEFINED)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: index " +
				    "not = MPI_UNDEFINED (2)\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    index = Request.testAny(new Request[0]);
    if(index != MPI.UNDEFINED) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: flag " +
				    "not \"true\" // index not = " +
				    "MPI_UNDEFINED (2)\n");
    }
    
    MPI.COMM_WORLD.barrier ();
  }
}
