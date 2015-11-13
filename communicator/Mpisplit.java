/* 
 *
 * This file is a port from "mpisplit.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Mpisplit.java			Author: S. Gross
 *
 */

import mpi.*;

public class Mpisplit
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, color, key;
    int me[] = new int[1];
    int ranks[] = new int[128];
    Comm newcomm;
    
    MPI.Init(args);
    me[0] = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());
    
    for (int i = 0; i < tasks; ++i) {
      ranks[i] = 0;
    }
    color = me[0] % 2;
    key = me[0];
    newcomm = MPI.COMM_WORLD.split(color, key);
    newcomm.allGather(me, 1, MPI.INT, ranks, 1, MPI.INT);
    if (me[0] % 2 == 0) {
      for (int i = 0; i < tasks / 2; ++i)
	if (ranks[i] != 2 * i)  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Comm_split (1): " +
				      "wrong tasks.\n" +
				      "ranks["+ i +"] = " + ranks[i] +
				      ", should be "+ (2*i) + "\n");
    }
    if (me[0] % 2 != 0) {
      for (int i = 0; i < tasks / 2; ++i)
	if (ranks[i] != 2 * i + 1)  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Comm_split (2): " +
				      "wrong tasks.\n" +
				      "ranks["+ i +"] = " + ranks[i] +
				      ", should be "+ (2*i + 1) + "\n");
    }
    MPI.COMM_WORLD.barrier();
    newcomm.free();
    
    /* Check to ensure that MPI_UNDEFINED works properly as a color */
    
    if (me[0] % 2 == 0)
      color = MPI.UNDEFINED;
    else
      color = 1;
    newcomm = MPI.COMM_WORLD.split(color, key);
    if (me[0] % 2 == 0) {
      if (!newcomm.isNull())
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Comm_split: " +
				    "expected MPI_COMM_NULL\n");
    } else {
      newcomm.barrier ();
      newcomm.free();
    }
    
    /* All done */
    MPI.Finalize();
  }
}
