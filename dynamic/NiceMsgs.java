/* 
 *
 * This file is a port from "nice_msgs.h" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: NiceMsgs.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class NiceMsgs
{
  /*
   * Here are some replacements for standard, blocking MPI
   * functions.  These replacements are "nice" and yield the
   * CPU instead of spinning hard.  The interfaces are the same.
   * Just replace:
   *     MPI_Recv    with  nice_recv
   *     MPI_Send    with  nice_send
   *     MPI_Barrier with  nice_barrier
   */
  
  static void nice_send(Buffer buf, int count, Datatype datatype,
                        int dest, int tag, Comm comm) throws MPIException
  {
    /* Assume a standard (presumably short/eager) send suffices. */
    comm.send(buf, count, datatype, dest, tag);
  }


  static void nice_recv(Buffer buf, int count, Datatype datatype,
                        int source, int tag, Comm comm)
    throws MPIException, InterruptedException
  {
    /*
     * We're only interested in modest levels of oversubscription
     * -- e.g., 2-4x more processes than physical processors.
     * So, the sleep time only needs to be about 2-4x longer than
     * a futile MPI_Test call.  For a wide range of processors,
     * something less than a millisecond should be sufficient.
     * Excessive sleep times (e.g., 1 second) would degrade performance.
     */
    //    struct timespec dt;
    //    dt.tv_sec    =       0;
    //    dt.tv_nsec   =  100000;
    
    Request req = comm.iRecv(buf, count, datatype, source, tag);
    while (!req.test()) {
      //      nanosleep(&dt, NULL);
      Thread.sleep(0, 100000);
    }
  }
  
  
  static void nice_barrier(Comm comm)
    throws MPIException, InterruptedException
  {
    int me, np, jump;
    IntBuffer buf = MPI.newIntBuffer(1);

    buf.put(0, -1);
    me = comm.getRank();
    np = comm.getSize();
    
    /* fan in */
    for (jump = 1; jump < np; jump <<= 1) {
      if ((me & jump) != 0) {
	nice_send(buf, 1, MPI.INT, me - jump, 343, comm);
	break;
      } else if (me + jump < np) {
	nice_recv(buf, 1, MPI.INT, me + jump, 343, comm);
      }
    }
    
    /* fan out */
    if (0 != me) {
      nice_recv(buf, 1, MPI.INT, me - jump, 344, comm);
    }
    jump >>= 1;
    for (; jump > 0; jump >>= 1) {
      if (me + jump < np) {
	nice_send(buf, 1, MPI.INT, me + jump, 344, comm);
      }
    }
  }
}
