/* 
 *
 * This file is a port from "getel.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Getel.java			Author: S. Gross
 *
 */

/*
 * This test must be implemented with direct buffers because
 * sendRecv with arrays uses pack/unpack internally.
 * pack and unpack need complete data, incomplete data throw MPI_ERR_TRUNCATE.
 */

import java.nio.*;
import mpi.*;

public class Getel
{
  private final static int DB_TALK = 1;
  private final static int SIZEOF_DOUBLE = 8;
  private final static int SIZEOF_INT = 4;
  private final static int MSG_SIZE = 256;

  public static void main (String args[]) throws MPIException
  {
    ByteBuffer imessage = MPI.newByteBuffer(MSG_SIZE),
               omessage = MPI.newByteBuffer(MSG_SIZE); 
    int numtasks, me, count, error = 0;
    Datatype type_ia, doubleint;
    Status status;

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
    
    /* MPI_DOUBLE_INT may contain an explicit UB, which will mess these
     * tests up
     */
    doubleint = new DoubleInt().getType();
    type_ia = Datatype.createContiguous(4, doubleint);
    type_ia.commit();
    
    for (int i = 0; i < MSG_SIZE; i++) {
      omessage.put(i, (byte)i);
    }
    
    /* Note that this test can deadlock ... */
    status = MPI.COMM_WORLD.sendRecv(omessage, 33, MPI.BYTE, 0, 0,
				     imessage, 3, type_ia, 0, 0);
    count = status.getElements(type_ia);
    if (count != MPI.UNDEFINED) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -1- MPI_Get_elements should " +
				  "return MPI_UNDEFINED, not " +
				  count + ".\n");
      error++;
    }

    count = status.getElements(type_ia);
    if (count != MPI.UNDEFINED) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -1- MPI_Get_elements should " +
				  "return MPI_UNDEFINED, not " +
				  count + ".\n");
      error++;
    }

    status = MPI.COMM_WORLD.sendRecv(omessage,
				     5 * SIZEOF_DOUBLE + 4 * SIZEOF_INT,
				     MPI.BYTE, 0, 0,
				     imessage, 3, type_ia, 0, 0);
    count = status.getElements(type_ia);
    if (count != 9) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -2- MPI_Get_elements should " +
				  "return 9, not " + count + ".\n");
      error++;
    }
    
    count = status.getCount(type_ia);
    if (count != MPI.UNDEFINED) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -2- MPI_Get_count should " +
				  "return MPI_UNDEFINED, not " +
				  count + ".\n");
      error++;
    }
    
    status = MPI.COMM_WORLD.sendRecv(omessage,
				     5 * (SIZEOF_DOUBLE | SIZEOF_INT),
				     MPI.BYTE, 0, 0,
				     imessage, 3, type_ia, 0, 0);
    count = status.getElements(type_ia);
    if (count != 10) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -3- MPI_Get_elements should " +
				  "return 10, not " + count + ".\n");
      error++;
    }
    
    count = status.getCount(type_ia);
    if (count != MPI.UNDEFINED) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -3- MPI_Get_count should " +
				  "return MPI_UNDEFINED, not " +
				  count + ".\n");
      error++;
    }
    
    /* This one is definitivly not MPI correct send+recv to yourself
     * is something that can deadlock on some MPI implementations.
     * I will replace by a MPI_Sendrecv.
     * MPI_Send(omessage, 96, MPI_CHAR, 0, 0, MPI_COMM_WORLD);
     * MPI_Recv(imessage, 3, type_ia, 0, 0, MPI_COMM_WORLD, &status);
     */
    status = MPI.COMM_WORLD.sendRecv(omessage, 96, MPI.BYTE, 0, 0,
				     imessage, 3, type_ia, 0, 0);
    count = status.getElements(type_ia);
    if (count != 16) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -4- MPI_Get_elements should " +
				  "return 16, not " + count +".\n");
      error++;
    }

    count = status.getCount(type_ia);
    if (count != 2) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR -4- MPI_Get_count should " +
				  "return 2, not " + count + ".\n");
      error++;
    }
    
    if (error != 0)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Get_count/MPI_Get_elements " +
				  "test had " + error + " errors.\n");
    
    type_ia.free();
    doubleint.free();
    MPI.Finalize();
  }
  
  private static class DoubleInt extends Struct
  {
      int d = addDouble(),
          i = addInt();

      @Override protected Data newData()
      {
          return new Data();
      }
      
      private class Data extends Struct.Data
      {
          double getDouble() { return getDouble(d); }
          int    getInt()    { return getInt(i);    }

          void putDouble(double v) { putDouble(d, v); }
          void putInt(int v)       { putInt(i, v);    }
      }
  }
}
