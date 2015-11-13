/* 
 *
 * This file is a port from "waitnull.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Waitnull.java			Author: S. Gross
 *
 */

import mpi.*;

public class Waitnull
{
  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    Request request;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    request = MPI.REQUEST_NULL;
    request.waitFor();

    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
}
