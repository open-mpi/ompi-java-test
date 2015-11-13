/* 
 *
 * This file is a port from "seq.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Seq.java			Author: S. Gross
 *
 */

import mpi.*;

public class Seq
{
  private final static int ITER = 30;

  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    int data[] = new int[1];
    Status status;
        
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    data[0] = -1;
    if(me == 0)  {
      for(int i = 0; i < (tasks - 1) * ITER; i++)  
	status = MPI.COMM_WORLD.recv(data, 1, MPI.INT, MPI.ANY_SOURCE, 1);
    } else {
      data[0] = me;
      for(int i = 0; i < ITER; i++)
	MPI.COMM_WORLD.send (data, 1, MPI.INT, 0, 1);
    }
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
