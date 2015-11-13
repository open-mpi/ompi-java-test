/* 
 *
 * This file is a port from "testsome.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Testsome.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Testsome
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, done, dataLength = 2000;
    IntBuffer me = MPI.newIntBuffer(1);
    IntBuffer data = MPI.newIntBuffer(dataLength);
    int index[];
    Request req[];
    Status statuses[];

    MPI.Init(args);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[2000];

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    /* first call methods with a status object				*/
    if (me.get(0) != 0)
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      statuses = Request.testSomeStatus(new Request[0]);
      if(statuses != null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testsome: outcount " +
				    "not = MPI_UNDEFINED (1), was " +
				    statuses.length + "\n");
      
      done = 0;
      while (done < tasks - 1) {
	statuses = Request.testSomeStatus(Arrays.copyOf(req, tasks));
	for(int i = 0; i < statuses.length; i++) {
	  done++;
	  if(statuses[i].getIndex() == MPI.UNDEFINED)
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"index = MPI_UNDEFINED\n");
	  if(!req[statuses[i].getIndex()].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"reqest not set to NULL\n");
	  if(data.get(statuses[i].getIndex()) != statuses[i].getIndex())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"wrong data\n");
	}
      }
      
      statuses = Request.testSomeStatus(Arrays.copyOf(req, tasks));
      if(statuses != null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testsome: outcount " +
				    "not = MPI_UNDEFINED (2), was " +
				    statuses.length + "\n");
    }
    
    /* Now try with a count of 0 */
    statuses = Request.testSomeStatus(new Request[0]);
    if(statuses != null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Testsome: outcount " +
				  "not = MPI_UNDEFINED (3), was " +
				  statuses.length + "\n");
    }

    /* Clear data in order to ensure that testSome receives all. */
    for(int i = 0; i < dataLength; i++)
        data.put(i, 0);

    /* now test with MPI_STATUSES_IGNORE, i.e., call the methods without
     * status parameter
     */

    if (me.get(0) != 0)
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      index = Request.testSome(new Request[0]);
      if(index != null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testsome: outcount " +
				    "not = MPI_UNDEFINED (1), was " +
				    index.length + "\n");
      
      done = 0;
      while (done < tasks - 1) {
	index = Request.testSome(Arrays.copyOf(req, tasks));
	for (int i = 0; i < index.length; i++) {
	  done++;
	  if (index[i] == MPI.UNDEFINED)
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"index = MPI_UNDEFINED\n");
	  if (!req[index[i]].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"reqest not set to NULL\n");
	  if (data.get(index[i]) != index[i])
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testsome: " +
					"wrong data\n");
	}
      }
      
      index = Request.testSome(Arrays.copyOf(req, tasks));
      if(index != null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testsome: outcount " +
				    "not = MPI_UNDEFINED (2), was " +
				    index.length + "\n");
    }
    
    /* Now try with a count of 0 */
    index = Request.testSome(new Request[0]);
    if(index != null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Testsome: outcount " +
				  "not = MPI_UNDEFINED (3), was " +
				  index.length + "\n");
    }

    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
