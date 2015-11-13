/* 
 *
 * This file is a port from "range.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Range.java			Author: S. Gross
 *
 */

import mpi.*;
import java.util.*;

public class Range
{
  public static void main (String args[]) throws MPIException
  {
    int size, myself, color;
    int cnt = 0;
    int ranks1[] = new int[16];
    int ranks2[];
    int ranges[][] = new int[10][3];
    Group group, newgroup;
    Group groups[];
    Comm subset;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    groups = new Group[20];

    /* We need at least 8 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
                                    OmpitestError.getLineNumber(),
                                    8, true);
    
    /* We only need 8 ranks for this test */
    if (myself < 8) {
      color = 0;
    } else {
      color = 1;
    }
    subset = MPI.COMM_WORLD.split(color, 0);
    if (myself >= 8) {
      MPI.Finalize();
      System.exit(0);
    }

    group = subset.getGroup();
    groups[cnt++] = group;
    size = group.getSize();
    
    ranges[0][0] = 1; ranges[0][1] = 4; ranges[0][2] = 1;
    ranges[1][0] = 5; ranges[1][1] = 8; ranges[1][2] = 2;
    newgroup = group.rangeIncl(Arrays.copyOf(ranges, 2));
    groups[cnt++] = newgroup;
    size = newgroup.getSize();
    if(size != 6)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Size = " + size +
				  ", should be 6\n");
    for(int i = 0; i < 6; i++) {
      ranks1[i] = i;
    }
    ranks2 = Group.translateRanks(newgroup, Arrays.copyOf(ranks1, 6), group);
    if(ranks2[0] != 1 || ranks2[1] != 2 || ranks2[2] != 3 ||
       ranks2[3] != 4 || ranks2[4] != 5 || ranks2[5] != 7)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Wrong ranks " + ranks2[0] +
				  ranks2[1] + ranks2[2] + ranks2[3] +
				  ranks2[4] + ranks2[5] + ",\n" +
				  "should be 1 2 3 4 5 7\n");

    newgroup = group.rangeExcl(Arrays.copyOf(ranges, 2));
    groups[cnt++] = newgroup;
    size = newgroup.getSize();
    if(size != 2)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Size = " + size +
				  ", should be 2\n");
    ranks2 = Group.translateRanks(newgroup, Arrays.copyOf(ranks1, 2), group);
    if(ranks2[0] != 0 || ranks2[1] != 6)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Wrong ranks " + ranks2[0] +
				  ranks2[1] + "should be 0 6\n");
    
    ranges[0][0] = 6; ranges[0][1] = 0; ranges[0][2] = -3;
    newgroup = group.rangeIncl(Arrays.copyOf(ranges, 1));
    groups[cnt++] = newgroup;
    size = newgroup.getSize();
    if(size != 3)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Size = " + size +
				  ", should be 3\n");
    for(int i = 0; i < 3; i++) {
      ranks1[i] = i;
    }
    ranks2 = Group.translateRanks(newgroup, Arrays.copyOf(ranks1, 3), group);
    if(ranks2[0] != 6 || ranks2[1] != 3 || ranks2[2] != 0)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Wrong ranks " + ranks2[0] +
				  ranks2[1] + ranks2[2] +
				  "should be 6 3 0\n");
    
    newgroup = group.rangeExcl(Arrays.copyOf(ranges, 1));
    groups[cnt++] = newgroup;
    size = newgroup.getSize();
    if(size != 5)  
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Size = " + size +
				  ", should be 5\n");
    ranks2 = Group.translateRanks(newgroup, Arrays.copyOf(ranks1, 5), group);
    if(ranks2[0] != 1 || ranks2[1] != 2 || ranks2[2] != 4 ||
       ranks2[3] != 5 || ranks2[4] != 7)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR: Wrong ranks " + ranks2[0] +
				  ranks2[1] + ranks2[2] + ranks2[3] +
				  ranks2[4] + ",\n" +
				  "should be 1 2 4 5 7\n");
    
    subset.barrier();
    for (int i = 0; i < cnt; i++) 
      groups[i].free();
    subset.free();
    MPI.Finalize();
  }
}
