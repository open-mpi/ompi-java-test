/* 
 *
 * This file is a port from "err.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Err.java			Author: S. Gross
 *
 */

import mpi.*;

public class Err
{
  private static int errcount1 = 0;
  private static int errcount2 = 0;

  public static void main (String args[]) throws MPIException
  {
    int me,tasks,rc;
    int size[] = new int[1];
    Errhandler handler1,handler2,commhandler;
    Comm comm;
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
    
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    MPI_Comm_create_errhandler((MPI_Comm_errhandler_function*) myhandler1,
			       &handler1);
    MPI_Comm_create_errhandler((MPI_Comm_errhandler_function*) myhandler2,
			       &handler2);
    
    comm = (Comm) (MPI.COMM_WORLD.clone());
    
    comm.setErrhandler(handler1);
    commhandler = comm.getErrhandler();
    if(commhandler != handler1) 
      /* Bonk -- unfortunately, this is not portable to
       * architectures/OS's where sizeof(int) < sizeof(void*)
       *
       *      OmpitestError.ompitestError(OmpitestError.getFileName(),
       *			  OmpitestError.getLineNumber(),
       *			  "ERROR in MPI_Errhandler_get, " +
       *			  "handler = " + (int) commhandler +
       *			  ", should be " + (int) handler1 + "\n");
       */
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Errhandler_get, " +
				  "handler is wrong value\n");

    comm.bcast (size, -1, MPI.INT, 0);
    
    comm.setErrhandler(handler2);
    comm.bcast (size, 1, MPI.INT, -1);
    
    MPI_Errhandler_free(&handler2);
    if(handler2 != MPI.ERRHANDLER_NULL)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Errorhandler_free, " +
				  "handle not set to NULL\n");
   
    comm.setErrhandler(commhandler);
    
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    rc = MPI_Comm_create_errhandler(0,&handler2);
    if(rc == MPI_SUCCESS) 
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: NULL function not detected\n");
    
    if ((errcount1 != 1) || (errcount2 != 1)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: errcount1 & errcount2 " +
				  "should be 1, 1; they are " +
				  errcount1 + ", " + errcount2 + "\n");
    }
    
    MPI.COMM_WORLD.barrier();
    MPI_Errhandler_free( &handler1 );
    MPI_Errhandler_free( &commhandler );
    comm.free();
    MPI.Finalize();
  }


  void myhandler1(Comm comm[],int code[],...)
  {
    int me;
    
    ++errcount1;
    
    me = comm[0].getRank();
    if (code[0] != MPI.ERR_COUNT) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: rank " + me + ": expected " +
				  "errcode " + MPI.ERR_COUNT +
				  ", got " + code[0] + "\n");
    }
  }


  void myhandler2(Comm comm[],int code[],...)
  {
    int me;
    
    ++errcount2;
    
    me = comm[0].getRank();
    if (code[0] != MPI.ERR_ROOT) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: rank " + me + ": expected " +
				  "errcode " + MPI.ERR_ROOT +
				  ", got " + code[0] + "\n");
    }
  }
}
