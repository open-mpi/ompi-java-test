/* 
 *
 * This file is a port from "final.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Final.java			Author: S. Gross
 *
 */

import mpi.*;

public class Final
{
  public static void main (String args[]) throws MPIException
  {
    int rc,rank;
    int checking_params =
      OmpitestConfig.OMPITEST_CHECKING_MPI_API_PARAMS;

    MPI.Init(args);

    /* If OMPI is not checking parameters, then just exit */
    if (checking_params != 0) {
      String e = System.getenv("OMPI_MCA_mpi_param_check");
      if (null != e && 0 == Integer.parseInt(e)) {
	checking_params = 0;
      }
    }
    if (checking_params == 0) {
      MPI.Finalize();
      System.exit(77);
    }

    rank = MPI.COMM_WORLD.getRank();
    MPI.COMM_WORLD.barrier();
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    MPI.Finalize();
    if (rank == 0) {
      System.out.printf("******************************************\n" +
			"This test should generate a message about\n" +
			"MPI is either not initialized or has\n" +
			"already been finialized.\n" +
			"ERRORS ARE EXPECTED AND NORMAL IN THIS\n" +
			"PROGRAM!!\n" +
			"******************************************\n");
      try {
	MPI.COMM_WORLD.barrier();
      }
      catch (MPIException ex)
      {
	System.err.printf ("%s\n", ex.getMessage());
	System.exit(0);
      }
      /* The next statement shouldn't be reached	*/
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Already finalized " +
				  "condition not detected\n");
    }
  }
}
