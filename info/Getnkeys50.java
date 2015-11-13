/* 
 *
 * This file is a port from "50_getnkeys.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Getnkeys50.java		Author: S. Gross
 *
 */

import mpi.*;

public class Getnkeys50
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

    /* Pretty simple test -- call MPI_Info_get_nkeys and ensure that it
     * returns the right number
     */
    Info info = new Info();

    int nkeys = info.size();
    if (nkeys != 0)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Info_get_nkeys reported " + nkeys +
				  " on an empty MPI_Info handle\n");

    info.set(key1, value2);
    info.set(key1, value1);
    info.set(key2, value2);
    info.set(key3, value3);

    nkeys = info.size();
    if (nkeys != 3)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Info_get_nkeys reported " + nkeys +
				  "; expected 3\n");

    /* Free it so that we are bcheck clean */
    info.free();

    MPI.Finalize();
  }
}
