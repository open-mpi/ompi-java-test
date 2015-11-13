/* 
 *
 * This file is a port from "group.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: GroupTest.java			Author: S. Gross
 *
 */

import mpi.*;
import java.util.*;

public class GroupTest
{
  public static void main (String args[]) throws MPIException
  {
    int tasks, me, size, rank, result, myclass;
    int cnt = 0;
    int checking_params =
      OmpitestConfig.OMPITEST_CHECKING_MPI_API_PARAMS;
    int ranks1[] = new int[128];
    int ranks2[] = new int[128];
    Group group1, group2, group3, newgroup;
    Group groups[];
    Comm newcomm;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    groups = new Group[20];

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    2, true);

    if (checking_params != 0) {
      String e = System.getenv("OMPI_MCA_mpi_param_check");
      if (null != e && 0 == Integer.parseInt(e)) {
	checking_params = 0;
      }
    }
    
    group1 = MPI.COMM_WORLD.getGroup();
    groups[cnt++] = group1;
    size = group1.getSize();
    if(size != tasks)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_size, size = " +
				  size + ", should be " + tasks + "\n");
    rank = group1.getRank();
    if(rank != me)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_rank, rank = " +
				  rank + ", should be " + me + "\n");
    for(int i = 0; i < tasks/2; i++) {
      ranks1[i] = i;
    }
    newgroup = group1.incl(Arrays.copyOf(ranks1, tasks/2));
    /* newgroup freed below */
    size = newgroup.getSize();
    if(size != tasks/2)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_size, size = " +
				  size + ", should be " + tasks/2 + "\n");
    result = Group.compare(newgroup,newgroup);
    if(result != MPI.IDENT)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (1), " +
				  "result = " + result +
				  ", should be " +
				  MPI.IDENT + "\n");
    result = Group.compare(newgroup,group1);
    if(result != MPI.UNEQUAL)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (2), " +
				  "result = " + result +
				  ", should be " +
				  MPI.UNEQUAL + "\n");
    group2 = Group.union(group1,newgroup);
    groups[cnt++] = group2;
    result = Group.compare(group1,group2);
    if(result != MPI.IDENT)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (3), " +
				  "result = " + result +
				  ", should be " +
				  MPI.IDENT + "\n");
    group2 = Group.intersection(newgroup,group1);
    groups[cnt++] = group2;
    result = Group.compare(group2,newgroup);
    if(result != MPI.IDENT) 
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (4), " +
				  "result = " + result +
				  ", should be " +
				  MPI.IDENT + "\n");
    group2 = Group.difference(group1,newgroup);
    groups[cnt++] = group2;
    size = group2.getSize();
    if(size != tasks - tasks/2)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_size, size = " +
				  size + ", should be " +
				  (tasks - tasks/2) + "\n");
    for(int i = 0; i < size; i++) {
      ranks1[i] = i;
    }
    ranks2 = Group.translateRanks(group2, Arrays.copyOf(ranks1, size), group1);
    for(int i = 0; i < size; i++) {
      if(ranks2[i] != tasks/2 + i)  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Group_translate_ranks\n");
    }
    newcomm = MPI.COMM_WORLD.create(newgroup);
    if(!newcomm.isNull())  {
      group3 = newcomm.getGroup();
      groups[cnt++] = group3;
      result = Group.compare(group3,newgroup);
      if (result != MPI.IDENT) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Comm_group, group " +
				    "is not what it should be\n");
      }
    }
    group3 = group1.excl(Arrays.copyOf(ranks1, tasks/2));
    groups[cnt++] = group3;
    result = Group.compare(group2,group3);
    if(result != MPI.IDENT)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (5), " +
				  "result = " + result +
				  ", should be " +
				  MPI.IDENT + "\n");
    
    for(int i = 0; i < tasks; i++) {
      ranks1[tasks-1-i] = i;
    }
    group3 = group1.incl(Arrays.copyOf(ranks1, tasks));
    groups[cnt++] = group3;
    result = Group.compare(group1,group3);
    if(result != MPI.SIMILAR)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_compare (6), " +
				  "result = " + result +
				  ", should be " +
				  MPI.SIMILAR + "\n");
    
    group3 = newgroup;
    newgroup.free();

    if(!newgroup.isNull())  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Group_free, group " +
				  "is not MPI_GROUP_NULL\n");

    if(!newcomm.isNull()) {
      newcomm.free();
      if(!newcomm.isNull())  
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Comm_free, " +
				    "comm is not MPI_COMM_NULL\n");
    }
    
    /* Only do this test if OMPI is checking MPI params */
    if (checking_params != 0) {
      MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
      try {
	newcomm = MPI.COMM_WORLD.create(newgroup);
      }
      catch (MPIException rc)
      {
	myclass = rc.getErrorClass();
	if(myclass != MPI.ERR_GROUP)
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      "ERROR in MPI_Group_free, " +
				      "group not freed\n");
      }
    }
    MPI.COMM_WORLD.barrier();
    
    /* Note newcomm above is not actually created... */
    for (int i = 0; i < cnt; i++) 
      groups[i].free();

    MPI.Finalize();
  }
}
