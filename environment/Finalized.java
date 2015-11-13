/* 
 *
 * This file is a port from "finalized.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Finalized.java			Author: S. Gross
 *
 */

import mpi.*;

public class Finalized
{
  public static void main (String args[]) throws MPIException
  {
    if (MPI.isFinalized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Finalized returned " +
				  "TRUE before finalization\n");
    }
    
    MPI.Init(args);
    if (MPI.isFinalized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Finalized returned " +
				  "TRUE before finalization\n");
    }
    
    MPI.Finalize();
    if (!MPI.isFinalized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Finalized returned " +
				  "FALSE after finalization\n");
    }
  }
}
