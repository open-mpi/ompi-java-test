/* 
 *
 * This file is a port from "groupfree.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Groupfree.java			Author: S. Gross
 *
 */

import mpi.*;

public class Groupfree
{
  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    Group group, newgroup;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    group = MPI.COMM_WORLD.getGroup();
    
    for(int i = 0; i < 100; i++)  {
      newgroup = Group.union(group,group);
      newgroup.free();
    }
    
    MPI.COMM_WORLD.barrier();
    group.free();
    MPI.Finalize();
  }
}
