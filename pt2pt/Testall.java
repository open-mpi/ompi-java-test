/* 
 *
 * This file is a port from "testall.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Testall.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Testall
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, dataLength = 2000;
    IntBuffer me = MPI.newIntBuffer(1);
    IntBuffer data = MPI.newIntBuffer(dataLength);
    boolean flag;
    Request req[] = new Request[2000];
    Status status[];

    MPI.Init(args);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();

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

      status = Request.testAllStatus(new Request[0]);
      if(status == null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testall: flag is " +
				    "not set\n");
      
      status = null;
      while(status == null) {
	for (int i = 1; i < tasks; i++) {
	  if (req[i].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testall: " +
					"incorrect status\n");
	}
	status = Request.testAllStatus(Arrays.copyOf(req, tasks));
      }
      for (int i = 1; i < tasks; i++) {
	if (!req[i].isNull())
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in Testall: request " +
				      "not set to NULL\n");
	if (status[i].getSource() != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in Testall: request " +
				      "prematurely set to NULL\n");
	if (data.get(i) != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Testall: " +
				      "incorrect data\n");
      }
      
      status = Request.testAllStatus(Arrays.copyOf(req, tasks));
      if(status == null)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: " +
				    "flag is not set\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    status = Request.testAllStatus(new Request[0]);
    if(status == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Testany: flag " +
				  "is not set\n");
    }

    /* Clear data in order to ensure that testAll receives all. */
    for(int i = 0; i < dataLength; i++)
        data.put(i, 0);

    /* now test with MPI_STATUS_IGNORE, i.e., call the methods without
     * status parameter
     */
    if (me.get(0) != 0)
      MPI.COMM_WORLD.send (me, 1, MPI.INT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (int i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.iRecv(slice(data, i), 1, MPI.INT, i, 1);

      flag = Request.testAll(new Request[0]);
      if (!flag)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testall: flag is " +
				    "not set\n");
      
      flag = false;
      while (!flag) {
	for (int i = 1; i < tasks; i++) {
	  if (req[i].isNull())
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"ERROR in MPI_Testall: " +
					"incorrect status\n");
	}
	flag = Request.testAll(Arrays.copyOf(req, tasks));
      }
      for (int i = 1; i < tasks; i++) {
	if (!req[i].isNull())
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in Testall: request " +
				      "not set to NULL\n");
	if (data.get(i) != i)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Testall: " +
				      "incorrect data\n");
      }
      
      flag = Request.testAll(Arrays.copyOf(req, tasks));
      if (!flag)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Testany: " +
				    "flag is not set\n");
    }
    
    /* Also try giving a 0 count and ensure everything is ok */
    flag = Request.testAll(new Request[0]);
    if (!flag) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Testany: flag " +
				  "is not set\n");
    }

    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
