/* 
 *
 * This file is a port from "pcontrol.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Pcontrol.java			Author: S. Gross
 *
 */

import mpi.*;

public class Pcontrol
{
  public static void main (String args[]) throws MPIException
  {
    int me,tasks;
    Object obj = null;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* Java doesn't support a variable number of arguments, so that
     * you have to store all arguments in an object which you can
     * hand over to pControl().
     */
    MPI.pControl(1, obj);

    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
