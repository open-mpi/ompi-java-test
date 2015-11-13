/* 
 *
 * This file is a port from "loop_spawn.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: LoopSpawn.java			Author: S. Gross
 *
 */

/*
 * README PLEASE!
 * 
 * This test needs the environment variable CLASSPATH
 * in order to find this class and mpi.jar
 * 
 * NUM_TESTS was 2000
 */
import mpi.*;

public class LoopSpawn
{
  private final static int NUM_TESTS = 200;
  private final static String EXE_TEST = LoopChild.class.getName();

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int cwrank, pattern, nseconds = 600;
    //char test[] = new char[512];
    
    /* construct the test name */
    /* extract the path to this executable - do a little dance
     * since some implementations of dirname can modify their input
     */
    {
      /* Perhaps we can use something like
       * String jvmName = ManagementFactory.getRuntimeMXBean().getName();
       * or like
       * Thread.currentThread().getStackTrace().getClassName()
       * (see getClassName() in OmpitestError.java) or like
       * System.getProperties()
       * to find something similar to argv[0]
       */
      /*
      char *prefix, *safe;
      safe = strdup(argv[0]);
      prefix = dirname(safe);
      sprintf(test, "%s/%s", prefix, EXE_TEST);
      free(safe);
      */
    }

    /* length of time of test */
    if (args.length > 0)
    {
      try {
	nseconds = Integer.parseInt(args[0]);
      }
      catch (NumberFormatException e)
      {
	System.err.printf ("\"%d\" isn't an integer.\n" +
			   "I use my default value.\n", args[0]);
      }
    }

    MPI.Init(args);
    cwrank = MPI.COMM_WORLD.getRank();
    
    /* loop over patterns, allotting equal time for each pattern */
    nseconds = nseconds / 2;
    for (pattern = 0; pattern < 2; pattern++) {
      int color = MPI.UNDEFINED;
      Intracomm comm_parent;
      
      /* form the parent communicator for this pattern
       * pattern 0 is only rank 0
       * pattern 1 has all even ranks
       */
      if ((0 == pattern) && (0 ==  cwrank   )) color = 1;
      if ((1 == pattern) && (0 == (cwrank&1))) color = 1;
      comm_parent = MPI.COMM_WORLD.split(color, cwrank);
      
      /* if you're a member of the parent communicator, participate
       * (non-participants will race ahead to wait at the barrier
       */
      if (!comm_parent.isNull()) {
	int iter[] = new int[1];
	double t_stop = MPI.wtime() + nseconds;
        String arguments[] = { EXE_TEST };
	
	for (iter[0] = 0; iter[0] < NUM_TESTS; ++iter[0]) {
	  int err[] = new int[1],
              rank, size;
	  Comm comm_spawned, comm_merged;
	  comm_spawned = comm_parent.spawn(
                  "java", arguments, 1, MPI.INFO_NULL, 0, err);

          if (0 == cwrank && 0 == iter[0] % 20) {
	    System.out.printf("parent: MPI_Comm_spawn #%d return : %d\n",
			      iter[0], err[0]);
	  }

	  comm_merged = ((Intercomm) comm_spawned).merge(false);
	  rank = comm_merged.getRank();
	  size = comm_merged.getSize();
	  if (0 == cwrank && args.length > 0) {
	    System.out.printf("parent: MPI_Comm_spawn #%d rank %d, " +
			      "size %d\n", iter[0], rank, size);
	  }
	  comm_merged.free();
	  comm_spawned.disconnect();
	  
	  /* if time exceeded, then bump iteration count to the end
	   * this decision is made on rank 0
	   */
	  if (MPI.wtime() > t_stop) {
	    iter[0] = NUM_TESTS;
	  }
	  comm_parent.bcast(iter, 1, MPI.INT, 0);
	}
	comm_parent.free();
      }
      NiceMsgs.nice_barrier(MPI.COMM_WORLD);
    }
    
    MPI.Finalize();
    if (0 == cwrank && args.length > 0) {
      System.out.printf("parent: End .\n");
    }
  }
}
