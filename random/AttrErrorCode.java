/* 
 *
 * This file is a port from "attr-error-code.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: AttrErrorCode.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class AttrErrorCode
{
  public static void main (String args[]) throws MPIException
  {
    int ret;
    Object value;

    if (OmpitestConfig.OMPI_PARAM_CHECK == 0) {
      /* If OMPI was not compiled with parameter checking,
       * skip this test
       */
      System.exit(77);
    }

    MPI.Init(args);

    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    value = MPI.COMM_WORLD.getAttr(MPI.TAG_UB);
    if(value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_COMM_GET_ATTR should have " +
				  "failed\n");
    }
    
    IntBuffer buffer = MPI.newIntBuffer(0);
    Win win = new Win(buffer, 0, 1, MPI.INFO_NULL, MPI.COMM_SELF);
    win.setErrhandler(MPI.ERRORS_RETURN);
    MPIException mpiEx = null;
    
    try
    {
        win.getAttr(MPI.KEYVAL_INVALID);
    }
    catch(MPIException ex)
    {
        mpiEx = ex;
    }

    if(mpiEx == null || mpiEx.getErrorCode() != MPI.ERR_KEYVAL)
    {
        OmpitestError.ompitestError(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    "MPI_WIN_GET_ATTR expected " +
                                    "MPI_ERR_KEYVAL\n");
    }

    win.free();
    
    /* The following code may be erroneous, because "ret" is used
     * in "if", but not in the function call.
     */
    //    MPI_Comm_get_attr(MPI.COMM_WORLD, MPI.KEYVAL_INVALID,
    //		      &value, &flag);
    //    if (MPI.ERR_KEYVAL != ret) {

    try
    {
        mpiEx = null;
        MPI.COMM_WORLD.getAttr(MPI.KEYVAL_INVALID);
    }
    catch(MPIException ex)
    {
        mpiEx = ex;
    }

    if(mpiEx == null || mpiEx.getErrorCode() != MPI.ERR_KEYVAL)
    {
        OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "MPI_COMM_GET_ATTR expected " +
				    "MPI_ERR_KEYVAL\n");
    }
    
    /* The following code may be erroneous, because "ret" is used
     * in "if", but not in the function call.
     */
    //    MPI_Type_get_attr(MPI.INT, MPI.KEYVAL_INVALID,
    //		      &value, &flag);
    //    if (MPI.ERR_KEYVAL != ret) {

    try
    {
        mpiEx = null;
        MPI.INT.getAttr(MPI.KEYVAL_INVALID);
    }
    catch(MPIException ex)
    {
        mpiEx = ex;
    }

    if(mpiEx == null || mpiEx.getErrorCode() != MPI.ERR_KEYVAL)
    {
        OmpitestError.ompitestError(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    "MPI_TYPE_GET_ATTR expected " +
                                    "MPI_ERR_KEYVAL\n");
    }
    
    MPI.Finalize();
  }
}
