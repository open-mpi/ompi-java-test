/* 
 *
 * This file is a port from "intercomm.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: InterComm.java			Author: S. Gross
 *
 */

import mpi.*;

public class InterComm
{
  private static int newsize, size, color, key, local_lead,
		     remote_lead, othersum;
  private static int me[] = new int[1],
		     newme[] = new int[1],
		     sum[] = new int[1],
		     newsum[] = new int[1];
  private static boolean flag;
  private static Comm comm, mergecomm;
  private static Intercomm intercomm;
  private static Status status;
  private static Group newgid;

  public static void main (String args[]) throws MPIException
  {
    Comm comm1;
    Intercomm comm2;

    MPI.Init(args);
    me[0] = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    sum[0] = -1;
    newsum[0] = -1;

    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    key = me[0];
    color = me[0] % 2;
    comm = MPI.COMM_WORLD.split(color, key);
    comm1 = comm;
    flag = comm.isInter();
    if(flag)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_test_inter: " +
				  "flag = " + flag + ", should be " +
				  "\"false\"\n");
    newme[0] = comm.getRank();
    comm.allReduce(me, sum, 1, MPI.INT, MPI.SUM);

    local_lead = 0;
    if (color != 0) {
      remote_lead = 0;
    } else {
      remote_lead = 1;
    }
    intercomm =  MPI.COMM_WORLD.createIntercomm(comm, local_lead,
						remote_lead, 5);
    comm2 = intercomm;
    inter_tests();

    comm = (Comm) intercomm.clone();
    intercomm =  (Intercomm) comm;
    inter_tests();

    MPI.COMM_WORLD.barrier();
    comm.free();
    comm1.free();
    comm2.free();
    MPI.Finalize();
  }



  private static void inter_tests() throws MPIException
  {
    flag = intercomm.isInter();
    if(!flag)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_test_inter: " +
				  "flag = " + flag + ", should be " +
				  "\"true\"\n");
 
    newsize = intercomm.getRemoteSize();
    if(newsize != size/2)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_remote_size: " +
				  "size = " + newsize +
				  ", should be " + size/2 + "\n");

    newgid = intercomm.getRemoteGroup();
    newsize = newgid.getSize();
    if(newsize != size/2)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Comm_remote_group: " +
				  "size = " + newsize +
				  ", should be " + size/2 + "\n");

    newsum[0] = sum[0];
    status = intercomm.sendRecvReplace(newsum, 1, MPI.INT,
				       newme[0], 7, newme[0], 7);
    othersum = size/2*(size/2-1);
    if(me[0]%2 == 0) {
      othersum += size/2;
    }
    if(othersum != newsum[0])
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in Intercomm_create, sum = " +
				  othersum + ", should be "+
				  newsum[0] + "\n");

    /* Java needs a boolean value as parameter.
     * color == 0: complying with "false" in the "man page"
     * color != 0: complying with "true" in the "man page"
     */
    mergecomm = intercomm.merge(color == 0);
    mergecomm.allReduce(me, newsum, 1, MPI.INT, MPI.SUM);
    if(newsum[0] != size*(size-1)/2)
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Intercomm_merge: sum = " +
				  newsum + ", should be " +
				  size*(size-1)/2 + "\n");
    mergecomm.free();
    newgid.free();
  }
}
