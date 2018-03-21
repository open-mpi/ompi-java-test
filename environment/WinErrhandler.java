/*
 *
 * File: FileAll.java			Author: Fujitsu
 *
 */

import java.nio.IntBuffer;

import mpi.Errhandler;
import mpi.MPI;
import mpi.MPIException;
import mpi.Win;

class WinErrhandler{
  private final static boolean DEBUG = false;

  public static void main (String[] args) throws MPIException {

    MPI.Init(args);
    int myrank = MPI.COMM_WORLD.getRank();
    int size   = MPI.COMM_WORLD.getSize();

    if (myrank == 0) {
        System.out.println("This program tests call callErrhandler() and generates error messages.\n" +
                           "ERRORS ARE EXPECTED AND NORMAL IN THIS PROGRAM!!");
    }

    IntBuffer buffer = MPI.newIntBuffer(1);
    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);

    /* save default error handler (MPI.ERRORS_ARE_FATAL) */
    Errhandler errhandler = win.getErrhandler();
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " win.getErrhandler_default() end");
    }
    /* set MPI.ERRORS_RETURN */
    win.setErrhandler(MPI.ERRORS_RETURN);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " win.setErrhandler(MPI.ERRORS_RETURN) end");
    }
    /* not abend (MPI.ERRORS_RETURN) */
    win.callErrhandler(MPI.ERR_WIN);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " win.callErrhandler_1() end");
    }

    MPI.COMM_WORLD.barrier();

    /* restore default error handler (MPI.ERRORS_ARE_FATAL) */
    win.setErrhandler(errhandler);
    if ((myrank == 0)  && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " win.setErrhandler(restore) end");
    }

    /* abend (MPI.ERRORS_ARE_FATAL) */
    win.callErrhandler(MPI.ERR_WIN);
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
