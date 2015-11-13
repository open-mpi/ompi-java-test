/* Measures the performance of Comm.bcast() (MPI_Bcast()). Results
 * are appended to files with the names:
 *   java_outputFilename_numProc_tasks_cputime.dat,
 *   java_outputFilename_numProc_tasks_elapsedtime.dat,
 * where "outputFilename" can be provided via the commandline
 * (e.g., SunOS_sparc or Linux_x86_64) and "numProc" is the
 * number of processes, which "mpiexec" starts on all machines.
 *
 * The file contains two columns. The first column specifies the
 * message length and the second one the time to broadcast
 * "NUM_MESSAGES" of the specified size to all processes, so that
 * you can use this file as input to Gnuplot. The x-axis shows the
 * message size and the y-axis the time to broadcast these messages.
 * You can use different files to plot curves with different
 * numbers of processes or different machine architectures into
 * the same graphic.
 *
 *
 * Usage:
 *   mpiexec -np <number of processes> java BcastPerformanceMsgSize
 *	[message size] [output filename]
 *
 *
 * File: BcastPerformanceMsgSize.java	Author: S. Gross
 * Date: 20.05.2013
 *
 */

import java.lang.management.*;
import java.io.*;
import mpi.*;

public class BcastPerformanceMsgSize
{
  private final static int DEF_MSG_SIZE	= 1000;	/* default message size	*/
  private final static int NUM_MESSAGES = 1000;	/* # of msgs to bcast	*/
  private final static String DEF_FILENAME = "unknown";	/* unknown arch	*/
  private final static int NANOSEC_PER_SEC = 1000000000;

  public static void main (String args[]) throws MPIException
  {
    int	mytid,				/* my task id			*/
	ntasks,				/* number of parallel tasks	*/
	msgSize[] = new int[1];		/* message size			*/
    int	buffer[];
    double elapsedTime,			/* elapsed time on root node	*/
	   myCPUtime[] = new double[1],	/* used CPU-time		*/
	   sumCPUtime[] = new double[1];
    String filename_cpu = null,		/* make the compiler happy	*/
	   filename_elapsed = null;
    boolean append = true;		/* append at end of file	*/
    FileWriter fp_cpu, fp_elapsed;

    MPI.Init (args);
    mytid  = MPI.COMM_WORLD.getRank ();
    ntasks = MPI.COMM_WORLD.getSize ();
    myCPUtime[0]  = 0.0;
    sumCPUtime[0] = 0.0;

    if (mytid == 0)
    {
      filename_cpu = new String ("java_");  /* first part of filename	*/
      /* evaluate command line arguments				*/
      switch (args.length)
      {
	case 0:
	  /* program was called without commandline arguments		*/
	  msgSize[0] = DEF_MSG_SIZE;
	  filename_cpu = filename_cpu + DEF_FILENAME;
	  break;

	case 1:
	  /* program was called with message size			*/
	  msgSize[0] = Integer.parseInt(args[0]);
	  if (msgSize[0] <= 0)
          {
	    help ("Error: Message size must be positive.");
	  }
	  filename_cpu = filename_cpu + DEF_FILENAME;
	  break;

	case 2:
	  /* program was called with message size and output filename	*/
	  msgSize[0] = Integer.parseInt(args[0]);
	  if (msgSize[0] <= 0)
	  {
	    help ("Error: Message size must be positive.");
	  }
	  filename_cpu = filename_cpu + args[1];
	  break;

	default:
	  /* program was called with too many commandline arguments	*/
	  help ("Error: Too many commandline arguments.");
	  msgSize[0] = -1;		/* terminate all processes	*/
      }
    }
    /* Each task  m u s t  call MPI_Bcast to learn the value of
     * "msgSize"
     */
    MPI.COMM_WORLD.bcast (msgSize, 1, MPI.INT, 0);
    if (msgSize[0] <= 0)
    {
      /* wrong value							*/
      MPI.Finalize ();
      System.exit (0);
    }
    buffer = new int[msgSize[0]];
    if (mytid == 0)
    {
      /* complete output filenames					*/
      filename_cpu = filename_cpu + "_" + String.valueOf(ntasks) +
	"_tasks";
      /* the remaining part is different for both filenames		*/
      filename_elapsed = new String (filename_cpu);
      filename_cpu = filename_cpu + "_cputime.dat";
      filename_elapsed = filename_elapsed + "_elapsedtime.dat";
      /* initialize buffer						*/
      for (int i = 0; i < msgSize[0]; ++i)
      {
      buffer[i] = i;
      }
    }

    /*
     * Now we can start our real work and measure how long it takes
     * to broadcast NUM_MESSAGES of size msgSize
     */
    elapsedTime = MPI.wtime ();
    myCPUtime[0]   = getThreadCpuTime ();
    for (int i = 0; i < NUM_MESSAGES; ++i)
    {
      /* broadcast value to all processes				*/
      MPI.COMM_WORLD.bcast (buffer, msgSize[0], MPI.INT, 0);
    }
    myCPUtime[0]   = getThreadCpuTime () - myCPUtime[0];
    elapsedTime = MPI.wtime () - elapsedTime;
    MPI.COMM_WORLD.reduce (myCPUtime, sumCPUtime, 1, MPI.DOUBLE,
			   MPI.SUM, 0);
    /* check buffer							*/
    for (int i = 0; i < msgSize[0]; ++i)
    {
      if (buffer[i] != i)
      {
	System.err.printf ("Error: Wrong value in 'buffer'.\n");
	MPI.Finalize ();
	System.exit (77);
      }
    }
    if (mytid == 0)
    {
      try
      {
	fp_cpu = new FileWriter (filename_cpu, append);
	fp_cpu.write (msgSize[0] + "  " +
		      sumCPUtime[0] / NANOSEC_PER_SEC + "\n");
        fp_cpu.close ();
      }
      catch (IOException ex)
      {
	System.err.printf ("Error opening file %s in append mode.\n" +
			   "Can't store my results.\n", filename_cpu);
      }

      try
      {
	fp_elapsed = new FileWriter (filename_elapsed, append);
	fp_elapsed.write (msgSize[0] + "  " + elapsedTime + "\n");
        fp_elapsed.close ();
      }
      catch (IOException ex)
      {
	System.err.printf ("Error opening file %s in append mode.\n" +
			   "Can't store my results.\n", filename_elapsed);
      }
    }
    MPI.Finalize ();
  }


  /* Get CPU time in nanoseconds.					*/
  private static long getThreadCpuTime()
  {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    return bean.isCurrentThreadCpuTimeSupported() ?
      bean.getCurrentThreadCpuTime() : 0L;
  }


  /* Print a nice help message						*/
  private static void help (String msg)
  {
    String className = new BcastPerformanceMsgSize().getClass().getName();

    System.err.printf ("\n%s\n" +
		       "Usage:\n" +
		       "  mpiexec -np <number of processes> " +
		       "java %s [<message size>] [output filename]\n",
		       msg, className);
  }
}
