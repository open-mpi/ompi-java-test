/* 
 *
 * This file is a port from "op.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OpTest.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class OpTest
{
  public static void main (String args[]) throws MPIException
  {
    int me, tasks, root;
    boolean commute = false;

    Op op, temp;
    int checking_params = OmpitestConfig.OMPITEST_CHECKING_MPI_API_PARAMS;

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    
    ByteBuffer info   = MPI.newByteBuffer(MPI.INT2.getExtent()),
               result = MPI.newByteBuffer(MPI.INT2.getExtent());

    /* Check to see if someone set the MCA param at run time to
     * disable MPI param checking
     */ 
    if (checking_params != 0) {
      String e = System.getenv("OMPI_MCA_mpi_param_check");
      if (null != e && 0 == Integer.parseInt(e)) {
	checking_params = 0;
      }
    }
    
    /* If we only have one rank, we must reduce the size to be not long
       (long requires the rendevouz protocol, which won't happen in the
       logic below -- the code will deadlock with one rank and long
       messages) */
    
    if (tasks == 1) {
      OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "This program expects more than " +
				    "one rank. I can still run\n" +
				    "with only one rank, but the test " +
				    "will not be as thorough.\n");
    }
    
    op = new Op(somewhatLess, commute);

    Int2.Data iData = MPI.int2.getData(info),
              rData = MPI.int2.getData(result);

    iData.putValue(me);
    iData.putIndex(1);
    
    root = 0;
    MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
    if(me == root)
      if(rData.getIndex() != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Reduce(1): result = " +
				    rData.getIndex() + ", should be 1\n");
    
    root = tasks/2;
    MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
    if(me == root)
      if(rData.getIndex() != 1)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Reduce(1): result = " +
				    rData.getIndex() + ", should be 1\n");
    
    /* Only run these tests if we have more than one rank */
    
    if (tasks > 1) {
      iData.putValue(tasks - me);
      root = tasks-1;
      MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
      if(me == root)
	if(rData.getIndex() != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Reduce(1): result = " +
				    rData.getIndex() + ", should be 0\n");
      
      iData.putValue(me);
      if(me == 0)  
	iData.putValue(tasks + 1);
      MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
      if(me == root)
	if(rData.getIndex() != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Reduce(1): result = " +
				    rData.getIndex() + ", should be 0\n");
    }
    
    temp = op;
    op.free();
    if(!op.isNull())
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Op_free: op not set " +
				  "to NULL\n");
    
    /* Do not run this test if we're not checking MPI API parameters */
    if (checking_params != 0) {
        try
        {
            MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
            throw new MPIException("Exception not thrown");
        }
        catch(MPIException ex)
        {
            if(ex.getErrorClass() != MPI.ERR_OP)
            {
                OmpitestError.ompitestError(
                        OmpitestError.getFileName(),
                        OmpitestError.getLineNumber(),
                        "WARNING in MPI_Op_free: "+
                        "error on NULL op not MPI_ERR_OP\n" +
                        "error returned was "+ ex.getErrorCode() +
                        "(" + ex.getErrorClass() + ")\n");
            }
        }
    }

    try
    {
        op = temp;
        MPI.COMM_WORLD.reduce(info, result, 1, MPI.INT2, op, root);
        throw new MPIException("Exception not thrown");
    }
    catch(MPIException ex)
    {
        if(ex.getErrorClass() != MPI.ERR_OP)
        {
            OmpitestError.ompitestError(
                    OmpitestError.getFileName(),
                    OmpitestError.getLineNumber(),
                    "WARNING in MPI_Op_free: "+
                    "error on op not freed not MPI_ERR_OP\n" +
                    "error returned was "+ ex.getErrorCode() +
                    "(" + ex.getErrorClass() + ")\n");
        }
    }

    op = new Op(somewhatLess, commute);
    temp = op;

    /* Do not run this test if we're not checking MPI API parameters */
    if (checking_params != 0) {
        try
        {
            op = MPI.SUM;
            op.free();
            throw new MPIException("Exception not thrown");
        }
        catch(MPIException ex)
        {
            if(ex.getErrorClass() != MPI.ERR_OP)
            {
                OmpitestError.ompitestError(
                        OmpitestError.getFileName(),
                        OmpitestError.getLineNumber(),
                        "WARNING in MPI_Op_free: " +
                        "error on free MPI_SUM not MPI_ERR_OP\n" +
                        "error returned was "+ ex.getMessage() +" ["+ 
                        ex.getErrorCode() +"("+ ex.getErrorClass() +")]\n");
            }
        }
    }

    /* Tidy up */
    temp.free();
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
  
  
  /*
   * Note that because MPI reduction operations are allowed to be
   * associative, this function does not necessarily guarantee that it
   * will actually measure "less" -- especially since it doesn't copy
   * the .data value to inout.  It only works with carefully constructed
   * data -- when we want it to come out to be false, we have to ensure
   * that the one element that will drive it to be false is not just
   * larger than its neighbor, but larger than the largest element in
   * the array.
   */
  private static final UserFunction somewhatLess = new UserFunction()
  {@Override public void call(ByteBuffer in, ByteBuffer inOut,
                              int count, Datatype dt) throws MPIException
  {
      if(dt != MPI.INT2)
      {
          OmpitestError.ompitestWarning(OmpitestError.getFileName(),
                                        OmpitestError.getLineNumber(),
                                        "ERROR in less: wrong data type\n");
      }
  
      for(int i = 0; i < count; i++)
      {
          Int2.Data inData    = MPI.int2.getData(in, i),
                    inOutData = MPI.int2.getData(inOut, i);

          boolean inOutFlag = inOutData.getIndex() != 0,
                  inFlag    = inData.getIndex() != 0;

          inOutFlag &= (inData.getValue() < inOutData.getValue()) && inFlag;
          inOutData.putIndex(inOutFlag ? 1 : 0);
      }
  }};

} // OpTest
