/* 
 *
 * This file is a port from "waitsome.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Waitsome.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Waitsome
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, done, dataLength = 2000;
    IntBuffer me = MPI.newIntBuffer(1);
    IntBuffer data = MPI.newIntBuffer(dataLength);
    Request req[];
    Status statuses[];
    int index[];

    MPI.Init(args);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[2000];

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    /* first call methods with a status object				*/
    if (me.get(0) != 0) {
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    } else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      statuses = Request.waitSomeStatus(new Request[0]);
      if(statuses != null) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitsome: " +
				    "outcount not = MPI_UNDEFINED\n");
      }

      done = 0;
      while (done < tasks - 1) {
	statuses = Request.waitSomeStatus(Arrays.copyOf(req, tasks));
	if (statuses.length == 0) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in Waitsome: " +
				      "outcount = 0\n");
	}
	for (int i = 0; i < statuses.length; i++) {
	  done++;
	  if (statuses[i].getIndex() == MPI.UNDEFINED) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"index = MPI_UNDEFINED\n");
	  }
	  if (!req[statuses[i].getIndex()].isNull()) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"reqest not set to NULL\n");
	  }
	  if (data.get(statuses[i].getIndex()) != statuses[i].getIndex()) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"wrong data\n");
	  }
	}
      }

      statuses = Request.waitSomeStatus(Arrays.copyOf(req, tasks));
      if(statuses != null) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitsome: " +
				    "outcount not = MPI_UNDEFINED\n");
      }
    }

    /* Also try giving a 0 count and ensure everything is ok */
    statuses = Request.waitSomeStatus(new Request[0]);
    if(statuses != null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Waitsome: " +
				  "outcount not = MPI_UNDEFINED\n");
    }

    /* Clear data in order to ensure that waitSome receives all. */
    for(int i = 0; i < dataLength; i++)
        data.put(i, 0);

    /* now test with MPI_STATUSES_IGNORE, i.e., call the methods without
     * status parameter
     */

    if (me.get(0) != 0) {
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    } else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      index = Request.waitSome(new Request[0]);
      if(index != null) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitsome: " +
				    "outcount not = MPI_UNDEFINED\n");
      }

      done = 0;
      while (done < tasks - 1) {
	index = Request.waitSome(Arrays.copyOf(req, tasks));
	if(index.length == 0) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in Waitsome: " +
				      "outcount = 0\n");
	}
	for (int i = 0; i < index.length; i++) {
	  done++;
	  if (index[i] == MPI.UNDEFINED) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"index = MPI_UNDEFINED\n");
	  }
	  if (!req[index[i]].isNull()) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"reqest not set to NULL\n");
	  }
	  if (data.get(index[i]) != index[i]) {
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Waitsome: " +
					"wrong data\n");
	  }
	}
      }

      index = Request.waitSome(Arrays.copyOf(req, tasks));
      if(index != null) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Waitsome: " +
				    "outcount not = MPI_UNDEFINED\n");
      }
    }

    /* Also try giving a 0 count and ensure everything is ok */
    index = Request.waitSome(new Request[0]);
    if (index != null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Waitsome: " +
				  "outcount not = MPI_UNDEFINED\n");
    }

    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
