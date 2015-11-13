/* 
 *
 * This file is a port from "start.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Start.java			Author: S. Gross
 *
 */

import java.nio.*;
import java.util.*;
import mpi.*;
import static mpi.MPI.slice;

public class Start
{
  private static int tasks, bytes;
  private static IntBuffer me, data;
  private static byte buf[];
  private static Prequest req[];

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    me = MPI.newIntBuffer(1);
    me.put(0, MPI.COMM_WORLD.getRank());
    tasks = MPI.COMM_WORLD.getSize();
    data = MPI.newIntBuffer(1000 + MPI.BSEND_OVERHEAD);
    buf = new byte[10000 + MPI.BSEND_OVERHEAD];
    req = new Prequest[1000];

    MPI.attachBuffer(buf);
    for(int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.sendInit(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.recvInit(slice(data, i), 1, MPI.INT, i, 1);
    }
    /* System.out.printf( "Testing send/recv init\n" ); */
    wstart();
    
    for(int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.sSendInit(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.recvInit(slice(data, i), 1, MPI.INT, i, 1);
    }
    /* System.out.printf( "Testing ssend init\n" ); */
    wstart();
    
    for(int i = 0; i < tasks; i++) {
      req[2*i] = MPI.COMM_WORLD.rSendInit(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.recvInit(slice(data, i), 1, MPI.INT, i, 1);
    }
    /* System.out.printf( "Testing rsend init\n" ); */
    wstart();
    
    for(int i = 0; i < tasks; i++)  {
      req[2*i] = MPI.COMM_WORLD.bSendInit(me, 1, MPI.INT, i, 1);
      req[2*i+1] = MPI.COMM_WORLD.recvInit(slice(data, i), 1, MPI.INT, i, 1);
    }
    /* System.out.printf( "Testing bsend init\n" ); */
    wstart();
    
    MPI.COMM_WORLD.barrier ();
    MPI.Finalize();
  }
  
  
  private static void wstart() throws MPIException
  {
    for(int i = 0; i < tasks; i++) {
      data.put(i, -1);
    }
    
    MPI.COMM_WORLD.barrier ();
    
    for(int i = 0; i < 2*tasks; i++) {
      req[i].start();
    }
    Status stats[] = Request.waitAllStatus(Arrays.copyOf(req, 2 * tasks));
    
    for(int i = 0; i < tasks; i++)
      if(data.get(i) != i)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in Startall: data is " +
				    data.get(i) + ", should be " + i + "\n");
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for(int i = 1; i < 2*tasks; i+=2) {
      bytes = stats[i].getCount(MPI.BYTE);
      if(bytes != 4)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in Waitall: bytes = " +
				    bytes + ", should be 4\n");
    }
    for (int i = 0; i < 2*tasks; i++)
      req[i].free();
  }
}
