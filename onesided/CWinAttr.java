/* 
 *
 * This file is a port from "c_win_attr.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 * I adapted the error messages from "Get_attr (1,1)" to
 * "Get_attr (25,1)" and "Get_attr (25,sizeof(int))", so that they
 * match errors in different blocks.
 *
 *
 * File: CWinAttr.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class CWinAttr
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int sizeOfInt = 4;
    IntBuffer buffer = MPI.newIntBuffer(25);

    /* one integer, displacement of 1 integer */
    Win win = new Win(buffer, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    
    Object value = win.getAttr(MPI.WIN_BASE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): MPI_WIN_BASE not " +
				  "found\n");
    }
    /*
    // We can't get the buffer address with a standard method.
    if (base != buffer) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): base != buffer\n");
    }
    */
    value = win.getAttr(MPI.WIN_SIZE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): MPI_WIN_SIZE not" +
				  "found\n");
    }
    if (sizeOfInt != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): sizeof(int) != " +
				  "size (" + value + ")\n");
    }
    value = win.getAttr(MPI.WIN_DISP_UNIT);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): MPI_WIN_DISP_UNIT " +
				  "not found\n");
    }
    if (sizeOfInt != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (1,1): displacement != 4\n");
    }
    win.free();
    
    /* 25 integers, displacement of 1 */
    win = new Win(buffer, 25, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    value = win.getAttr(MPI.WIN_BASE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): MPI_WIN_BASE not " +
				  "found\n");
    }
    /*
    // We can't get the buffer address with a standard method.
    if (base != buffer) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): base != buffer\n");
    }
    */
    value = win.getAttr(MPI.WIN_SIZE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): MPI_WIN_SIZE not" +
				  "found\n");
    }
    if (sizeOfInt * 25 != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): sizeof(int) * 25 " +
				  "!= size (" + value + ")\n");
    }
    value = win.getAttr(MPI.WIN_DISP_UNIT);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): MPI_WIN_DISP_UNIT " +
				  "not found\n");
    }
    if (sizeOfInt != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,1): displacement != 4\n");
    }
    win.free();

    /* 25 integers, displacement of sizeof(int) */
    win = new Win(buffer, 25, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    value = win.getAttr(MPI.WIN_BASE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "MPI_WIN_BASE not found\n");
    }
    /*
    // We can't get the buffer address with a standard method.
    if (value != buffer) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "base != buffer\n");
    }
    */
    value = win.getAttr(MPI.WIN_SIZE);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "MPI_WIN_SIZE not found\n");
    }
    if (sizeOfInt * 25 != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "sizeof(int) * 25 != size (" +
				  value + ")\n");
    }
    value = win.getAttr(MPI.WIN_DISP_UNIT);
    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "MPI_WIN_DISP_UNIT not found\n");
    }
    if (sizeOfInt != ((Integer)value).intValue()) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Get_attr (25,sizeof(int)): " +
				  "displacement != sizeof(int)\n");
    }
    win.free();

    MPI.Finalize();
  }
}
