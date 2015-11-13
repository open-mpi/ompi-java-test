/* 
 *
 * This file is a port from "40_getvaluelen.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: GetValuelen40.java		Author: S. Gross
 *
 */

import mpi.*;

public class GetValuelen40
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

    /* Pretty simple test -- call MPI_Info_set followed by MPI_Info_get
     * and compare that the value that we get out is the same as the one
     * that we put in
     */
    Info info1 = test_info(key1, value1),
         info2 = test_info(key2, value2),
         info3 = test_info(key3, value3);

    /* Free them so that we are bcheck clean */
    info1.free();
    info2.free();
    info3.free();

    MPI.Finalize();
  }

  /*
   * Abort on failure
   */
  private static Info test_info(String key, String value) throws MPIException
  {
    Info info = new Info();
    info.set(key, value);
    int len = info.get(key).length();

    if (value.length() != len)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Info_get_valuelen obtained len " +
				  len + ", expected " + value.length() +
				  "\n");

    String get_value = info.get("bogus");

    if(get_value != null)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Info_get_valuelen for bogus key " +
				  "reported flag==1\n");

    return info;
  }
}
