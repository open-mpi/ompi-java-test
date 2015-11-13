/* 
 *
 * This file is a port from "zero6.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Zero6.java			Author: S. Gross
 *
 */

import mpi.*;

public class Zero6
{
  private final static int BAD_MPI_BEHAVIOUR = 1;
  private final static int DB_TALK = 1;
  private final static int MSZ = 10;

  public static void main (String args[]) throws MPIException
  {
    int myself, me, numtasks, count1, count2, count3, error=0, rc;
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
    Status status = null;

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

    if (BAD_MPI_BEHAVIOUR == 0) {
      aot2[0] = MPI.INT;
      aob2[0] = 1;
      aod2[0] = 0;
      aot2[1] = MPI.INT;
      aob2[1] = 2;
      aod2[1] = 8;
      newtype = Datatype.createStruct(aob2, aod2, aot2);
    } else {
      aot3[0] = MPI.INT;
      aob3[0] = 1;
      aod3[0] = 0;
      aot3[1] = MPI.INT;
      aob3[1] = 2;
      aod3[1] = 8;
      /* Overlapping datatype is not a correct MPI behaviour for
       * receive. OpenMPI should detect that the datatype overlap
       * and return an error code from the receive operation. But in
       * this test the return code are ignored so I will disable
       * this part by now.
       */
      aot3[2] = MPI.FLOAT;
      aob3[2] = 0;
      aod3[2] = 8; /* should drop from map */
      newtype = Datatype.createStruct(aob3, aod3, aot3);
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
      //      rc = MPI_Recv(ii,2,newtype,0,0,MPI.COMM_WORLD,&status);
      rc = MPI.SUCCESS;
      MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
      try {
	status = MPI.COMM_WORLD.recv (ii, 2, newtype, 0, 0);
      }
      catch (MPIException ex)
      {
	rc = ex.getErrorCode();
      }
      MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_ARE_FATAL);

      if (BAD_MPI_BEHAVIOUR != 0) {
	for (int i = 0; i < MSZ; i++) {
	  if (ii[i] != check[i]) {
	    error++;
	  }
	}
	if(MPI.SUCCESS == rc) {
	  OmpitestError.ompitestWarning(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					"The receive datatype overlap. " +
					"The operation is not legal\n" );
	}
      } else {
	/* The return code should be != MPI_SUCCESS and the data should
	 * be untouched.
	 */
	for (int i = 0; i < MSZ; i++) {
	  if (ii[i] != check[i]) {
	    error++;
	  }
	}
      }
      if (error != 0) {
	OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "FAILURE: Results below.\n");
        for (int i = 0; i < MSZ; i++) {
	  if (BAD_MPI_BEHAVIOUR != 0) {
	    OmpitestError.ompitestWarning(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "ii[" + i + "]= " + ii[i] +
					  " and check[" + i + "] = " +
					  check[i] + ".\n");
	  } else {
	    OmpitestError.ompitestWarning(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "ii[" + i + "]= " + ii[i] +
					  " (should be -1)\n");
	  }
        }
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "giving up.\n");
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
