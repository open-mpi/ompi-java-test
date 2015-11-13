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
    Comm comm1, comm2, comm3;
    int me, result, color, key, tasks;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    comm1 = (Comm) (MPI.COMM_WORLD.clone());  
    result = Comm.compare(comm1, comm1);
    if(result != MPI.IDENT)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_compare, " +
				  "result = " + result + ", should " +
				  "be " + MPI.IDENT +
				  "(IDENT)\n");
    
    result = Comm.compare(MPI.COMM_WORLD, comm1);
    if(result != MPI.CONGRUENT)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_compare, " +
				  "result = " + result + ", should " +
				  "be " + MPI.CONGRUENT +
				  "(CONGRUENT)\n");
   
    /* Only do these tests if we have more than one rank */
    
    if (tasks > 1) {
      color = 1;
      key = -me;
      comm2 = ((Intracomm) comm1).split(color, key);
      comm3 = comm2;
      result = Comm.compare(comm1, comm2);
      if(result != MPI.SIMILAR)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Comm_compare, " +
				    "result = " + result + ", should " +
				    "be " + MPI.SIMILAR + "(SIMILAR)\n");
      
      color = me;
      comm2 = ((Intracomm) comm1).split(color, key);
      result = Comm.compare(comm1, comm2);
      if(result != MPI.UNEQUAL)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Comm_compare, " +
				    "result = " + result + ", should " +
				    "be " + MPI.UNEQUAL + "(UNEQUAL)\n");

      comm2.free();
      comm3.free();
    }
    
    comm1.free();
    MPI.Finalize();
  }
}
