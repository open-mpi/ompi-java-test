/*
 *
 * File: FileAll.java			Author: Fujitsu
 *
 */

import mpi.Errhandler;
import mpi.File;
import mpi.MPI;
import mpi.MPIException;

class FileErrhandler{
  private final static boolean DEBUG = false;

  public static void main (String[] args) throws MPIException {
    MPI.Init(args);
    int myrank = MPI.COMM_WORLD.getRank();
    int size   = MPI.COMM_WORLD.getSize();

    
    String filename = "ompi_testfile." + myrank;
    File file = new File(MPI.COMM_SELF, filename,
                         MPI.MODE_WRONLY | MPI.MODE_CREATE);

    if(myrank == 0) {
        System.out.println("This program tests call callErrhandler() and generates error messages.\n" +
                           "ERRORS ARE EXPECTED AND NORMAL IN THIS PROGRAM!!");
    }

    /* set MPI.ERRORS_ARE_FATAL */
    file.setErrhandler(MPI.ERRORS_ARE_FATAL);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.setErrhandler(MPI.ERRORS_ARE_FATAL) end");
    }

    /* save error handler (MPI.ERRORS_ARE_FATAL) */
    Errhandler errhandler = file.getErrhandler();
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.getErrhandler_fatal(MPI.ERRORS_ARE_FATAL) end");
    }
    /* set MPI.ERRORS_RETURN */
    file.setErrhandler(MPI.ERRORS_RETURN);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.setErrhandler(MPI.ERRORS_RETURN) end");
    }
    /* not abend (MPI.ERRORS_RETURN) */
    file.callErrhandler(MPI.ERR_FILE);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.callErrhandler_1() end");
    }

    MPI.COMM_WORLD.barrier();

    /* restore error handler (MPI.ERRORS_ARE_FATAL) */
    file.setErrhandler(errhandler);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.setErrhandler(restore) end");
    }

    /* abend (MPI.ERRORS_ARE_FATAL) */
    file.callErrhandler(MPI.ERR_FILE);
    if (myrank == 0) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
                                  OmpitestError.getLineNumber(),
                                  "Error: The behavior of callErrhandler() is abnormal.");
    }

    MPI.COMM_WORLD.barrier();
    File.delete(filename);
    MPI.Finalize();

    if ((myrank == 0)  && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " MPI.Finalize() end");
    }
  }
}

