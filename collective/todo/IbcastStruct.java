/* 
 *
 * This file is a port from "ibcast_struct.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IbcastStruct.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IbcastStruct
{
  private final static int MAX_SIZE = 1000000;

  public static void main (String args[]) throws MPIException
  {
    int myself, size, i, nseconds = 600;
    boolean done_flag[] = new boolean[1];
    double t_stop;
    Datatype newtype, t1, t2;
    Request request;
    /* Java doesn't support addresses, so that we must place "ii"
     * and "a" in a structure, when we want to use them in a new
     * datatype.
     *
     * int ii;
     * double a[] = new double[2];
     * MPI_Address(&ii, &disp[0]);
     * MPI_Address(a, &disp[1]);
     */
    StructIntDoubleArray iDaType = new StructIntDoubleArray();
    ByteBuffer iDaBuf  = MPI.newByteBuffer(iDaType.getExtent());
    StructIntDoubleArray.Data iDaData = iDaType.getData(iDaBuf, 0);
    //    struct foo_t {
    //      int i[3];
    //      double d[3];
    //    } foo, *bar;
    //    bar = malloc(sizeof(foo) * MAX_SIZE);
    StructIntAndDoubleArrays iaDaType = new StructIntAndDoubleArrays();
    ByteBuffer fooBuf = MPI.newByteBuffer(iaDaType.getExtent()),
	       barBuf = MPI.newByteBuffer(iaDaType.getExtent()*MAX_SIZE);
    StructIntAndDoubleArrays.Data foo = iaDaType.getData(fooBuf, 0);

    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    
    if (args.length > 0) {
      nseconds = Integer.parseInt(args[0]);
    }
    t_stop = MPI.wtime() + nseconds;
    done_flag[0] = false;
    
    /*-------------------------------------------------------------*/
    /* Build a datatype that probably has holes */
    
    newtype = new StructIntDoubleArray().getType();
    newtype.commit();

    if(myself == 0) { 
      iDaData.putI(2);
      iDaData.putD(0, 123.456);
      iDaData.putD(1, 456.123);
    }
    request = MPI.COMM_WORLD.iBcast(MPI.BOTTOM, 1, newtype, 0);
    request.waitFor();
    request.free();
    if (iDaData.getI() != 2 ||
	iDaData.getD(0) != 123.456 ||
	iDaData.getD(1) != 456.123) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR! " + iDaData.getI() +
				  iDaData.getD(0) + iDaData.getD(1) +
				  "\n");
    }
    newtype.free();
    
    /*-------------------------------------------------------------*/
    /* Build a datatype that is guaranteed to have holes; broadcast
     * large numbers of them
     */

    t1 = Datatype.createVector(2, 1, 2, MPI.INT);
    t1.commit();
    t2 = Datatype.createVector(2, 1, 2, MPI.DOUBLE);
    t2.commit();
    
    len[0] = 1;
    len[1] = 1;
    MPI_Address(&foo.i[0], &disp[0]);
    MPI_Address(&foo.d[0], &disp[1]);
    disp[0] -= (MPI_Aint) &foo;
    disp[1] -= (MPI_Aint) &foo;
    type[0] = t1;
    type[1] = t2;
    newtype = Datatype.createStruct(len, disp, type);
    newtype.commit();
    
    if (0 == myself) {
      foo.putI(0, 123);
      foo.putI(1, 456);
      foo.putI(2, 789);
      foo.putD(0, 123.456);
      foo.putD(1, 456.789);
      foo.putD(2, 789.123);
      //      MPI_Send(&foo, 1, newtype, 1, 1234, MPI.COMM_WORLD);
      MPI.COMM_WORLD.send (fooBuf, 1, newtype, 1, 123);
    } else if (1 == myself) {
      foo.putI(0, 0);
      foo.putI(1, 0);
      foo.putI(2, 0);
      foo.putD(0, 0.0);
      foo.putD(1, 0.0);
      foo.putD(2, 0.0);
      //      MPI_Recv(&foo, 1, newtype, 0, 1234, MPI.COMM_WORLD,
      //	       MPI.STATUS_IGNORE);
      MPI.COMM_WORLD.recv(fooBuf, 1, newtype, 0, 1234);
      if (foo.getI(0) != 123 ||
	  foo.getI(1) != 0 ||
	  foo.getI(2) != 789 ||
	  foo.getD(0) != 123.456 ||
	  foo.getD(1) != 0.0 ||
	  foo.getD(2) != 789.123) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Simple receive failed: got (" +
				    foo.getI(0) + "," +
				    foo.getI(1) + "," +
				    foo.getI(2) + "," +
				    foo.getD(0) + "," +
				    foo.getD(1) + "," +
				    foo.getD(2) + "), expected \n(" +
				    "123, 0, 789, 123.456, 0.0, " +
				    "789.123)\n");
      }
    }
    MPI.COMM_WORLD.barrier();
    
    
    /*
     * We want to loop over many messages sizes and all roots.
     *
     * We also impose a time limit, however.
     *
     * In case the test takes too long, we want to get reasonable
     * coverage early on and, time willing, fill in the gaps later.
     * So the innermost loop iterates over both message size and root.
     */
    for (int i0 = 1; (i0 <= MAX_SIZE) && !done_flag[0]; i0 *= 10) {
      i = i0;
      for (int root = 0; (root < size) && !done_flag[0]; ++root) {
	
	/* Initialize buffers */
	for (int j = 0; j < i; ++j) {
	  StructIntAndDoubleArrays.Data bar =
	    iaDaType.getData(barBuf, j);
	  if (root == myself) {
	    bar.putI(0, 123);
	    bar.putI(1, 456);
	    bar.putI(2, 789);
	    bar.putD(0, 123.456);
	    bar.putD(1, 456.789);
	    bar.putD(2, 789.123);
	  } else {
	    bar.putI(0, 0);
	    bar.putI(1, 0);
	    bar.putI(2, 0);
	    bar.putD(0, 0.0);
	    bar.putD(1, 0.0);
	    bar.putD(2, 0.0);
	  }
	}
	
	/* Broadcast */
	//	MPI_Ibcast(bar, i, newtype, root, MPI.COMM_WORLD,
	//		   &request);
	request = MPI.COMM_WORLD.iBcast(barBuf, i, newtype, root);
	request.waitFor();
	request.free();
	
	/* Make sure we got the right values */
	if (root != myself) {
	  for (int j = 0; j < i; ++j) {
	    StructIntAndDoubleArrays.Data bar =
	      iaDaType.getData(barBuf, j);
	    if (bar.getI(0) != 123 ||
		bar.getI(1) != 0 ||
		bar.getI(2) != 789 ||
		bar.getD(0) != 123.456 ||
		bar.getD(1) != 0.0 ||
		bar.getD(2) != 789.123) {
	      OmpitestError.ompitestError(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "ERROR! root=" + root +
					  ", me=" + myself + ", count=" +
					  i + ", position=" + j +
					  ", got (" +
					  bar.getI(0) + "," +
					  bar.getI(1) + "," +
					  bar.getI(2) + "," +
					  bar.getD(0) + "," +
					  bar.getD(1) + "," +
					  bar.getD(2) +
					  "), expected \n(" +
					  "123, 0, 789, 123.456, 0.0, " +
					  "789.123)\n");
	    }
	  }
	}
	
	/* Iterate over message size */
	i *= 10;
	if (i > MAX_SIZE) {
	  i = 1;
	  /* Use this occasion to check the time limit (on process 0) */
	  done_flag[0] = (MPI.wtime() > t_stop);
	  request = MPI.COMM_WORLD.iBcast(done_flag, 1, MPI.BOOLEAN, 0);
	  request.waitFor();
	  request.free();
	  //	  if (done_flag[0]) goto done;
	}
      }
    }
    
    //    done:
    t1.free();
    t2.free();
    newtype.free();
    
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }


  /* Java doesn't support addresses, so that we must place "ii"
   * and "a" in a structure, when we want to use them in a new
   * datatype.
   *
   * int ii;
   * double a[] = new double[2];
   * MPI_Address(&ii, &disp[0]);
   * MPI_Address(a, &disp[1]);
   *
   */
  private static class StructIntDoubleArray extends Struct
  {
    /* This section defines the offsets of the fields
     * ip: int position
     * dp: double array position
     */
    int ip = addInt();
    int dp = addDouble(2);
    
    /* This method tells the super class how to create a data object	*/
    @Override protected Data newData() { return new Data(); }
    
    private class Data extends Struct.Data
    {
      /* These methods read an array element from the buffer
       * idx: index
       */
      int    getI()	   { return getInt(ip, idx); }
      double getD(int idx) { return getDouble(dp, idx); }
      
      /* These methods write an array element to the buffer
       * idx: index
       * val: value
       */
      void putI(int val)	     { putInt(ip, idx, val); }
      void putD(int idx, double val) { putDouble(dp, idx, val); }
    }
  }


  /* Datatype for the following C structure.
   *
   * struct foo_t {
   *   int i[3];
   *   double d[3];
   * };
   *
   */
  private static class StructIntAndDoubleArrays extends Struct
  {
    /* This section defines the offsets of the fields
     * ip: int array position
     * dp: double array position
     */
    int ip = addInt(3);
    int dp = addDouble(3);
    
    /* This method tells the super class how to create a data object	*/
    @Override protected Data newData() { return new Data(); }
    
    private class Data extends Struct.Data
    {
      /* These methods read an array element from the buffer
       * idx: index
       */
      int    getI(int idx) { return getInt(ip, idx); }
      double getD(int idx) { return getDouble(dp, idx); }
      
      /* These methods write an array element to the buffer
       * idx: index
       * val: value
       */
      void putI(int idx, int val)    { putInt(ip, idx, val); }
      void putD(int idx, double val) { putDouble(dp, idx, val); }
    }
  }
}
