/* 
 *
 * This file is a port from "zero5.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Zero5.java			Author: S. Gross
 *
 */

import mpi.*;

public class Zero5
{
  private final static int DB_TALK = 1;
  private final static int DB_TALK = 1;
  private final static int MSZ = 10;

  public static void main (String args[]) throws MPIException
  {
    int myself, me, numtasks, count1, count2, count3, error=0;
    int check[] = new int[MSZ],
	ii[] = new int [MSZ];
    //    MPI_Aint aod[10];
    /* Java uses "length" to determine the number of elements in an
     * array, so that we need one set of arrays with two elements and
     * another one with three elements.
     */
    int aob2[] = new int[2],
        aod2[] = new int[2],
        aob3[] = new int[3],
        aod3[] = new int[3];
    Datatype aot2[] = new Datatype[2],
             aot3[] = new Datatype[3];
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

    for (int i = 0; i < MSZ; i++) {
      check[i] = i;
    }
    check[1] = -1;
    check[5] = -1;
    check[8] = -1;
    check[9] = -1;

    if (BAD_MPI_BEHAVIOUR != 0) {
      aot3[0] = MPI.FLOAT;
      aob3[0] = 0;
      aod3[0] = 8; /* should drop from map */
      aot3[1] = MPI.INT;
      aob3[1] = 1;
      aod3[1] = 0;
      aot3[2] = MPI.INT;
      aob3[2] = 2;
      aod3[2] = 8;
      newtype = Datatype.createStruct(aob3, aod3, aot3);
    } else {
      aot2[0] = MPI.INT;
      aob2[0] = 1;
      aod2[0] = 0;
      aot2[1] = MPI.INT;
      aob2[1] = 2;
      aod2[1] = 8;
      newtype = Datatype.createStruct(aob2, aod2, aot2);
    }
    newtype.commit();

    if(myself == 0)  {
      for (int i = 0; i < MSZ; i++) {
        ii[i] = i;
      }
      MPI.COMM_WORLD.send (ii, 2, newtype, 1, 0);
    } else if(myself == 1) {
      for (int i = 0; i < MSZ; i++) {
        ii[i] = -1;
      }
      status = MPI.COMM_WORLD.recv (ii, 2, newtype, 0, 0);
      for (int i = 0; i < MSZ; i++) {
        if (ii[i] != check[i]) {
	  error++;
	}
      }
      if (error != 0) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "FAILURE: Results below.\n");
        for (int i = 0; i < MSZ; i++) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ii[" + i + "]= " + ii[i] + ".\n");
        }
      }
      count1 = status.getCount(newtype);
      count2 = status.getElements(newtype);
      count3 = status.getCount(MPI.BYTE);
      if ((count1 != 2) ||
	  (count2 != 6) ||
	  (count3 != 24)) 
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Should be 2, 6, 24 but is " +
				    count1 + ", " + count2 + ", " +
				    count3 + ".\n");
    } 

    newtype.free();
    MPI.Finalize();
  }
}
