/* 
 *
 * This file is a port from "ring_mmap.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: RingMmap.java			Author: S. Gross
 *
 */

import mpi.*;

public class RingMmap
{
  public static void main (String args[]) throws MPIException
  {
    int npages = 512, page_size = sysconf(_SC_PAGESIZE);
    int rank, size, next, prev, count, val, *message, tag = 201;
    
    /* Start up MPI */
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    
    /* Calculate the rank of the next process in the ring.  Use the
     * modulus operator so that the last process "wraps around" to
     * rank zero.
     */
    
    next = (rank + 1) % size;
    prev = (rank + size - 1) % size;
    
    /* Use mmap to explicitly control adding and removing addresses
     * from our address space
     */
    count = npages * page_size / sizeof(int);
    
    message = (int*)(mmap(NULL, npages * page_size,
			  PROT_READ | PROT_WRITE,
#if defined MAP_ANON
                          MAP_PRIVATE | MAP_ANON,
#else
                          MAP_PRIVATE | MAP_ANONYMOUS,
#endif
                          -1, 0));
    
    /* Populate mapped address space */
    memset(message, 0, npages * page_size);
    
    if (NULL == message) {
      System.err.printf("Got null back from mmap (1)\n");
      MPI_Abort(MPI.COMM_WORLD, 1);
    }
    
    /* If we are the "master" process (i.e., MPI_COMM_WORLD rank 0),
     * put the number of times to go around the ring in the
     * message.
     */
    
    if (0 == rank) {
      *message = val = 10;
      
      System.out.printf("Process 0 sending %d to %d, tag %d " +
			"(%d processes in ring)\n",
			*message, next, tag, size);
      MPI_Send(message, count, MPI.INT, next, tag, MPI.COMM_WORLD); 
      System.out.printf("Process 0 sent to %d\n", next);
    }
    
    /* Pass the message around the ring.  The exit mechanism works as
     * follows: the message (a positive integer) is passed around the
     * ring.  Each time it passes rank 0, it is decremented.  When
     * each processes receives a message containing a 0 value, it
     * passes the message on to the next process and then quits. By
     * passing the 0 message first, every process gets the 0 message
     * and can quit normally.
     */
    
    while (1) {
      MPI_Recv(message, count, MPI.INT, prev, tag, MPI.COMM_WORLD, 
	       MPI.STATUS_IGNORE);
      
      val = *message;
      if (0 == rank) {
	--val;
	System.out.printf("Process 0 decremented value: %d\n", val);
      }
      
      if (0 == *message % 2) {
	munmap((char*)message, npages * page_size);
	npages += 20;
	
	message = (int*)(mmap(NULL, npages * page_size,
			      PROT_READ | PROT_WRITE,
#if defined MAP_ANON
			      MAP_PRIVATE | MAP_ANON,
#else
			      MAP_PRIVATE | MAP_ANONYMOUS,
#endif
			      -1, 0));
	
	/* Populate mapped address space */
	memset(message, 0, npages * page_size);
	
	System.out.printf(">> Process %d now up to %d pages\n",
			  rank, npages);
      }
      if (NULL == message) {
	System.err.printf("Got null from mmap (2)\n");
	MPI_Abort(MPI.COMM_WORLD, 1);
      }
      
      *message = val;
      MPI_Send(message, count, MPI.INT, next, tag, MPI.COMM_WORLD);
      if (0 == val) {
	System.out.printf("Process %d exiting\n", rank);
	break;
      }
    }
    
    /* The last process does one extra send to process 0, which
     * needs to be received before the program can exit
     */
    
    if (0 == rank) {
      MPI_Recv(message, count, MPI.INT, prev, tag, MPI.COMM_WORLD,
	       MPI.STATUS_IGNORE);
    }
    munmap((char*)message, npages * page_size);
    
    /* All done */
    
    MPI.Finalize();
  }
}
