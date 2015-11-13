/* 
 *
 * This file is a port from "c_lock_illegal.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CLockIllegal.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CLockIllegal
{
  public static void main (String args[]) throws MPIException
  {
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

    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    /* MPI_Win_create needs sizeOfInt in bytes, so that we cannot use
     * MPI.INT.getExtent() or we must adapt the size from elements
     * to bytes in the Java API
     */
    Info info = new Info();
    info.set("no_locks", "true");
    IntBuffer buffer = MPI.newIntBuffer(1);
    Win win = new Win(buffer, 1, 1, info, MPI.COMM_WORLD);
    win.setErrhandler(MPI.ERRORS_RETURN);   

    info.free();
    /*
    if (MPI.SUCCESS != ret) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Info_free");
    }
    */
    if (0 == rank) {
        try
        {
            win.lock(MPI.LOCK_SHARED, 0, 0);

            OmpitestError.ompitestError(OmpitestError.getFileName(),
                                        OmpitestError.getLineNumber(),
                                        "MPI_Win_lock succeeded when " +
                                        "no_locks given");
        }
        catch(MPIException ex)
        {
            // desired behaviour
        }
    }
    win.free();
    /*
    if (MPI.SUCCESS != ret) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Win_free");
    }
    */

    MPI.Finalize();
  }
}
