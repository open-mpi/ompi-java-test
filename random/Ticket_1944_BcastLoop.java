/* 
 *
 * This file is a port from "ticket-1944-bcast-loop.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: Ticket_1944_BcastLoop.java	Author: S. Gross
 *
 */

import mpi.*;

public class Ticket_1944_BcastLoop
{
  private final static int numm = 100;
  private final static int numt = 142;

  public static void main (String args[]) throws MPIException
  {
    int myid, iter_mod;
    double workarray1[] = new double[561],
	   workarray2[] = new double[561];
    
    MPI.Init(args);
    myid = MPI.COMM_WORLD.getRank();
    
    if (0 == args.length) {
      iter_mod = 1000;
      if (0 == myid) {
	System.out.printf("Defaulting to show only every %dth\n" +
			  "iteration; run with any value as\n" +
			  "args[0] to show every iteration\n\n",
			  iter_mod);
      }
    } else {
      iter_mod = 1;
      if (0 == myid) {
	System.out.printf("Showing every iteration\n");
      }
    }
    
    for (int m = 0; m < numm; ++m) {
      if (m % iter_mod == 0) {
	System.out.printf("rank %d, m = %d\n", myid, m);
	System.out.flush();
      }
      
      for (int nt = 0; nt <= numt; ++nt) {
	if (0 == myid) {
	  for (int i = 0; i < 561; ++i) {
	    workarray1[i] = numm * numt * i;
	    workarray2[i] = numm * numt * (i + 1);
	  }
	}
	MPI.COMM_WORLD.bcast(workarray1, 561, MPI.DOUBLE, 0);
	MPI.COMM_WORLD.bcast(workarray2, 561, MPI.DOUBLE, 0);
      }
    }
    MPI.Finalize();
  }
}
