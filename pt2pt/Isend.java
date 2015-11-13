/* 
 *
 * This file is a port from "isend.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Isend.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Isend
{
  private static int me, tasks, bytes;
  private static IntBuffer indata  = MPI.newIntBuffer(1000);
  private static IntBuffer outdata = MPI.newIntBuffer(1000);
  private static Request req[];

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    req = new Request[1000];

    for (int i = 0; i < tasks; i++) {
      outdata.put(i, me);
      indata.put(i, -1);
    }

    for (int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.iSend(slice(outdata, i), 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(indata, i), 1, MPI.INT, i, 1);
    }
    wstart();

    for (int i = 0; i < tasks; i++)  
      indata.put(i, -1);
    for (int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.isSend(slice(outdata, i), 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(indata, i), 1, MPI.INT, i, 1);
    }
    wstart();

    for (int i = 0; i < tasks; i++)  
      indata.put(i, -1);
    for (int i = 0; i < tasks; i++)
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(indata, i), 1, MPI.INT, i, 1);
    MPI.COMM_WORLD.barrier ();
    for (int i = 0; i < tasks; i++)
      req[2*i] = MPI.COMM_WORLD.irSend(slice(outdata, i), 1, MPI.INT, i, 1);
    wstart();

    for (int i = 0; i < tasks; i++)  
      indata.put(i, -1);
    for (int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.ibSend(slice(outdata, i), 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.iRecv(slice(indata, i), 1, MPI.INT, i, 1);
    }
    wstart();

    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }

 
  private static void wstart() throws MPIException
  {
    Status[] stats = Request.waitAllStatus(Arrays.copyOf(req, 2 * tasks));
    
    for (int i = 0; i < tasks; ++i)
      if (indata.get(i) != i)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in Waitall: data is " +
				    indata.get(i) + ", should be " +
				    i +"\n");
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (int i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].getCount(MPI.BYTE);
      if (bytes != 4)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in Waitall/getcount: " +
				    "bytes = " + bytes + ", should " +
				    "be 4\n");
    }
  }
}
