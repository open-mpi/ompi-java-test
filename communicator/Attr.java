/*
 *
 * This file is a port from "attr.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Attr.java			Author: S. Gross
 *
 */

import mpi.*;

public class Attr
{
  public static void main (String args[]) throws MPIException
  {
    int errorcodeClass,rc,me,tasks,temp;
    int val, key, backend;
    Object attrObject;
    Comm comm;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    key = 0;

    attrObject = MPI.COMM_WORLD.getAttr(MPI.TAG_UB);
    if(attrObject == null)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: no val " +
				  "for MPI_TAG_UB\n");

    val = (int) attrObject;
    if (val < (1 << 15) - 1) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: tag_ub is " + val +
				  ", must be > " +
				  ((1 << 15) - 1) + "\n");
    }
    attrObject = MPI.COMM_WORLD.getAttr(MPI.HOST);
    if(attrObject == null)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: no val " +
				  "for MPI_HOST\n");
    val = (int) attrObject;
    if ((val != MPI.PROC_NULL) &&
	((val < 0) || (val >= tasks)))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: host =" +
				  val + "\n");

    attrObject = MPI.COMM_WORLD.getAttr(MPI.IO);
    if(attrObject == null)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: no val " +
				  "for MPI_IO\n");
    val = (int) attrObject;
    if ((val != MPI.ANY_SOURCE) &&
	(val != MPI.PROC_NULL) &&
	((val < 0) || (val >= tasks)))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: io = " +
				  val + "\n");

    key = Comm.createKeyval();
    comm = (Comm) (MPI.COMM_WORLD.clone());

    /*
     * MPI does not require attributes be copied on MPI_Comm_dup().
     */
    attrObject = comm.getAttr(MPI.TAG_UB);
    if (attrObject != null) {
      val = (int) attrObject;
      if (val < (1 << 15) - 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Attr_get: tag_ub = " +
				    val + ", must be > " +
				    ((1 << 15) - 1) + "\n");
    }

    backend = 12345;
    comm.setAttr(key,backend);
    val = 0;
    attrObject = comm.getAttr(key);
    if(attrObject == null)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: flag is " +
				  "false\n");
    val = (int) attrObject;
    if(val != 12345)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Attr_get: val = " +
				  val + ", should be 12345\n");

    comm.setErrhandler(MPI.ERRORS_RETURN);
    temp = key;
    Comm.freeKeyval(key);
    /*
    // In Java you can't modify parameters.
    if(key != MPI.KEYVAL_INVALID)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Keyval_free: key " +
				  "not set to INVALID\n");
    */

    /* Note that this is erroneous use of a keyval; the standard
       does not specify any particular behavior */
    key = temp;
    try {
      attrObject = comm.getAttr(key);
    }
    catch (MPIException ex)
    {
      rc = ex.getErrorCode();
      errorcodeClass = ex.getErrorClass();
      if(errorcodeClass != MPI.ERR_OTHER) {
	if (rc != MPI.SUCCESS) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "WARNING in MPI_Keyval_free: " +
				      "key not freed\n" +
				      "error returned was " +
				      rc + "(" + errorcodeClass + ")\n");
	}
	else {
	  /*
	   * MPI does not require this user error to be detected.
	   */
	}
      }
    }

    try {
      comm.deleteAttr(MPI.TAG_UB);
    }
    catch (MPIException ex)
    {
      rc = ex.getErrorCode();
      if(rc == MPI.SUCCESS)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Attr_delete, no " +
				    "error detected\n");
    }

    try {
      comm.deleteAttr(MPI.HOST);
    }
    catch (MPIException ex)
    {
      rc = ex.getErrorCode();
      if(rc == MPI.SUCCESS)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Attr_delete, no " +
				    "error detected\n");
    }

    try {
      comm.deleteAttr(MPI.IO);
    }
    catch (MPIException ex)
    {
      rc = ex.getErrorCode();
      if(rc == MPI.SUCCESS)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Attr_delete, no " +
				    "error detected\n");
    }

    comm.barrier();
    comm.free();
    MPI.Finalize();
  }
}
