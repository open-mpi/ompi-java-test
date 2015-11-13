/* 
 *
 * This file is a port from "bottom.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Bottom.java			Author: S. Gross
 *
 */

import mpi.*;

public class Bottom
{
  private final static int DB_TALK = 1;

  public static void main (String args[]) throws MPIException
  {
    int myself, me, numtasks;
    //    MPI_Aint disp;
    int ii[] = new int[1],
	disp[] = new int[1],
	len[] = new int[1];
    Datatype type[] = new Datatype[1],
	     newtype;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    me = MPI.COMM_WORLD.getRank();
    numtasks = MPI.COMM_WORLD.getSize();
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    
    if ((numtasks > 2) && (me > 1)) { 
      if (DB_TALK != 0) {
	/* Java doesn't have the name of the command in args[0],
	 * so that I use the classname for method "main()".
	 */
	System.out.printf("Testcase %s uses two tasks, extraneous " +
			  "task #%d exited.\n",
			  OmpitestError.getClassName(), me);
      }
      MPI.Finalize();
      System.exit(0);
    }
    len[0] = 1;
    disp[0] = &ii[0];
    type[0] = MPI.INT;
    newtype = Datatype.createStruct(len, disp, type);
    newtype.commit();

    if(myself == 0) {
      ii[0] = 2;
      MPI.COMM_WORLD.send (MPI.BOTTOM, 1, newtype, 1, 0);
    } else if(myself == 1) {
      ii[0] = 0;
      MPI.COMM_WORLD.recv (MPI.BOTTOM, 1, newtype, 0, 0);
      if(ii[0] != 2) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "MPI_BOTTOM test FAILED\n"); 
    } 
    newtype.free();
    MPI.Finalize();
  }
}
