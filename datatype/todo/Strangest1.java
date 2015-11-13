/* 
 *
 * This file is a port from "strangest1.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Strangest1.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Strangest1
{
  private final static int DB_TALK = 1;
  private final static int MSZ = 16;

  public static void main (String args[]) throws MPIException
  {
    byte[]     imessage = new byte[MSZ]; /* pack/unpack need byte buffer */
    ByteBuffer omessage = MPI.newByteBuffer(MSZ),
               xmessage = MPI.newByteBuffer(MSZ);

    //    MPI_Aint      aod[5], extent, lb, ub;
    int myrank, me, numtasks, extent, lb, ub, tmp,
	tmp2, tmp3, error=0;

    /* Java uses "length" to determine the number of elements in an
     * array, so that we need an arrays with four elements.
     */
    int aob[] = new int[4],
	aod[] = new int[4];
    Datatype aot[] = new Datatype[4];
    Datatype newtype0;

    for (int i = 0; i < MSZ; i++) {
      omessage.put(i, (byte)((i % 255) + 1));
      imessage[i] = (byte) 0;
      xmessage.put(i, (byte)0);
    }

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    myrank = MPI.COMM_WORLD.getRank();
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
    
    aob[0] = 1;
    aot[0] = MPI.LB;
    aod[0] = 0;
    aob[1] = 1;
    aot[1] = MPI.SHORT;
    aod[1] = 0;
    aob[2] = 1;
    aot[2] = MPI.SHORT;
    aod[2] = 8;
    aob[3] = 1;
    aot[3] = MPI.UB;
    aod[3] = 2; 
    newtype0 = Datatype.createStruct(aob, aod, aot);
    newtype0.commit();
    
    extent = newtype0.getExtent();
    lb = newtype0.getLb();
    ub = lb + extent;
    if (DB_TALK != 0) {
      System.out.printf("extent= %d, lb= %d, ub= %d.\n",
			extent, lb, ub);
    }

    MPI.COMM_WORLD.pack(omessage, 4, newtype0, imessage, 0);
    MPI.COMM_WORLD.unpack(imessage, 0, xmessage, 4, newtype0);

    for (int i = 0; i < MSZ; i++)
      if (omessage.get(i) != xmessage.get(i))
        error++;
    if (error != 0) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "FAIL: results below.\n");
      for (int i = 0; i < MSZ; i++) {
        tmp  = omessage.get(i); 
        tmp2 = imessage[i];
        tmp3 = xmessage.get(i);
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "idx " + i + ", original " + tmp +
				    ", packval " + tmp2 +
				    ", unpackval " + tmp3 + "\n");
      }
    }
    
    newtype0.free();
    if (!newtype0.isNull()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "MPI_Type_free test FAILED.\n");
    }
    MPI.Finalize();
  }
}
