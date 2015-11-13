/* 
 *
 * This file is a port from "dup_with_info.c" from the mpich
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File:CommDupWithInfo			Author: N. Graham
 *
 */

import mpi.*;

public class CommDupWithInfo {

	public static void main(String[] args) throws MPIException
	{
		int totalErrs = 0;
		Comm newComm;
		Info info;

		MPI.Init(args);

		int rank = MPI.COMM_WORLD.getRank();
		
		/* Dup with no info */
		newComm = MPI.COMM_WORLD.dupWithInfo(MPI.INFO_NULL);
		totalErrs += runTests(newComm);
		newComm.free();

		/* Dup with info keys */
		info = new Info();
		info.set("host", "myhost.myorg.org");
		info.set("file", "runfile.txt");
		info.set("soft", "2:1000:4,3:1000:7");
		newComm = MPI.COMM_WORLD.dupWithInfo(info);
		totalErrs += runTests(newComm);
		info.free();
		newComm.free();

		/* Dup with deleted info keys */
		info = new Info();
		info.set("host", "myhost.myorg.org");
		info.set("file", "runfile.txt");
		info.set("soft", "2:1000:4,3:1000:7");
		newComm = MPI.COMM_WORLD.dupWithInfo(info);
		info.free();
		totalErrs += runTests(newComm);
		newComm.free();

		if(rank == 0) {
			if(totalErrs == 0) {
				System.out.println("Test Passed");
			} else {
				System.out.println("Test Failed");
			}
		}

		MPI.Finalize();
	}

	private static int runTests(Comm comm) throws MPIException
	{
		int rank, size, wRank, wSize, dest, errs = 0;
		int[] a = new int[1];
		int[] b = new int[1];
		Status status;

		/* Check basic properties */
		wSize = MPI.COMM_WORLD.getSize();
		wRank = MPI.COMM_WORLD.getRank();
		size = comm.getSize();
		rank = comm.getRank();

		if (size != wSize || rank != wRank) {
			errs++;
			System.err.printf("Size (" + size + ") or rank (" + rank + ") wrong\n");
		}

		comm.barrier();

		/* Can we communicate with this new communicator? */
		dest = MPI.PROC_NULL;
		if (rank == 0) {
			dest = size - 1;
			a[0] = rank;
			b[0] = -1;
			status = comm.sendRecv(a, 1, MPI.INT, dest, 0, b, 1, MPI.INT, dest, 0);

			if (b[0] != dest) {
				errs++;
				System.err.printf("Received " + b + " expected " + dest + " on + " + rank + "\n");
			}
			if (status.getSource() != dest) {
				errs++;
				System.err.printf("Source not set correctly in status on " + rank + "\n");
			}
		}
		else if (rank == size - 1) {
			dest = 0;
			a[0] = rank;
			b[0] = -1;
			status = comm.sendRecv(a, 1, MPI.INT, dest, 0, b, 1, MPI.INT, dest, 0);
			if (b[0] != dest) {
				errs++;
				System.err.printf("Received " + b + " expected " + dest + " on " + rank + "\n");
			}
			if (status.getSource() != dest) {
				errs++;
				System.err.printf("Source not set correctly in status on " + rank + "\n");
			}
		}
		comm.barrier();

		return errs;
	}
}