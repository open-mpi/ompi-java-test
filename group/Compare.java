/* 
 *
 * This file is a port from "compare.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Compare.java			Author: S. Gross
 *
 */

import mpi.*;

public class Compare
{
  public static void main (String args[]) throws MPIException
  {
    int cmp;
    int r1[] = {0, 1};
    int r2[] = {1, 2};
    Group group;
    Group g1, g2;

    MPI.Init(args);
    
    /* We need at least 3 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    3, true);

    group = MPI.COMM_WORLD.getGroup();
    g1 = group.incl(r1);
    g2 = group.incl(r2);
    
    cmp = Group.compare(g1, g2);
    if (MPI.IDENT == cmp) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare, " +
				  "should not be MPI_IDENT\n");
    }
    g1.free();
    g2.free();
    group.free();

    MPI.Finalize();
  }
}
