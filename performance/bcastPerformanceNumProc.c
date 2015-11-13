/* Measures the performance of MPI_Bcast(). Results are appended
 * to files with the names:
 *   c_outputFilename_msgSize_intValues_cputime.dat,
 *   c_outputFilename_msgSize_intValues_elapsedtime.dat,
 * where "outputFilename" (e.g., SunOS_sparc or Linux_x86_64) and
 * "msgSize" can be provided via the commandline.
 *
 * The file contains two columns. The first column specifies the
 * number of processes and the second one the time to broadcast
 * "NUM_MESSAGES" of the specified size to all processes, so that
 * you can use this file as input to Gnuplot. The x-axis shows the
 * number of processes and the y-axis the time to broadcast these
 * messages. You can use different files to plot curves with
 * different message sizes or different machine architectures into
 * the same graphic.
 *
 *
 * Usage:
 *   mpiexec -np <number of processes> bcastPerformanceNumProc
 *	[message size] [output filename]
 *
 *
 * File: bcastPerformanceNumProc.c	Author: S. Gross
 * Date: 20.05.2013
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#ifdef SunOS
  #include <strings.h>
#else
  #include <string.h>
#endif
#include "mpi.h"

#define DEF_MSG_SIZE	1000		/* default message size		*/
#define NUM_MESSAGES	1000		/* # of messages to broadcast	*/
#define MAX_FILENAME	60		/* maximum filename length	*/
#define DEF_FILENAME	"unknown"	/* unknown architecture		*/

void help (char *msg, char *progName);

int main (int argc, char *argv[])
{
  int mytid,				/* my task id			*/
      ntasks,				/* number of parallel tasks	*/
      msgSize;				/* message size			*/
  int *buffer;				/* message buffer		*/
  double elapsedTime,			/* elapsed time on root node	*/
	 myCPUtime,			/* used CPU-time		*/
	 sumCPUtime;
  char filename_cpu[MAX_FILENAME],
       filename_elapsed[MAX_FILENAME],
       msgSizeAsString[32];		/* message size as string	*/
  FILE *fp_cpu, *fp_elapsed;

  MPI_Init (&argc, &argv);
  MPI_Comm_rank (MPI_COMM_WORLD, &mytid);
  MPI_Comm_size (MPI_COMM_WORLD, &ntasks);

  if (mytid == 0)
  {
    strcpy(filename_cpu, "c_");		/* first part of filename	*/
    /* evaluate command line arguments					*/
    switch (argc)
    {
      case 1:
	/* program was called without commandline arguments		*/
	msgSize = DEF_MSG_SIZE;
	strncpy (filename_cpu + strlen (filename_cpu), DEF_FILENAME,
		 MAX_FILENAME - strlen (filename_cpu));
	break;

      case 2:
	/* program was called with message size				*/
	msgSize = atoi (argv[1]);
	if (msgSize <= 0)
        {
	  help ("Error: Message size must be positive.", argv[0]);
	}
	strncpy (filename_cpu + strlen (filename_cpu), DEF_FILENAME,
		 MAX_FILENAME - strlen (filename_cpu));
	break;

      case 3:
	/* program was called with message size and output filename	*/
	msgSize = atoi (argv[1]);
	if (msgSize <= 0)
        {
	  help ("Error: Message size must be positive.", argv[0]);
	}
	strncpy (filename_cpu + strlen (filename_cpu), argv[2],
		 MAX_FILENAME - strlen (filename_cpu));
	break;

      default:
	/* program was called with too many commandline arguments	*/
	help ("Error: Too many commandline arguments.", argv[0]);
	msgSize = -1;			/* terminate all processes	*/
    }
  }
  /* Each task  m u s t  call MPI_Bcast to learn the value of
   * "msgSize"
   */
  MPI_Bcast (&msgSize, 1, MPI_INT, 0, MPI_COMM_WORLD);
  if (msgSize <= 0)
  {
    /* wrong value							*/
    MPI_Finalize ();
    exit (EXIT_SUCCESS);
  }
  buffer = (int *) malloc (msgSize * sizeof (int));
  if (buffer == NULL)
  {
    fprintf (stderr, "Error: Can't allocate buffer memory.\n");
    MPI_Finalize ();
    exit (EXIT_FAILURE);
  }
  if (mytid == 0)
  {
    /* complete output filenames					*/
    strncpy (filename_cpu + strlen (filename_cpu), "_",
	     MAX_FILENAME - strlen (filename_cpu));
    sprintf (msgSizeAsString, "%d", msgSize);
    strncpy (filename_cpu + strlen (filename_cpu), msgSizeAsString,
	     MAX_FILENAME - strlen (filename_cpu));
    strncpy (filename_cpu + strlen (filename_cpu), "_intValues",
	     MAX_FILENAME - strlen (filename_cpu));
    /* the remaining part is different for both filenames		*/
    strcpy (filename_elapsed, filename_cpu);
    strncpy (filename_cpu + strlen (filename_cpu), "_cputime.dat",
	     MAX_FILENAME - strlen (filename_cpu));
    strncpy (filename_elapsed + strlen (filename_elapsed),
	     "_elapsedtime.dat",
	     MAX_FILENAME - strlen (filename_elapsed));
    /* initialize buffer						*/
    for (int i = 0; i < msgSize; ++i)
    {
      buffer[i] = i;
    }
  }

  /*
   * Now we can start our real work and measure how long it takes
   * to broadcast NUM_MESSAGES of size msgSize
   */
  elapsedTime = MPI_Wtime ();
  myCPUtime   = clock ();
  for (int i = 0; i < NUM_MESSAGES; ++i)
  {
    MPI_Bcast (buffer, msgSize, MPI_INT, 0, MPI_COMM_WORLD);
  }
  myCPUtime   = clock () - myCPUtime;
  elapsedTime = MPI_Wtime () - elapsedTime;
  MPI_Reduce (&myCPUtime, &sumCPUtime, 1, MPI_DOUBLE,
	      MPI_SUM, 0, MPI_COMM_WORLD);
  /* check buffer							*/
  for (int i = 0; i < msgSize; ++i)
  {
    if (buffer[i] != i)
    {
      fprintf (stderr, "Error: Wrong value in 'buffer'.\n");
      MPI_Finalize ();
      exit (EXIT_FAILURE);
    }
  }
  if (mytid == 0)
  {
    fp_cpu = fopen (filename_cpu, "a+");
    if (fp_cpu == NULL)
    {
      fprintf (stderr, "Error opening file %s in append mode.\n"
	       "Can't store my results.\n", filename_cpu);
    }
    else
    {
      fprintf (fp_cpu, "%d  %f\n", ntasks, sumCPUtime / CLOCKS_PER_SEC);
      fclose (fp_cpu);
    }
    fp_elapsed = fopen (filename_elapsed, "a+");
    if (fp_elapsed == NULL)
    {
      fprintf (stderr, "Error opening file %s in append mode.\n"
	       "Can't store my results.\n", filename_elapsed);
    }
    else
    {
      fprintf (fp_elapsed, "%d  %f\n", ntasks, elapsedTime);
      fclose (fp_elapsed);
    }
  }
  free (buffer);
  MPI_Finalize ();
  return EXIT_SUCCESS;
}


/* Print a nice help message						*/
void help (char *msg, char *progName)
{
  fprintf (stderr, "\n%s\n"
	   "Usage:\n"
	   "  mpiexec -np <number of processes> %s "
	   "[message size] [output filename]\n", msg, progName);
}
