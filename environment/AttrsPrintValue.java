/* 
 *
 * This file is a port from "attrs.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file. I have added a
 * statement to print the value of a predefined attribute.
 *
 *
 * File: AttrsPrintValue.java		Author: S. Gross
 *
 */

import mpi.*;

public class AttrsPrintValue
{
  public static void main (String args[]) throws MPIException
  {
    int ret = 0;

    MPI.Init(args);
    ret += get_attr("MPI_UNIVERSE_SIZE", MPI.UNIVERSE_SIZE);
    ret += get_attr("MPI_TAG_UB", MPI.TAG_UB);
    ret += get_attr("MPI_IO", MPI.IO);
    ret += get_attr("MPI_HOST", MPI.HOST);
    ret += get_attr("MPI_WTIME_IS_GLOBAL", MPI.WTIME_IS_GLOBAL);
    ret += get_attr("MPI_APPNUM", MPI.APPNUM);
    ret += get_attr("MPI_LASTUSEDCODE", MPI.LASTUSEDCODE);
    MPI.Finalize();
    System.exit(ret);
  }

  private static int get_attr(String str, int key) throws MPIException
  {
    Object attrObj;

    attrObj = MPI.COMM_WORLD.getAttr(key);
    if (attrObj == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Didn't get attribute " +
				  str + "\n");
      return 1;
    }
    System.out.printf("Process " + MPI.COMM_WORLD.getRank() +
		      ":  " + str + " = " + attrObj + "\n");
    return 0;
  }
}
