/* 
 *
 * This file is a port from "ticket-2014-basic-send-recv.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: Ticket_2014_BasicSendRecv.java		Author: S. Gross
 *
 */

import mpi.*;

public class Ticket_2014_BasicSendRecv
{
  private final static int TAG = 1234;

  public static void main (String args[]) throws MPIException
  {
    int rank, size, peer;
    int resp[] = new int[1];
    Status status;
    Datatype type;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    
    if(size != 2) {
      System.out.printf("Please 2 processes\n");
      MPI.Finalize();
      System.exit(0);
    }

    type = MPI.INT.clone();

    if( rank == 0 ) {
      peer = 1;
      status = MPI.COMM_WORLD.recv(resp, 1, type, peer, TAG);
      System.out.printf("Manager: Received (%d) from Rank %d with " +
			"Tag %d\n", 
			resp[0], status.getSource(), status.getTag());
    }
    else {
      peer = 0;
      resp[0] = 12345;
      MPI.COMM_WORLD.send (resp, 1, type, 0, TAG * rank);
    }
    
    MPI.COMM_WORLD.barrier();
    
    type.free();
    MPI.Finalize();
  }
}
