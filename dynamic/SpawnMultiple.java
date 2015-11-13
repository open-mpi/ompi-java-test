/* 
 *
 * This file is a port from "spawn_multiple.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: SpawnMultiple.java		Author: S. Gross
 *
 */

/* 
 * README PLEASE!
 * 
 * This test needs the environment variable CLASSPATH
 * in order to find this class and mpi.jar
 */
import java.nio.*;
import mpi.*;

public class SpawnMultiple
{
  private final static String CMD_ARGV1_1 = "this is job 1";
  private final static String CMD_ARGV1_2 = "job1: this is argv 2";
  private final static String CMD_ARGV2_1 = "this is job 2";
  private final static String CMD_ARGV2_2 = "job2: this is argv 2";
  private final static int TAG = 201;

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int rank, size;
    String hostname;
    long t1, t2;
    Intercomm parent;
    
    t1 = System.currentTimeMillis();
    MPI.Init(args);
    t2 = System.currentTimeMillis();
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    hostname = MPI.getProcessorName();

    /* Check to see if we *were* spawned -- because this is a test, we
     * can only assume the existence of this one executable.  Hence, we
     * both mpirun it and spawn it.
     */
    //    parent = MPI_COMM_NULL;
    parent = Intercomm.getParent();
    if (!parent.isNull()) {
      String argv1 = "";
      String argv2 = "";
      if (args.length > 0)
	argv1 = args[0];
      if (args.length > 1)
	argv2 = args[1];
      System.out.printf("Child: %d of %d, %s (%s) (%d ms in init)\n",
	     rank, size, hostname, argv1, (t2 - t1));
      do_target(argv1, argv2, parent);
    } else {
      System.out.printf("Parent: %d of %d, %s (%d ms in init)\n",
	     rank, size, hostname, (t2 - t1));
      //      do_parent(argv[0], size);
      do_parent();
    }
    
    /* All done */
    
    MPI.Finalize();
  }
  
  
  private static void do_parent()
    throws MPIException, InterruptedException
  {
    int i, errcode[], err, rank, size;
    int found[] = new int[1],
	count[] = { MPI.COMM_WORLD.getSize() },
	maxprocs[] = { count[0], count[0] };
    String command[] = { "java", "java" },
           className = SpawnMultiple.class.getName(),
	   argv0[] = { className, CMD_ARGV1_1, CMD_ARGV1_2 },
	   argv1[] = { className, CMD_ARGV2_1, CMD_ARGV2_2 };
    String spawn_argv[][] = {argv0, argv1};
    Intercomm child_inter = null;
    Comm intra;
    Info info[] = { MPI.INFO_NULL, MPI.INFO_NULL };
    //FILE *fp;
    
    /* Ensure we have 3 processes (actually, this check is not needed)
     * We need at least 3 to run
     *   OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
     *				    OmpitestError.getLineNumber(),
     *				    3, true);
     */

    found[0] = 1;
    /* First, see if cmd exists on all ranks */
    /*
    fp = fopen(cmd, "r");
    if (fp == NULL)
      found[0] = 0;
    else {
      fclose(fp);
      found[0] = 1;
    }
    */
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    MPI.COMM_WORLD.allReduce(found, count, 1, MPI.INT, MPI.SUM);
    if (count[0] != size && rank == 0) {
      OmpitestError.ompitestWarning(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                  //"Not all ranks were able to " +
                                  //"find:\n\t\"" + cmd + "\"\n" +
                                    "You probably don't have a " +
                                    "uniform filesystem...?\n" +
                                    "So I'll skip this test, but " +
                                    "not call it a failure.\n");
      return;
    }
    
    /* Now try the spawn if it's found anywhere */
    errcode = new int[2 * count[0]];
    for (i = 0; i < 2 * count[0]; i++) {
      errcode[i] = -1;
    }
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    try {
      child_inter = MPI.COMM_WORLD.spawnMultiple(
              command, spawn_argv, maxprocs, info, 0, errcode);

      for (size = 0, i = 0; i < 2; i++)
	size += maxprocs[i];
      for (i = 0; i < size; i++)
	if (errcode[i] != MPI.SUCCESS)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR: MPI_Comm_spawn_multiple " +
				      "returned errcode[" + i + "] = " +
				      errcode[i] + "\n");
    }
    catch (MPIException ex)
    {
      err = ex.getErrorClass();
      if (err != MPI.SUCCESS)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: MPI_Comm_spawn_multiple " +
				    "returned errcode = " + err + "\n");
    }

    /* Now do a simple ping pong to everyone in the child */
    intra = child_inter.merge(false);
    all_to_all(intra);
    intra.free();
    
    /* Clean up */
    free_inter(child_inter, true);
  }


  private static void do_target(String argv1, String argv2, Intercomm parent)
    throws MPIException, InterruptedException
  {
    int rank, size;
    Comm intra;
    
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    
    /* Check that we got the argv that we expected */
    if (rank < size / 2) {
      if (argv1.compareTo(CMD_ARGV1_1) != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Spawn target rank " + rank +
				    " got argv[1]=\"" + argv1 +
				    "\" when expecing \"" + CMD_ARGV1_1 +
				    "\"\n");
      if (argv2.compareTo(CMD_ARGV1_2) != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Spawn target rank " + rank +
				    " got argv[2]=\"" + argv2 +
				    "\" when expecing \"" + CMD_ARGV1_2 +
				    "\"\n");
    } else {
      if (argv1.compareTo(CMD_ARGV2_1) != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Spawn target rank " + rank +
				    " got argv[1]=\"" + argv1 +
				    "\" when expecing \"" + CMD_ARGV2_1 +
				    "\"\n");
      if (argv2.compareTo(CMD_ARGV2_2) != 0)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: Spawn target rank " + rank +
				    " got argv[2]=\"" + argv2 +
				    "\" when expecing \"" + CMD_ARGV2_2 +
				    "\"\n");
    }
    
    /* Now merge it down to an intra and do a simple all-to-all to
     * everyone in the parent
     */
    intra = parent.merge(false);
    all_to_all(intra);
    intra.free();
    
    free_inter(parent, false);
  }
  
  
  private static void all_to_all(Comm intra)
    throws MPIException, InterruptedException
  {
    int size;
    
    IntBuffer rank    = MPI.newIntBuffer(1),
              message = MPI.newIntBuffer(1);
    
    rank.put(0, intra.getRank());
    size = intra.getSize();
    
    for (int i = 0; i < size; i++) {
      message.put(0, -1);
      if (i == rank.get(0))
	continue;
      else if (i < rank.get(0)) {
	NiceMsgs.nice_send(rank, 1, MPI.INT, i, TAG, intra);
	NiceMsgs.nice_recv(message, 1, MPI.INT, i, TAG, intra);
      } else {
	NiceMsgs.nice_recv(message, 1, MPI.INT, i, TAG, intra);
	NiceMsgs.nice_send(rank, 1, MPI.INT, i, TAG, intra);
      }
      
      if (message.get(0) != i)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: rank " + rank.get(0) +
				    " got message " + message.get(0) +
				    " from comm rank " + i +
				    "; expected " + i + "\n");
    }
    NiceMsgs.nice_barrier(intra);
  }
  
  
  private static void free_inter(Intercomm inter, boolean do_free)
    throws MPIException
  {
    Comm intra;
    int size;
    
    /* I have no idea, why we need the following four lines of code
     * or even the whole method.
     */
    size = inter.getSize();
    intra = inter.merge(false);
    size = inter.getSize();
    intra.free();

    if (do_free) {
      inter.free();
    }
  }  
}
