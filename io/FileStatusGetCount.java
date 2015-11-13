/* 
 *
 * This file is a port from "file_status_get_count.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 * This program origianlly written by Nina Thiessen.  Modified by Jeff
 * Squyres to become part of the LAM test suite.
 *
 * Test program to ensure that ROMIO is setting the raw number of
 * bytes received properly in an MPI_Status object correctly.  This is
 * tested because ROMIO doesn't do it by default -- we had to make a
 * small hack in ROMIO to make this work properly (since ROMIO doesn't
 * natively understand the hidden members of LAM's MPI_Status object).
 * See romio/README_LAM.
 *
 *
 * File:  FileStatusGetCount.java	Author: S. Gross
 *
 */

import mpi.*;

public class  FileStatusGetCount
{
  private static final int SIZEOF_INT = 4;

  public static void main (String args[]) throws MPIException
  {
    int myrank, numprocs, bufsize, count;
    int buf[];
    Status status;
    File thefile;
    long filesize;
    
    MPI.Init(args);
    myrank = MPI.COMM_WORLD.getRank();
    numprocs = MPI.COMM_WORLD.getSize();
    
    /* Do this on every node, because in a testing environment, we can't
     * assume a common filesystem
     */
    String filename = "ompi_testfile." + myrank;
    
    /* Writes */
    thefile = new File(MPI.COMM_SELF, filename,
                       MPI.MODE_WRONLY | MPI.MODE_CREATE);
    buf = new int[1];

    for (int i = 0; i < numprocs * 50; ++i) {
      buf[0] = i;
      thefile.write(buf, 1, MPI.INT);
    }

    thefile.close();
    
    /* Reads */
    thefile  = new File(MPI.COMM_SELF, filename, MPI.MODE_RDONLY);
    filesize = thefile.getSize();     /* in bytes */
    filesize = filesize / SIZEOF_INT; /* in # of ints */
    bufsize  = (int)(filesize / numprocs); /* local num of ints to read */
    buf = new int[bufsize];
    
    thefile.setView(myrank * buf.length, MPI.INT, MPI.INT, "native");
    status = thefile.read(buf, bufsize, MPI.INT);
    
    count = status.getCount(MPI.INT);
    if (bufsize != count)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: MPI_Get_count returned the " +
				  "incorrect value.\n" +
				  "Was expecing: " + bufsize +
				  ", MPI_Get_count returned " + count +
				  "\n");

    thefile.close();
    
    /* Delete the testfile.
     * original file: unlink(filename);
     * The second parameter is an info object.
     */
    MPI.COMM_WORLD.barrier();
    File.delete(filename);

    MPI.Finalize();
  }
}
