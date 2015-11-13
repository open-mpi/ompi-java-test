/* 
 *
 * This file is a port from "commdup.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Commdup.java			Author: S. Gross
 *
 */

import mpi.*;

public class Commdup
{
  private final static int ITER = 10;

  public static void main (String args[]) throws MPIException
  {
    int myself;
    Comm comm, newcomm;
    Comm savecomm[];
    int  cnt = 0;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    savecomm = new Comm[2*ITER];

    for(int i= 0 ; i < ITER; i++) {
      comm = (Comm) (MPI.COMM_WORLD.clone());
      savecomm[cnt++] = comm;
    }
    
    comm = MPI.COMM_WORLD;
    for(int i = 0; i < ITER; i++) { 
      newcomm = comm.clone();
      comm = newcomm;
      savecomm[cnt++] = newcomm;
    }

    MPI.COMM_WORLD.barrier ();
    for (int i = 0; i < cnt; i++) 
      savecomm[i].free();
    
    MPI.Finalize();
  }
}
