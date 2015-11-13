/* 
 *
 * This file is a port from "no-disconnect.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: NoDisconnect.java		Author: S. Gross
 *
 */

/*
 * README PLEASE!
 * 
 * This test needs the environment variable CLASSPATH
 * in order to find this class and mpi.jar
 */
import java.lang.management.*;
import java.util.*;
import java.nio.*;
import mpi.*;

public class NoDisconnect
{
  private final static int NCHARS = 30;
  private final static int MAX_DEPTH = 4;

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    CharBuffer bufs;				/* send buffer  */
    /* bufr[0] is the first buffer and bufr[1] is the second
     * buffer for a char array. In this special case we can use
     * java multi-dimensional arrays, because we only need one
     * row at a time.
     */
    CharBuffer bufr[] = new CharBuffer[2];	/* recv buffers */
    for(int i = 0; i < bufr.length; i++)
        bufr[i] = MPI.newCharBuffer(NCHARS);
    
    Intercomm parent;
    int level = 0, participate = 1;

    System.out.printf("Verify that this test is truly working\n" +
		      "because concurrent MPI_Comm_spawns\n" +
		      "have not worked before.\n");

    MPI.Init(args);
    parent = Intercomm.getParent();
    
    if (!parent.isNull()) {
      /* spawned processes get stuff from parent */
      level = Integer.parseInt(args[0]);
      //      MPI_Recv(&bufr[0], sizeof(char)*NCHARS, MPI_CHAR, MPI_ANY_SOURCE, 
      //       MPI_ANY_TAG, parent, MPI_STATUS_IGNORE);
      parent.recv(bufr[0], NCHARS, MPI.CHAR, MPI.ANY_SOURCE, MPI.ANY_TAG);
      System.out.printf("Parent sent: %s\n", toString(bufr[0]));
    }
    else {      
      /* original processes have to decide whether to participate */
      
      /* In this test, each process launched by "mpirun -n <np>" spawns a
       * binary tree of processes.  You end up with <np> * (1 << MAX_DEPTH)
       * processes altogether.  For MAX_DEPTH=4, this means 16*<np>.  There
       * is potential here for heavy oversubscription, especially if in
       * testing we launch tests with <np> set to the number of available
       * processors.  This test tolerates oversubscription somewhat since
       * it entails little inter-process synchronization.  Nevertheless,
       * we try to idle all but <np>/4 of the original processes, using a
       * minimum of at least two processes
       */
      
      int me, np;

      me = MPI.COMM_WORLD.getRank();
      np = MPI.COMM_WORLD.getSize();
      
      if (np > 4) {
	/* turn off all but every 4th process */
	if ((me & 3) != 0) participate = 0;
      } else
        if (np > 2) {
	  /* turn off all but every 2nd process */
	  if ((me & 1) != 0) participate = 0;
        }
    }
    
    /* all spawned processes and selected "root" processes participate */
    if (participate == 1) {
      System.out.printf("level = %d\n", level);
      
      /* prepare send buffer */
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
      //      sprintf(bufs,"level %d (pid:%d)", level, getpid());
      StringBuilder myString = new StringBuilder();
      myString.append("level " + String.valueOf(level));
      myString.append(" (pid: " + String.valueOf(pid) + ")");

      while(myString.length() < NCHARS)
          myString.append(' ');

      bufs = MPI.newCharBuffer(NCHARS);
      bufs.put(myString.toString().toCharArray());

      /* spawn */
      if (level < MAX_DEPTH) {
	int i, nspawn = 2, errcodes[] = new int[1];
	Request req[] = new Request[2];
	Comm   comm[] = new Comm[2];

        /* prepare command line arguments */
	String myArgs[] = {
            NoDisconnect.class.getName(),
            String.valueOf(level+1)
        };
	
	/* level 0 spawns only one process to mimic the original test */
	if (level == 0) {
	  nspawn = 1;
	}
	
	/* spawn, with a message sent to and received from each child */
	for (i = 0; i < nspawn; i++) {
	  comm[i] = MPI.COMM_SELF.spawn(
                  "java", myArgs, 1, MPI.INFO_NULL, 0, errcodes);
	  //	  MPI_Send(&bufs, sizeof(char)*NCHARS, MPI_CHAR, 0, 100,
	  //		   comm[i]);
	  comm[i].send(bufs, NCHARS, MPI.CHAR, 0, 100);
	  //	  MPI_Irecv(&bufr[i], sizeof(char)*NCHARS, MPI_CHAR,
	  //		    MPI_ANY_SOURCE, MPI_ANY_TAG, comm[i], &req[i]);
	  req[i] = comm[i].iRecv(bufr[i], NCHARS, MPI.CHAR,
				 MPI.ANY_SOURCE, MPI.ANY_TAG);
	}
	
	/* wait for messages from children and print them */
	//	MPI_Waitall(nspawn, req, MPI_STATUSES_IGNORE);
	Request.waitAll(Arrays.copyOf(req, nspawn));
	for (i = 0; i < nspawn; i++)
	  System.out.printf("Child %d sent: %s\n",
			    i, toString(bufr[i]));
      }

      /* send message back to parent */
      if (!parent.isNull()) {
	//	MPI_Send(&bufs, sizeof(char)*NCHARS, MPI_CHAR,
	//		 0, 100, parent);
	parent.send(bufs, NCHARS, MPI.CHAR, 0, 100);
      }
    }
    
    /* non-participating processes wait at this barrier for their peers
     * (This barrier won't cost that many CPU cycles.)
     */
    if (parent.isNull()) {
      NiceMsgs.nice_barrier(MPI.COMM_WORLD);
    }
    
    MPI.Finalize();
  }
  
  private static String toString(CharBuffer buf)
  {
      char[] c = new char[buf.capacity()];
      buf.position(0);
      buf.get(c);
      return new String(c);
  }
}
