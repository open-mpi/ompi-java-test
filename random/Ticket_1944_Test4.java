/* 
 * This program should stall with OMPI 1.3.2.  Run it with
 *
 *      % mpiexec -np 2 --mca btl sm,self java Ticket_1944_Test4
 *
 *
 * This file is a port from "ticket-1944-test4.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Ticket_1944_Test4.java		Author: S. Gross
 *
 */

import mpi.*;

public class Ticket_1944_Test4
{
  /* N >= btl_sm_fifo_size */
  private final static int N = 4096;

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int myid, i;
    
    MPI.Init(args);
    myid = MPI.COMM_WORLD.getRank();
    
    /*
     * In the first batch of messages, rank 0 sends a flood
     * of messages to rank 1.  We stall rank 0 momentarily
     * so that rank 1 will be ready to receive.  Thus, all
     * messages should be successfully sent by 0 and received
     * by 1.
     *
     * On the other hand, rank 1 is concurrently returning
     * fragments to rank 0, who is not "listening" for them.
     * Thus, rank 0's in-coming FIFO becomes congested.  At
     * that point, rank 1 no longer writes returned fragments
     * to rank 0's FIFO, but to rank 1's own "pending send"
     * queue.
     **/
    
    if(myid == 0) {
      Thread.sleep(2000);
      for(i = 0; i < N; i++)
	MPI.COMM_WORLD.send(null, 0, MPI.BYTE, 1-myid, 343);
    }
    if(myid == 1) {
      for(i = 0; i < N; i++)
	MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, 1-myid, 343);
    }
    
    /*
     * Now, rank 1 tries to send a user message back to
     * rank 0.  It does so immediately -- even though rank 0's
     * FIFO is still congested, rank 1 simply writes the
     * user message to its own pending-send queue.  Rank 0
     * posts a receive for the user message.  This causes
     * it to drain its FIFO.  Once the FIFO is drained,
     * rank 0 keeps looking for the expected message, but
     * that message is stuck in rank 1's pending-send queue.
     * And, rank 1 goes into a futile receive, and will not
     * retry any of its pending sends.
     *
     * To illustrate this problem, we have rank 0 sleep before
     * posting the user receive.  This will keep rank 0 from
     * starting to drain its FIFO too early.
     *
     * To indicate hung execution, rank 1 prints some output
     * when it has sent its user message to rank 0.  Rank 0
     * prints output before and after its receive operation.
     * When we see output showing rank 1 has sent its message,
     * but that rank 0 is stuck in its receive, we know the
     * program has hung.
     *
     **/
    
    if(myid == 0) {
      Thread.sleep(2000);
      System.out.printf("%d start recv\n", myid);
      MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, 1-myid, 343);
      System.out.printf("%d finish recv\n", myid);
      
      for(i = 0; i < N; i++)
	MPI.COMM_WORLD.send(null, 0, MPI.BYTE, 1-myid, 343);
    }
    
    if(myid == 1) {
      MPI.COMM_WORLD.send(null, 0, MPI.BYTE, 1-myid, 343);
      System.out.printf("%d finish send\n", myid);
      
      for(i = 0; i < N; i++)
	MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, 1-myid, 343);
    }
    
    MPI.Finalize();
  }
}
