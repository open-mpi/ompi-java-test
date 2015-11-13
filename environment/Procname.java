/* 
 *
 * This file is a port from "procname.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Procname.java			Author: S. Gross
 *
 */

import java.net.*;
import mpi.*;

public class Procname
{
  public static void main (String args[]) throws MPIException,
						 UnknownHostException
  {
    int me, len;
    String name, tmp;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    
    name = MPI.getProcessorName ();
    tmp = InetAddress.getLocalHost().getHostName();
    if(!name.equals(tmp))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR, processor name = " +
				  name + ", should be " + tmp + "\n");
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
