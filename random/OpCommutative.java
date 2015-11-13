/* 
 *
 * This file is a port from "op_commutative.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OpCommutative.java		Author: S. Gross
 *
 */

import mpi.*;

public class OpCommutative
{
  public static void main (String args[]) throws MPIException
  {
    boolean commute = false;
    Op op;
    
    MPI.Init(args);
    
    op = new Op(func, commute);
    if (commute) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "op should not be commutative");
    }
    op.free();

    commute = true;
    op = new Op(func, commute);
    if (!commute) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "op should be commutative");
    }
    op.free();
    
    MPI.Finalize();
  }
  
  
  private static final UserFunction func = new UserFunction()
  {@Override public void call(Object inVec, Object inOutVec,
                              int count, Datatype dt) throws MPIException
  {
    /* Just for compilation purposes */
  }};
}
