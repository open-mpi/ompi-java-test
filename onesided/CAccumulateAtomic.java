/* 
 *
 * This file is a port from "c_accumulate_atomic.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: CAccumulateAtomic.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CAccumulateAtomic
{
  private final static int WINDOW_SIZE = 128;
  private final static int REPS = 100;

  public static void main (String args[]) throws MPIException
  {
    int rank, size;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();

    //MPI_Alloc_mem(sizeof(int) * WINDOW_SIZE * 2, MPI.INFO_NULL, &buffer);
    IntBuffer buffer = MPI.newIntBuffer(WINDOW_SIZE * 2);
    
    /* initialize bottom half to 1 and top half to 0 */
    for (int i = 0 ; i < WINDOW_SIZE ; ++i) {
      buffer.put(i, 1);
      buffer.put(i + WINDOW_SIZE, 0);
    }
    
    /* create window */
    Win win = new Win(buffer, WINDOW_SIZE*2, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    
    /* everyone updates root's upper half REPS * 2 times */
    win.fence(MPI.MODE_NOPRECEDE|MPI.MODE_NOSTORE);
    
    for (int i = 0 ; i < REPS ; ++i) {
      win.accumulate(buffer, WINDOW_SIZE, MPI.INT,
		     0, WINDOW_SIZE, WINDOW_SIZE, MPI.INT, MPI.SUM);
    }
    
    win.fence(0);
    
    for (int i = 0 ; i < REPS ; ++i) {
      win.accumulate(buffer, WINDOW_SIZE, MPI.INT,
		     0, WINDOW_SIZE, WINDOW_SIZE, MPI.INT, MPI.SUM);
    }
    
    win.fence(MPI.MODE_NOSUCCEED|MPI.MODE_NOPUT);
    
    /* check result */
    for (int i = 0 ; i < WINDOW_SIZE ; ++i) {
      if (buffer.get(i) != 1) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Accumulate appears to have " +
				    "failed. Found " + buffer.get(i) +
				    " at " + i + ", expected 1.\n");
      }
    }
    
    if (0 == rank) {
      for (int i = WINDOW_SIZE ; i < WINDOW_SIZE * 2 ; ++i) {
	if (buffer.get(i) != size * REPS * 2) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "Accumulate appears to have " +
				      "failed. Found " + buffer.get(i) +
				      " at " + i + ", expected " +
				      (size * REPS * 2) + ".\n");
	}
      }
    }
    
    /* cleanup */
    win.free();
    //MPI_Free_mem(buffer);
    MPI.Finalize();
  }
}
