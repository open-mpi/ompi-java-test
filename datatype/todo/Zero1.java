/* 
 *
 * This file is a port from "zero1.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Zero1.java			Author: S. Gross
 *
 */

import mpi.*;

public class Zero1
{
  private final static int DB_TALK = 1;

  public static void main (String args[]) throws MPIException
  {
    int myself, me, numtasks, count1, count2, count3;
    int ii[] = new int [1];
    //    MPI_Aint disp;
    int len[] = new int[1],
	disp[] = new int[1];
    Datatype type[] = new Datatype[1];
    Datatype newtype;
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    myself = MPI.COMM_WORLD.getRank();
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
    disp[0] = 0;
    type[0] = MPI.INT;
    //    MPI_Type_struct(0, &len, &disp, &type, &newtype);
    newtype = Datatype.createStruct(new int[0], new int[0],
				    new Datatype[0]);
    newtype.commit();
    
    if(myself == 0) {
      ii[0] = 2;
      MPI.COMM_WORLD.send (ii, 100, newtype, 1, 0);
    } else if(myself == 1) {
      ii[0] = 0;
      status = MPI.COMM_WORLD.recv (ii, 100, newtype, 0, 0);
      if(ii[0] != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: ii = " + ii[0] +
				    " instead of 0!\n");
      count1 = status.getCount(newtype);
      count2 = status.getElements(newtype);
      count3 = status.getCount(MPI.BYTE);
      /* JMS: This used to be MPI_UNDEFINED.  But the MPI spec doesn't
       * specify what the Right answer should be.  We decided to go
       * with MPICH's behavior and return 0 (vs. MPI_UNDEFINED).
       */
      if ((count1 == 0) &&
	  (count2 == 0) &&
	  (count3 == 0)) {
      } else
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Should be 0, 0, 0 but is " +
				    count1 + ", " + count2 + ", " +
				    count3 + ".\n");
    } 
    
    newtype.free();
    MPI.Finalize();
  }
}
