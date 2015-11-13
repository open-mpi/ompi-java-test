/* 
 *
 * This file is a port from "dimscreate.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Dimscreate.java		Author: S. Gross
 *
 */

import mpi.*;

public class Dimscreate
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    
    int tasks = 6;
    int dims[] = new int[2];
    CartComm.createDims(tasks, dims);

    if (dims[0] != 3 || dims[1] != 2)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Dims_create, dims = (" +
				  dims[0] + "," + dims[1] +
				  "), should be (3,2)\n");

    dims = new int[]{ 2, 0 };
    CartComm.createDims(tasks, dims);

    if (dims[0] != 2 || dims[1] != 3)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Dims_create, dims = (" +
				  dims[0] + "," + dims[1] +
				  "), should be (2,3)\n");

    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    dims = new int[]{ 0, 5 };

    try {
      CartComm.createDims(tasks, dims);
    }
    catch (MPIException ex)
    {
      int rc = ex.getErrorCode();
      int myClass = ex.getErrorClass();
      if (myClass != MPI.ERR_DIMS) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Dims_create, invalid " +
				    "dims not detected\n" +
				    "Error values are " + rc +
				    "(" + myClass + ")\n");
      }
    }
    
    dims = new int[3];
    CartComm.createDims(tasks, dims);
    
    if (dims[0] != 3 || dims[1] != 2 || dims[2] != 1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Dims_create, dims = (" +
				  dims[0] + "," + dims[1] + "," + dims[2] +
				  "), should be (3,2,1)\n");
    }    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
