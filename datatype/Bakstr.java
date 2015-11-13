/* 
 *
 * This file is a port from "bakstr.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Bakstr.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;
import static mpi.MPI.slice;

public class Bakstr
{
  private final static int DB_TALK = 1;
  private final static int MSZ = 96;

  public static void main (String args[]) throws MPIException
  {
    byte[]     imessage = new byte[MSZ]; /* pack/unpack need byte buffer */
    ByteBuffer omessage = MPI.newByteBuffer(MSZ),
               xmessage = MPI.newByteBuffer(MSZ);

    int numtasks, me, error = 0,
	tmp, tmp2, tmp3, tmp4;
    int checkmask[] = new int[MSZ];
    int checkpack[] = {
      49,50, 45,46, 41,42, 37,38, 33,34, 29,30, 25,26, 21,22, 17,18,
      13,14,  9,10,  5, 6,
      95,96, 91,92, 87,88, 83,84, 79,80, 75,76, 71,72, 67,68, 63,64,
      59,60, 55,56, 51,52
    };
    //    MPI_Aint      extent,lb,ub;
    int extent, lb;
    Datatype newtype0;

    for (int i = 0; i < MSZ; i++) {
      omessage.put(i, (byte)((i % 255) + 1));
      imessage[i] = (byte) 0;
      xmessage.put(i, (byte)0);
    }
    checkmask[0] = 0;
    checkmask[1] = 0;
    
    for (int i = 2; i <= 46; i += 4) {
      checkmask[i]   = 0;
      checkmask[i+1] = 0;
      checkmask[i+2] = 1;
      checkmask[i+3] = 1;
    }
    for (int i = 48; i <= 92; i += 4) {
      checkmask[i]   = 0;
      checkmask[i+1] = 0;
      checkmask[i+2] = 1;
      checkmask[i+3] = 1;
    } 
    checkmask[48] = 1;
    checkmask[49] = 1;
    
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

    newtype0 = Datatype.createVector(12, 1, -2, MPI.SHORT);
    newtype0.commit();
    
    extent = newtype0.getExtent();
    lb = newtype0.getLb();

    if (DB_TALK != 0) {
      System.out.printf("extent= %d, lb= %d.\n", extent, lb);
      System.out.printf("The first gather runs back from &buf+%2d " +
			"and the second from &buf+%2d.\n",
			MSZ/2, MSZ/2+extent);
    }

    MPI.COMM_WORLD.pack(slice(omessage, MSZ/2), 2, newtype0, imessage, 0);
    MPI.COMM_WORLD.unpack(imessage, 0, slice(xmessage, MSZ/2), 2, newtype0);

    for (int i = 0; i < checkpack.length; i++) {
      if (checkpack[i] != imessage[i]) {
        error++;
      }
    }
    for (int i = 0; i < MSZ; i++) {
      if (checkmask[i] != 0) {
        if (omessage.get(i) != xmessage.get(i)) {
          error++;
        }
      } else {
        if (xmessage.get(i) != 0)  {
          error++;
        }
      }
    }
    if (error != 0) {
      for (int i = 0; i < MSZ; i++) {
        tmp  = omessage.get(i); 
        tmp2 = imessage[i];
        tmp3 = xmessage.get(i);
        if (i < 48)
          tmp4 = checkpack[i];
        else
          tmp4 = 0;
        String mark;
        if (checkmask[i] != 0) {
          mark = "<-";
        } else {
          mark = "";
        }
	OmpitestError.ompitestWarning(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "idx " + i + "; original " + tmp +
				      "; packval " + tmp2 + " (" + tmp4 +
				      "); unpackval " + tmp3 + " " +
				      mark + "\n");
      }
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Found " + error + " errors out " +
				  "of " + MSZ + "\n");
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
