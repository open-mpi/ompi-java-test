/* 
 *
 * This file is a port from "lbub.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Lbub.java			Author: S. Gross
 *
 */

import mpi.*;

public class Lbub
{
  private final static int DB_TALK = 1;

  public static void main (String args[]) throws MPIException
  {
    //    MPI_Aint aod[3], lb, ub, extent;
    int numtasks, me, lb, ub, extent, error=0;
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
    Datatype newtype, newtype2, newtype3, newtype4, newtype5, newtype6;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    numtasks = MPI.COMM_WORLD.getSize();
    
    if ((numtasks != 1) && (me != 0)) { 
      if (DB_TALK != 0) {
	/* Java doesn't have the name of the command in args[0],
	 * so that I use the classname for method "main()".
	 */
	System.out.printf("Testcase %s uses one task, extraneous " +
			  "task #%d exited.\n",
			  OmpitestError.getClassName(), me);
      }
      MPI.Finalize();
      System.exit(0);
    }
    
    newtype = Datatype.createContiguous(4, MPI.INT);
    newtype.commit();
    
    aot2[0] = newtype;
    aod2[0] = 0;
    aob2[0] = 1;
    aot2[1] = MPI.UB;
    aod2[1] = 97;
    aob2[1] = 1;
    newtype2 = Datatype.createStruct(aob2, aod2, aot2);
    newtype2.commit();

    extent = newtype2.getExtent();
    lb = newtype2.getLb();
    ub = lb + extent;
    if ((extent != 97) | (lb != 0) | (ub != 97)) {
      error++;
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should be: Extent = 97, lb = 0, " +
				  "ub = 97.\n" +
				  "Is:        Extent = " + extent +
				  ", lb = " + lb + ", ub = " +
				  ub + ".\n"); 
    }
    if (DB_TALK != 0) {
      System.out.printf("Talking:\n" +
			"Should be: Extent = 97, lb = 0, ub = 97.\n" +
			"Is:        Extent = %d, lb = %d, ub = %d.\n",
			extent, lb, ub);
    }
    
    aot2[0] = newtype;
    aod2[0] = 0;
    aob2[0] = 1;
    aot2[1] = MPI.CHAR;
    aod2[1] = 97;
    aob2[1] = 1;
    newtype3 = Datatype.createStruct(aob2, aod2, aot2);
    newtype3.commit();

    extent = newtype3.getExtent();
    lb = newtype3.getLb();
    ub = lb + extent;
    if ((extent != 100) | (lb != 0) | (ub != 100)) {
      error++;
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should be: Extent = 100, lb = 0, " +
				  "ub = 100.\n" +
				  "Is:        Extent = " + extent +
				  ", lb = " + lb + ", ub = " +
				  ub + ".\n"); 
    }
    
    if (DB_TALK != 0) {
      System.out.printf("Talking:\n" +
			"Should be: Extent = 100, lb = 0, ub = 100.\n" +
			"Is:        Extent = %d, lb = %d, ub = %d.\n",
			extent, lb, ub);
    }
    
    aot3[0] = newtype;
    aod3[0] = 0;
    aob3[0] = 1;
    aot3[1] = MPI.LB;
    aod3[1] = 3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 94;
    aob3[2] = 1;
    newtype4 = Datatype.createStruct(aob3, aod3, aot3);
    newtype4.commit();

    extent = newtype4.getExtent();
    lb = newtype4.getLb();
    ub = lb + extent;
    if ((extent != 91) | (lb != 3) | (ub != 94)) {
      error++;
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should be: Extent = 91, lb = 3, " +
				  "ub = 94.\n" +
				  "Is:        Extent = " + extent +
				  ", lb = " + lb + ", ub = " +
				  ub + ".\n"); 
    }
    if (DB_TALK != 0) {
      System.out.printf("Talking:\n" +
			"Should be: Extent = 91, lb = 3, ub = 94.\n" +
			"Is:        Extent = %d, lb = %d, ub = %d.\n",
			extent, lb, ub);
    }
    
    aot3[0] = newtype;
    aod3[0] = 0;
    aob3[0] = 2;
    aot3[1] = MPI.LB;
    aod3[1] = -3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 96;
    aob3[2] = 1;
    newtype5 = Datatype.createStruct(aob3, aod3, aot3);
    newtype5.commit();

    extent = newtype5.getExtent();
    lb = newtype5.getLb();
    ub = lb + extent;
    if ((extent != 99) | (lb != -3) | (ub != 96)) {
      error++;
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should be: Extent = 99, lb = -3, " +
				  "ub = 96.\n" +
				  "Is:        Extent = " + extent +
				  ", lb = " + lb + ", ub = " +
				  ub + ".\n"); 
    }
    if (DB_TALK != 0) {
      System.out.printf("Talking:\n" +
		       "Should be: Extent = 99 lb = -3, ub = 96.\n" +
		       "Is:        Extent = %d, lb = %d, ub = %d.\n",
		       extent, lb, ub);
    }
    
    aot3[0] = newtype;
    aod3[0] = 2;
    aob3[0] = 2;
    aot3[1] = MPI.LB;
    aod3[1] = -3;
    aob3[1] = 1;
    aot3[2] = MPI.UB;
    aod3[2] = 86;
    aob3[2] = 1;
    newtype6 = Datatype.createStruct(aob3, aod3, aot3);
    newtype6.commit();

    extent = newtype6.getExtent();
    lb = newtype6.getLb();
    ub = lb + extent;
    if ((extent != 89) | (lb != -3) | (ub != 86)) {
      error++;
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should be: Extent = 89, lb = -3, " +
				  "ub = 86.\n" +
				  "Is:        Extent = " + extent +
				  ", lb = " + lb + ", ub = " +
				  ub + ".\n"); 
    }
    if (DB_TALK != 0) {
      System.out.printf("Talking:\n" +
			"Should be: Extent = 89  lb = -3, ub = 86.\n" +
			"Is:        Extent = %d, lb = %d, ub = %d.\n",
			extent, lb, ub);
    }
    
    if (error != 0)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERRORS in bounds/extent test.\n");
    
    newtype.free();
    newtype2.free();
    newtype3.free();
    newtype4.free();
    newtype5.free();
    newtype6.free();

    MPI.Finalize();
  }
}
