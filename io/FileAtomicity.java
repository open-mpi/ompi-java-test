/*
 *
 * File: FileAll.java			Author: Fujitsu
 *
 */

import mpi.File;
import mpi.MPI;
import mpi.MPIException;

class  FileAtomicity{
  private final static boolean DEBUG = false;

  public static void main (String args[]) throws MPIException{

    boolean atomicity;

    MPI.Init(args);
    int myrank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();

    /* Do this on every node, because in a testing environment, we can't
     * assume a common filesystem
     */
    String filename = "ompi_testfile." + myrank;

    File file = new File(MPI.COMM_SELF, filename,
                         MPI.MODE_WRONLY | MPI.MODE_CREATE);

    /* setAtomicity (true) */
    file.setAtomicity(true);
    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.setAtomicity()  end");
    }
    /* getAtomicity (true) */
    atomicity = file.getAtomicity();
    if (atomicity != true) {
        if(DEBUG) {
          System.out.println("myrank " + myrank + " of " + size +
                             " file.getAtomicity()  error" +
                             " setvalue= " + true  + " atomicity=" + atomicity);
        }
      MPI.COMM_WORLD.abort(100);
    }else{
      if (myrank == 0) {
         if(DEBUG) {
            System.out.println("myrank " + myrank + " of " + size +
                               " file.getAtomicity()  end(OK)" +
                               " atomicity=" + atomicity);
        }
      }
    }

    /* setAtomicity (false) */
    file.setAtomicity(false);
    /* getAtomicity (false) */
    atomicity = file.getAtomicity();
    if (atomicity != false) {
    	OmpitestError.ompitestError(OmpitestError.getFileName(),
    			OmpitestError.getLineNumber(),
    			"myrank " + myrank + " of " + size +
    			 " file.getAtomicity()  error" +
    			 " setvalue= " + false  + " atomicity=" + atomicity);

/*
      System.out.println("myrank " + myrank + " of " + size +
                         " file.getAtomicity()  error" +
                         " setvalue= " + false  + " atomicity=" + atomicity + "\n");
*/
      MPI.COMM_WORLD.abort(100);
    }else{
      if ((myrank == 0) && DEBUG) {
        System.out.println("myrank " + myrank + " of " + size +
                           " file.getAtomicity()  end(OK)" +
                           " atomicity=" + atomicity);
      }
    }

    file.close();

    /* Delete the testfile.
     * original file: unlink (filename);
     * The second parameter is an info object.
     */
    MPI.COMM_WORLD.barrier();
    File.delete(filename);

    MPI.Finalize();

    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " MPI.Finalize() end");
    }

  }
}
