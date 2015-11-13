/* 
 *
 * This file is a port from "wtime.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Wtime.java			Author: S. Gross
 *
 */

import mpi.*;

public class Wtime
{
  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    double time, delta, min;
    double tick1, tick2;
    int rank;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    
    if (rank == 0) {
      Thread.sleep(1);
      
      tick1 = MPI.wtick();
      
      for (int i = 0; i < 100; ++i) {
	tick2 = MPI.wtick();
	if ((tick2 - tick1) > 1e-06) {
	  System.out.printf("wtick variation: %10.10f, %10.10f\n",
			    tick1, tick2);
	  break;
	}
      }
      
      min = -1;
      
      for (int i = 0; i < 100; ++i) {
	time = MPI.wtime();
	while ((delta = MPI.wtime() - time) <= 0);
	
	if ((min < 0) || (min > delta))
	  min = delta;
      }
      
      System.out.printf("resolution = %10.10f, wtick = %10.10f\n",
			min, MPI.wtick());
    }
    
    MPI.Finalize();
  }
}
