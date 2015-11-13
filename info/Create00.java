/* 
 *
 * This file is a port from "00_create.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Create00.java			Author: S. Gross
 *
 */

import mpi.*;

public class Create00
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    /* Pretty simple test -- call MPI_Info_create and ensure that it
     * doesn't return an error
     */
    Info info1 = new Info(),
         info2 = new Info(),
         info3 = new Info();

    /* Free them so that we are bcheck clean */
    info1.free();
    info2.free();
    info3.free();

    MPI.Finalize();
  }
}
