/* 
 *
 * This file is a port from "buffer.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Buffer.java			Author: S. Gross
 *
 */

import mpi.*;

public class Buffer
{
  private final static int BIGSIZE = 100000;
  private final static int SMALLSIZE = 10000;

  public static void main (String args[]) throws MPIException
  {
    int me;
    int data[] = new int[BIGSIZE];
    /* C uses "int" buffers, so that we probably must enlarge the
     * buffers by a factor of 4
     */
    byte oldbuf[],
	 buf1[] = new byte[SMALLSIZE + MPI.BSEND_OVERHEAD],
	 buf2[] = new byte[2 * BIGSIZE];
    Status status;
    Request request;
    Errhandler warn;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    status = new Status();
    MPI_Comm_create_errhandler((MPI_Handler_function*) Errors_warn,&warn);
    MPI.COMM_WORLD.setErrhandler(warn);
    MPI.COMM_WORLD.barrier();

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);


   if(me == 0) {
     oldbuf = MPI.detachBuffer();
     MPI.attachBuffer(buf2);
     oldbuf = MPI.detachBuffer();
     MPI.attachBuffer(buf1);
     for(int i = 0; i < 10000; i++) {
       data[i] = i;
     }
     request = MPI.COMM_WORLD.iBsend(data, 10000, MPI.INT, 1, 1);
 
     oldbuf = MPI.detachBuffer();
     for(int i = 0; i < 10000; i++) {
       buf1[i] = 0;
     }
     status = request.waitStatus();
   } else if(me == 1) {
     for(int i = 0; i < 4000000; i++)
     {
       ;
     }
     status = MPI.COMM_WORLD.recv(data, 10000, MPI.INT, 0, 1);
     for(int i = 0; i < 10000; i++)
       if(data[i] != i) { 
	 OmpitestError.ompitestError(OmpitestError.getFileName(),
				     OmpitestError.getLineNumber(),
				     "ERROR\n"); 
	 break; 
       }
   }
   MPI.COMM_WORLD.barrier();
   MPI_Errhandler_free(&warn);
   MPI.Finalize();
  }



  void Errors_warn(Comm comm[], int code[]) {
    char buf[] = new char[MPI.MAX_ERROR_STRING];
    int  myid, result_len; 
  
    myid = MPI.COMM_WORLD.getRank();
    MPI_Error_string( *code, buf, &result_len );
    OmpitestError.ompitestError(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				myid + " : " + buf + "\n");
  }
}
