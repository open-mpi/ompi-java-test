/* Function:	- tests synchonicity of MPI.Send between two ranks
 *
 * This file is a port from "send.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Send.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Send
{
  private final static int MAX_MSG_SIZE = 10485760;
  private final static int SMALL_SIZE = 65537;

  public static void main (String args[]) throws MPIException,
						 NullPointerException
  {
    int size, rank, mcwrank;
    int len;
    int count, total;
    int left, right;
    int left_value, right_value;
    Intracomm comm1;
    Request req[];
    int max_msg_size;
    
    MPI.Init(args);
    mcwrank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    req = new Request[4];

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);
    
    if (mcwrank == 0) {
      System.out.printf("It is not unusual for this test to take " +
			"several minutes,\n" +
			"particularly on slow networks.\n" +
			"Please be patient.\n");
    }
    
    /* Make a new comm with a different ordering than MPI_COMM_WORLD (to
       ensure that the underlying message transport doesn't rely on MCW
       ordering) */
    
    if (size > 4)
      comm1 = MPI.COMM_WORLD.split(mcwrank % 2, -mcwrank);
    else
      comm1 = MPI.COMM_WORLD.split(0, -mcwrank);
    size = comm1.getSize();
    rank = comm1.getRank();
    
    /* Look for overrides for RPI's that can't allocate much "special"
       memory */
    
    max_msg_size = MAX_MSG_SIZE;
    if (System.getenv("LAM_MPI_test_small_mem") != null)
      max_msg_size = SMALL_SIZE;
    if (mcwrank == 0)
      System.out.printf("NOTICE: Using max message size: %d\n",
			max_msg_size);
    
    /* Send short and long messages */
    
    CharBuffer send_left  = MPI.newCharBuffer(max_msg_size),
               send_right = MPI.newCharBuffer(max_msg_size),
               recv_left  = MPI.newCharBuffer(max_msg_size),
               recv_right = MPI.newCharBuffer(max_msg_size);
    
    /* Fill the send messages with expected values */
    
    for (int i = 0; i < max_msg_size; ++i) {
      send_left.put(i, (char)((rank % 254) + 1));
      send_right.put(i, (char)((rank % 254) + 1));
    }
    
    /* Calculate my left and right neighbors */
    
    left = (rank + 1) % size;
    left_value = (left % 254) + 1;
    right = (rank + size - 1) % size;
    right_value = (right % 254) + 1;
    
    /* Do the sends */
    
    for (total = 0, len = 1; len <= max_msg_size; len <<= 1, ++total) {
      ;
    }
    --total;
    
    OmpitestProgress.ompitestProgressStart(total);
    for (count = 0, len = 8192; len <= max_msg_size; len <<= 1, ++count) {
      for (int i = 0; i < len; ++i) {
	recv_left.put(i, (char)0);
	recv_right.put(i, (char)0);
      }
      
      req[0] = comm1.iSend(send_left, len, MPI.CHAR, left, 111);
      req[1] = comm1.iSend(send_right, len, MPI.CHAR, right, 222);
      req[2] = comm1.iRecv(recv_left, len, MPI.CHAR, left, 222);
      req[3] = comm1.iRecv(recv_right, len, MPI.CHAR, right, 111);
      
      Request.waitAll(req);
      
      /* Check the received message contents */
      
      for (int i = 0; i < len; ++i) {
	if (recv_left.get(i) != left_value)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR: Got wrong value in " +
				      "received message, got recv_left[" +
				      i + "] = " + recv_left.get(i) +
				      " should have been " +
				      left_value + "\n");
	if (recv_right.get(i) != right_value)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR: Got wrong value in " +
				      "received message, got recv_right[" +
				      i + "] = " + recv_right.get(i) +
				      " should have been " +
				      right_value + "\n");
      }

      /* Print the progress bar */
      
      OmpitestProgress.ompitestProgress(count);
    }
    OmpitestProgress.ompitestProgressEnd();
    
    /* All done */
    
    comm1.free();
    MPI.Finalize();
  }
}
