/* 
 *
 * This file is a port from "spawn.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Spawn.java			Author: S. Gross
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

public class Spawn
{
  private final static String CMD_ARGV1 = "this is argv 1";
  private final static String CMD_ARGV2 = "this is argv 2";
  private final static int TAG = 201;

  public static void main (String args[]) throws MPIException,
						 InterruptedException
  {
    int rank, size;
    Intercomm parent;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();

    /* Check to see if we *were* spawned -- because this is a test, we
     * can only assume the existence of this one executable.  Hence, we
     * both mpirun it and spawn it.
     */
    parent = Intercomm.getParent();
    if (!parent.isNull()) {
      int i = 0;
      String argv1 = i < args.length ? args[i++] : "";
      String argv2 = i < args.length ? args[i++] : "";
      do_target(argv1, argv2, parent);
    }
    else {
      do_parent(rank);
    }

    /* All done */
    MPI.Finalize();
  }
  
  
  private static void do_parent(int rank)
    throws MPIException, InterruptedException
  {
    int errcode[], err, size;
    int found[] = new int[1],
	count[] = new int[1];
    
    String spawn_argv[] = {
        Spawn.class.getName(), // Class to execute
        CMD_ARGV1,
        CMD_ARGV2
    };
    
    Intercomm child_inter = null;
    Comm intra;
    
    /* Ensure we have 3 processes (actually, this check is not needed)
     * We need at least 3 to run
     *   OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
     *				    OmpitestError.getLineNumber(),
     *				    3, true);
     */

    /* First, see if cmd exists on all ranks */
    found[0] = 1;
    /*
    // The 'cmd' must be java, so we can't find it.
    fp = fopen(cmd, "r");
    if (fp == NULL)
      found[0] = 0;
    else {
      fclose(fp);
      found[0] = 1;
    }
    */

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
    errcode = new int[count[0]];
    for (int i = 0; i < count[0]; i++) {
      errcode[i] = -1;
    }
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    try {
      child_inter = MPI.COMM_WORLD.spawn("java", spawn_argv, count[0],
                                         MPI.INFO_NULL, 0, errcode); 
      for (int i = 0; i < count[0]; i++) {
	if (errcode[i] != MPI.SUCCESS)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR: MPI_Comm_spawn returned " +
				      "errcode[" + i + "] = " +
				      errcode[i] + "\n");
      }
    }
    catch (MPIException ex)
    {
      err = ex.getErrorClass();
      if (err != MPI.SUCCESS)
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR: MPI_Comm_spawn returned " +
				    "errcode = " + err + "\n");
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
    int rank;
    Comm intra;
    
    rank = MPI.COMM_WORLD.getRank();
    
    /* Check that we got the argv that we expected */
    if (!argv1.equals(CMD_ARGV1))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Spawn target rank " + rank +
				  " got argv[1]=\"" + argv1 +
				  "\" when expecing \"" + CMD_ARGV1 +
				  "\"\n");
    if (!argv2.equals(CMD_ARGV2))
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Spawn target rank " + rank +
				  " got argv[2]=\"" + argv2 +
				  "\" when expecing \"" + CMD_ARGV2 +
				  "\"\n");
    
    /* Now merge it down to an intra and do a simple all-to-all to
     * everyone in the parent
     */
    intra = parent.merge(false);
    all_to_all(intra);
    intra.free();
    
    free_inter(parent, false);
  }
  
  
  static void all_to_all(Comm intra)
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
  
  
  static void free_inter(Intercomm inter, boolean do_free)
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
