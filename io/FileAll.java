/*
 *
 * File: FileAll.java			Author: Fujitsu
 *
 */

import java.nio.IntBuffer;

import mpi.File;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

public class  FileAll
{
  private static final int ITEMS = 50;
  private static final int SIZEOF_INT = 4;
  private final static boolean DEBUG = false;

  public static void main (String args[]) throws MPIException
  {
    int myrank, size;
    Request req1, req2;
    File file;

    MPI.Init(args);
    myrank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();

    /* Do this on every node, because in a testing environment, we can't
     * assume a common filesystem
     */
    String filename = "ompi_testfile_all";

    /* iWriteAtAll */
    file = new File(MPI.COMM_WORLD, filename,
                       MPI.MODE_WRONLY | MPI.MODE_CREATE);

    IntBuffer buffer = MPI.newIntBuffer(ITEMS);
    for (int i = 0; i < ITEMS; ++i) {
      buffer.put(i, i*100*(myrank+1));   // myrank=0: 0,100,200,300..., myrank=1: 0,200,400,600..., myrank=2: 0,300,600,900...
    }
    file.setView(myrank * ITEMS * SIZEOF_INT, MPI.INT, MPI.INT, "native");
    req1 = file.iWriteAll(buffer, ITEMS, MPI.INT);
    req1.waitFor();

    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.iWriteAll() end");
    }

    file.close();

    /* iReadAtAll */
    file = new File(MPI.COMM_WORLD, filename, MPI.MODE_RDONLY);
    file.setView(myrank * ITEMS * SIZEOF_INT, MPI.INT, MPI.INT, "native");
    req2 = file.iReadAll(buffer, ITEMS, MPI.INT);
    req2.waitFor();

    /* printing read data */
    for (int i = 0; i < ITEMS; ++i) {
      if (buffer.get(i) != i*100*(myrank+1)){
    	  OmpitestError.ompitestError(OmpitestError.getFileName(),
    			  OmpitestError.getLineNumber(),
    			  "myrank " + myrank + " of " + size +
    			  " data incorrect, read data=" + buffer.get(i) +
    			  ", expected data=" + i*100*(myrank+1));
      }
    }

    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " file.iReadAll() end");
    }

    file.close();

    /* Delete the testfile.
     * original file: unlink(filename);
     * The second parameter is an info object.
     */
    MPI.COMM_WORLD.barrier();
    if(myrank == 0) {
      File.delete(filename);
    }
    MPI.Finalize();

    if ((myrank == 0) && DEBUG) {
      System.out.println("myrank " + myrank + " of " + size +
                         " MPI.Finalize() end");
    }

  }
}
