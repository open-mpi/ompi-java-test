/* Simple routines to print and store errors for the OMPI test suite.
 *
 * This file is a port from "ompitest_error.c" from the
 * "ompi-ibm-10.0" regression test package with some add-ons for
 * "__FILE__", "__LINE__", etc.  The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * Available methods:
 *
 *   public static String getFileName()
 *   public static String getClassName()
 *   public static String getMethodName()
 *   public static String getLineNumber()
 *   public static String getLocation()
 *   public static void ompitestWarning(String file, int line,
 *				        String message) throws MPIException
 *   public static void ompitestWarning(Comm comm, String commStr,
 *				        String file, int line,
 *				        String message) throws MPIException
 *   public static void ompitestError(String file, int line,
 *				      String message) throws MPIException
 *   public static void ompitestError(Comm comm, String commStr,
 *				      String file, int line,
 *				      String message) throws MPIException
 *   public static void ompitestCheckSize(String file, int line,
 *				          int min, boolean wantAbort)
 *     throws MPIException
 *   public static void ompitestNeedEven(String file, int line)
 *     throws MPIException
 *
 *
 * File: OmpitestError.java		Author: S. Gross
 *
 */

import mpi.*;

public class OmpitestError
{
  /* Methods to get file name class name, method name, and
   * line number of a problematic location
   */

  public static String getFileName()
  {
    StackTraceElement steArray[], steElem;
    int lastIndex;

    steArray  = Thread.currentThread().getStackTrace();
    lastIndex = steArray.length - 1;
    steElem   = steArray[lastIndex];
    return steElem.getFileName();
  }


  public static String getClassName()
  {
    StackTraceElement steArray[], steElem;
    int lastIndex;

    steArray  = Thread.currentThread().getStackTrace();
    lastIndex = steArray.length - 1;
    steElem   = steArray[lastIndex];
    return steElem.getClassName();
  }


  public static String getMethodName()
  {
    StackTraceElement steArray[], steElem;
    int lastIndex;

    steArray  = Thread.currentThread().getStackTrace();
    lastIndex = steArray.length - 1;
    steElem   = steArray[lastIndex];
    return steElem.getMethodName();
  }


  public static int getLineNumber()
  {
    StackTraceElement steArray[], steElem;
    int lastIndex;

    steArray  = Thread.currentThread().getStackTrace();
    lastIndex = steArray.length - 1;
    steElem   = steArray[lastIndex];
    return steElem.getLineNumber();
  }


  public static String getLocation()
  {
    StackTraceElement steArray[], steElem;
    String str;
    int lastIndex;

    steArray  = Thread.currentThread().getStackTrace();
    lastIndex = steArray.length - 1;
    steElem   = steArray[lastIndex];
    str = "  File:   " + steElem.getFileName() + "\n" +
      "  Class:  " + steElem.getClassName() + "\n" +
      "  Method: " + steElem.getMethodName() + "\n" +
      "  Line:   " + steElem.getLineNumber();
    return str;
  }


  /* Methods to print a warning or an error.				*/

  public static void ompitestWarning(String file, int line,
				     String message) throws MPIException
  {
    String rank;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      rank = (new Integer(MPI.COMM_WORLD.getRank())).toString();
    } else {
      rank = "(unknown)";
    }

    /* Print the warning to stderr					*/
    System.err.printf("[**WARNING**]: MPI.COMM_WORLD rank %s, " +
		      "file %s:%d:\n%s\n",
		      rank, file, line, message);
  }


  public static void ompitestWarning(Comm comm, String commStr,
				     String file, int line,
				     String message) throws MPIException
  {
    String rank;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      rank = (new Integer(comm.getRank())).toString();
    } else {
      rank = "(unknown)";
    }

    /* Print the warning to stderr					*/
    System.err.printf("[**WARNING**]: %s rank %s, file %s:%d:\n%s\n",
		      commStr, rank, file, line, message);
  }


  public static void ompitestError(String file, int line,
				   String message) throws MPIException
  {
    String rank;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      rank = (new Integer(MPI.COMM_WORLD.getRank())).toString();
    } else {
      rank = "(unknown)";
    }

    /* Print the error to stderr					*/
    System.err.printf("[**ERROR**]: MPI.COMM_WORLD rank %s, " +
		      "file %s:%d:\n%s\n",
		      rank, file, line, message);

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      /* Now MPI abort							*/
      MPI.COMM_WORLD.abort(1);
    }
    System.exit(1);
  }


  public static void ompitestError(Comm comm, String commStr,
				   String file, int line,
				   String message) throws MPIException
  {
    String rank;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      rank = (new Integer(comm.getRank())).toString();
    } else {
      rank = "(unknown)";
    }

    /* Print the error to stderr					*/
    System.err.printf("[**ERROR**]: %s rank %s, file %s:%d:\n%s\n",
		      commStr, rank, file, line, message);

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      /* Now MPI abort							*/
      MPI.COMM_WORLD.abort(1);
    }
    System.exit(1);
  }


  public static void ompitestCheckSize(String file, int line,
				       int min, boolean wantAbort)
    throws MPIException
  {
    int size;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      size = MPI.COMM_WORLD.getSize();
      if (size < min) {
	if (wantAbort) {
	  ompitestWarning(file, line,
			  "This test requires at least " + min +
			  " processes to run.  Aborting.\n");
	  MPI.Finalize();
	  System.exit(77);
	} else {
	  ompitestWarning(file, line,
			  "This test performs best with at least " +
			  min + " processes. It will still run, but\n" +
			  "some tests may be skipped and/or reduced.\n");
	}
      }
    } else {
      ompitestError(file, line,
		    "[**ERROR**]: ompitestCheckSize was called " +
		    "before MPI.Init or\n" +
		    "after MPI.Finalize.  Aborting.\n");
    }
  }


  public static void ompitestNeedEven(String file, int line)
    throws MPIException
  {
    int size;

    if (MPI.isInitialized() && !MPI.isFinalized()) {
      size = MPI.COMM_WORLD.getSize();
      if ((size % 2) != 0) {
	ompitestWarning(file, line,
			"This test requires an even number of " +
			"processes to run.  Aborting.\n");
	MPI.Finalize();
	System.exit(77);
      }
    } else {
      ompitestError(file, line,
		    "[**ERROR**]: ompitestNeedEven was called " +
		    "before MPI.Init or\n" +
		    "after MPI.Finalize.  Aborting.\n");
    }
  }
}
