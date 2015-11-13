/* 
 *
 * This file is a port from "alloc-mem.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: AllocMem.java			Author: S. Gross
 *
 */

import mpi.*;

public class AllocMem
{
  private final static int SIZE = (1024 * 1024 * 64);

  public static void main (String args[]) throws MPIException
  {
    char *send_buf, *recv_buf;
    
    MPI.Init(args);
    
    send_buf = malloc(2 * SIZE);
    if (NULL == send_buf) {
      fprintf(stderr, "Malloc failed\n");
      exit(1);
    }
    recv_buf = send_buf + SIZE;
    do_test(send_buf, recv_buf, 7);
    free(send_buf);
    
    MPI_Alloc_mem(SIZE * 2, MPI.INFO_NULL, &send_buf);
    if (NULL == send_buf) {
      fprintf(stderr, "MPI_Alloc_mem failed\n");
      exit(1);
    }
    recv_buf = send_buf + SIZE;
    do_test(send_buf, recv_buf, 8);
    MPI_Free_mem(send_buf);
    
    MPI.Finalize();
  }
  
  
  private static void do_test(char *send_buf, char *recv_buf, int val)
    throws MPIException
  {
    int i, rank, size, to, from;
    
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    to = (rank + 1) % size;
    from = (rank + size - 1) % size;
    
    memset(send_buf, val, SIZE);
    memset(recv_buf, 0, SIZE);
    
    MPI_Sendrecv(send_buf, SIZE, MPI.CHAR, to, 100,
                 recv_buf, SIZE, MPI.CHAR, from, 100, 
                 MPI.COMM_WORLD, MPI.STATUS_IGNORE);
    for (i = 0; i < SIZE; ++i) {
      if (recv_buf[i] != val) {
	fprintf(stderr, "Got wrong data in recv_buf -- recv_buf[%d] "
		"= %d instead of %d\n",	i, recv_buf[i], val);
      }
    }
  }
}
