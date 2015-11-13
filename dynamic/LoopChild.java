/* 
 *
 * This file is a port from "loop_child.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: LoopChild.java			Author: S. Gross
 *
 */

import java.lang.management.*;
import mpi.*;

public class LoopChild
{
  public static void main (String args[]) throws MPIException
  {
    Intercomm parent;
    Comm merged;
    int cwrank, rank;
    int size;

    MPI.Init(args);
    cwrank = MPI.COMM_WORLD.getRank();
    if (0 == cwrank && args.length > 0) {
      System.out.printf("Child: launch\n");
    }

    /* Our test harness may run this program independently of the
     *  parent, so make it safe to do so
     */
    parent = Intercomm.getParent();
    if (!parent.isNull()) {
      merged = parent.merge(true);
      rank = merged.getRank();
      size = merged.getSize();
      if (0 == cwrank && args.length > 0) {
	System.out.printf("Child merged rank = %d, size = %d\n",
			  rank, size);
      }
        
      merged.free();
      parent.disconnect();
    }

    MPI.Finalize();
    if (0 == cwrank && args.length > 0) {
      /* Try to get the process ID	*/
      String jvmName = ManagementFactory.getRuntimeMXBean().getName();
      int at = jvmName.indexOf('@');
      long pid;

      try {
	pid = Long.parseLong(jvmName.substring(0, at));
      }
      catch (NumberFormatException e)
      {
	pid = -1;
      }
      catch (StringIndexOutOfBoundsException e)
      {
	pid = -1;
      }
      // printf("Child %d: exiting\n", (int)getpid());
     System.out.printf("Child %d: exiting\n", pid);
    }
  }
}
