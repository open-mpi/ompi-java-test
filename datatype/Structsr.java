/* 
 *
 * This file is a port from "structsr.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Structsr.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Structsr
{
  private final static int DB_TALK = 1;
  private final static int MSZ = 2000;
  private final static int CHKSZ = 200;
  private final static int SIZEOF_INT = 4;

  public static void main (String args[]) throws MPIException
  {
    ByteBuffer omessage = MPI.newByteBuffer(MSZ),
	       xmessage = MPI.newByteBuffer(MSZ);
    //    MPI_Aint extent;
    int myrank, me, other, numtasks, size, extent,
	tmp, tmp3, error = 0;
    //    int *ip, *op;
    int checkmask[] = new int[CHKSZ];
    Datatype newtype1, newtype2, shortint;
    
    for (int i = 0; i < MSZ; i++) {
      omessage.put(i, (byte)((i % 255) + 1));
      xmessage.put(i, (byte)0);
    }

    MPI.Init(args);
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
    
    myrank = MPI.COMM_WORLD.getRank();
    other = (myrank != 0) ? 0 : 1;
    
    /* This test only works in a homogeneous environment. Find out
     * if my peer is homogeneous (endian-wise) with me. If it's not,
     * then just change my "peer" to be myself.
     */
    //    ip = (int*) omessage;
    //    op = (int*) xmessage;
    //    *ip = 1;
    //    MPI_Sendrecv(ip, sizeof(int), MPI.BYTE, other, 123,
    //    		 op, sizeof(int), MPI.BYTE, other, 123,
    //    		 MPI.COMM_WORLD, MPI.STATUS_IGNORE);
    //    if (*ip != *op)
    //      other = myrank;
    MPI.COMM_WORLD.sendRecv(omessage, 1, MPI.INT, other, 123,
			    xmessage, 1, MPI.INT, other, 123);
    if (omessage.getInt(0) != xmessage.getInt(0)) {
      other = myrank;
    }
    /* Typemap for MPI_SHORT_INT is: { {short,0) (int,4) } extent==8
     * MPI_SHORT_INT may contain an explicit UB, which will mess
     * these tests up
     */
    TShortInt tShortInt = new TShortInt();
    shortint = tShortInt.getType();
    newtype1 = Datatype.createContiguous(2, shortint);

    /* Typemap for newtype1 is: 
     * { (short,0) (int,4) (short,8) (int,12) } extent 16
     */
    extent = newtype1.getExtent();
    size = newtype1.getSize();
    if ((extent != 16) || (size != 12))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "OOPS: newtype1, extent = " +
				  extent + ", size = " +
				  size + ".\n");

    TStruct2 tStruct2 = new TStruct2(newtype1, shortint);
    newtype2 = tStruct2.getType();

    /* Typemap for newtype2 is:
     * { (int,0) (int,4)
     * (short,0+16) (int,4+16) (short,8+16) (int,12+16)
     * (short,0+64) (int,4+64) (short,8+64) (int,12+64)  } 
     * extent==80 
     * gaps are at: 8-15, 18-19, 26-27, 32-63, 66-67 74-75
     */
    for (int i = 0; i < CHKSZ; i++) {
      checkmask[i] = 1;
      if ((i >= 8) && (i <= 15))
	checkmask[i] = 0;
      if ((i >= 18) && (i <= 19))
	checkmask[i] = 0;
      if ((i >= 26) && (i <= 27))
	checkmask[i] = 0;
      if ((i >= 32) && (i <= 63))
	checkmask[i] = 0;
      if ((i >= 66) && (i <= 67))
	checkmask[i] = 0;
      if ((i >= 74) && (i <= 75))
	checkmask[i] = 0;
      if ((i >= 8 + 80) && (i <= 15 + 80))
	checkmask[i] = 0;
      if ((i >= 18 + 80) && (i <= 19 + 80))
	checkmask[i] = 0;
      if ((i >= 26 + 80) && (i <= 27 + 80))
	checkmask[i] = 0;
      if ((i >= 32 + 80) && (i <= 63 + 80))
	checkmask[i] = 0;
      if ((i >= 66 + 80) && (i <= 67 + 80))
	checkmask[i] = 0;
      if ((i >= 74 + 80) && (i <= 75 + 80))
	checkmask[i] = 0;
    }
    extent = newtype2.getExtent();
    size = newtype2.getSize();
    if ((extent != 80) || (size != 32))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "OOPS: newtype2, extent = " +
				  extent + ", size = " +
				  size + ".\n");
    for (int i = 0; i < MSZ; i++) {
      xmessage.put(i, (byte)0);
    }
    
    MPI.COMM_WORLD.sendRecv(omessage, 2, newtype2, other, 321,
			    xmessage, 2, newtype2, other, 321);
    
    for (int i = 0; i < extent * 2; i++) {
      tmp  = omessage.get(i);
      tmp3 = xmessage.get(i);
      if ((checkmask[i] == 0) && (tmp3 != 0)) {
	error++;
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Byte# " + i + " should have " +
				    "remained 0 but was " + tmp3 +
				    ".\n");
      }
      if ((checkmask[i] != 0) && (tmp != tmp3)) {
	error++;
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Byte# " + i + " should have been " +
				    tmp + " but was " + tmp3 + ".\n");
      }
      /* Restore this printf to see the whole pattern if you need to
       * debug .... 
       * if (myrank == 0)
       *   System.out.printf("idx %#4d  original %#4d  unpackval %#4d\n",
       *		     i, tmp, tmp3);
       */
    }
    newtype1.free();
    if (!newtype1.isNull()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Type_free test FAILED.\n");
    }
    newtype2.free();
    shortint.free();
    MPI.Finalize();
  }

  private static class TShortInt extends Struct
  {
      private final int fs = addShort(),
                        fi = setOffset(4).addInt();
      /*
      aob2[0] = 1;
      aod2[0] = 0;
      aot2[0] = MPI.SHORT;
      aob2[1] = 1;
      aod2[1] = SIZEOF_INT;
      aot2[1] = MPI.INT;
      */

      @Override protected Data newData() { return new Data(); }
      
      private class Data extends Struct.Data
      {
          // These methods are not needed but show how to access to data.
          public short getShort() { return getShort(fs); }
          public int   getInt()   { return getInt(fi);   }

          public void putShort(short v) { putShort(fs, v); }
          public void putInt(int v)     { putInt(fi, v);   }
      } // Data
  } // TShortInt
  
  private static class TStruct2 extends Struct
  {
      private final Datatype type1, shortint;
      private final int fi, ft1, fsi;
      
      /*
      aob3[0] = 2;
      aod3[0] = 0;
      aot3[0] = MPI.INT;
      aob3[1] = 1;
      aod3[1] = 16;
      aot3[1] = newtype1;
      aob3[2] = 2;
      aod3[2] = 64;
      aot3[2] = shortint;
      */

      private TStruct2(Datatype type1, Datatype shortint) throws MPIException
      {
          this.type1    = type1;
          this.shortint = shortint;

          fi  = addInt(2);
          ft1 = setOffset(16).addData(type1);
          fsi = setOffset(64).addData(shortint, 2);
      }
      
      @Override protected Data newData() { return new Data(); }

      private class Data extends Struct.Data
      {
          //These methods are not needed but show how to access to data.
          public ByteBuffer getData1()  { return getBuffer(type1, ft1);    }
          public ByteBuffer getDataSI() { return getBuffer(shortint, fsi); }

          public int   getIntField(int i) { return getInt(fi, i); }
          public short getShortField()    { return getShort(fsi); }

          public void putIntField(int i, int v) { putInt(fi, i, v); }
          public void putShortField(short v)    { putShort(fsi, v); }
      } // Data
  } // TStruct2
}
