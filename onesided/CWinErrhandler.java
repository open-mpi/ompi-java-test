/* 
 *
 * This file is a port from "c_win_errhandler.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CWinErrhandler.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CWinErrhandler
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    IntBuffer buffer = MPI.newIntBuffer(1);
    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    win.setErrhandler(MPI.ERRORS_RETURN);   
    win.callErrhandler(MPI.ERR_OTHER);
    /* success is not aborting ;) */
    win.free();
    
    MPI.Finalize();
  }
}
