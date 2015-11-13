/*
 *
 * This file is a port from "iallreduce.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Iallreduce.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class Iallreduce
{
  private final static int MAXLEN = 100000;

  public static void main (String args[]) throws MPIException
  {
    int myself, tasks;
    IntBuffer out = MPI.newIntBuffer(MAXLEN),
              in  = MPI.newIntBuffer(MAXLEN);
    Request request;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    for (int j = 1; j <= MAXLEN; j *= 10)  {
      for (int i = 0; i < j; i++) {
	out.put(i, i);
      }

      request = MPI.COMM_WORLD.iAllReduce(out, in, j, MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();

      for (int k = 0; k < j; k++) {
	if (in.get(k) != k * tasks) {
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + in.get(k) +
				      ") at index " + k +
				      " of " + j + " (should be " +
				      (k * tasks) + ")\n");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
