/* 
 *
 * This file is a port from "initialized.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Initialized.java		Author: S. Gross
 *
 */

import mpi.*;

public class Initialized
{
  public static void main (String args[]) throws MPIException
  {
    if (MPI.isInitialized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Initialized returned " +
				  "TRUE before initialization\n");
    }
    
    MPI.Init(args);
    if (!MPI.isInitialized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Initialized returned " +
				  "FALSE after initialization\n");
    }
    
    MPI.Finalize();
    if (!MPI.isInitialized()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Initialized returned " +
				  "FALSE after initialization\n");
    }
  }
}
