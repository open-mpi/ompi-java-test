/* 
 *
 * This file is a port from "10_set.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Set10.java			Author: S. Gross
 *
 */

import mpi.*;

public class Set10
{
  public static void main (String args[]) throws MPIException
  {
    String key1 = "key1",
	   key2 = "key2key2",
	   key3 = "key3key3key3",
	   value1 = "value1",
	   value2 = "value2 value2",
	   value3 = "value3 value3 value3";

    MPI.Init(args);

    /* Pretty simple test -- call MPI_Info_set a few times and ensure
     * that they doesn't return an error
     */
    Info info1 = new Info(),
         info2 = new Info(),
         info3 = new Info();

    info1.set(key1, value2);
    info1.set(key1, value1);
    info2.set(key2, value2);
    info3.set(key3, value3);

    /* Free them so that we are bcheck clean */
    info1.free();
    info2.free();
    info3.free();

    MPI.Finalize();
  }
}
