/*
 *
 * File: FileAll.java			Author: Fujitsu
 *
 */

import mpi.Errhandler;
import mpi.MPI;
import mpi.MPIException;

class CommErrhandler{
  private final static boolean DEBUG = false;

  public static void main (String[] args) throws MPIException {

    MPI.Init(args);
    int myrank = MPI.COMM_WORLD.getRank();
    int size   = MPI.COMM_WORLD.getSize();

    if (myrank == 0) {
        System.out.println("This program tests call callErrhandler() and generates error messages.\n" +
                           "ERRORS ARE EXPECTED AND NORMAL IN THIS PROGRAM!!");
    }

    /* save default error handler (MPI.ERRORS_ARE_FATAL) */
    Errhandler errhandler = MPI.COMM_WORLD.getErrhandler();
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " comm.getErrhandler_default() end");
    }
    /* set MPI.ERRORS_RETURN */
    MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " comm.setErrhandler(MPI.ERRORS_RETURN) end");
    }
    /* not abend (MPI.ERRORS_RETURN) */
    MPI.COMM_WORLD.callErrhandler(MPI.ERR_COMM);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " comm.callErrhandler_1() end");
    }

    MPI.COMM_WORLD.barrier();

    /* restore default error handler (MPI.ERRORS_ARE_FATAL) */
    MPI.COMM_WORLD.setErrhandler(errhandler);
    if ((myrank == 0)  && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " comm.setErrhandler(restore) end");
    }

    /* abend (MPI.ERRORS_ARE_FATAL) */
    MPI.COMM_WORLD.callErrhandler(MPI.ERR_COMM);
    if (myrank == 0) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
                                  OmpitestError.getLineNumber(),
                                  "Error: The behavior of callErrhandler() is abnormal.");
    }

    MPI.COMM_WORLD.barrier();

    MPI.Finalize();

    if (myrank == 0) {
      System.out.println("myrank " + myrank + " of " + size +
                         " MPI.Finalize() end");
    }
  }
}
