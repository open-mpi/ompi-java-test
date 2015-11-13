/*
 *
 * This file is a port from "win_allocate.c" from the ibm
 * regression test package found in the ompi-tests repository. 
 * The formatting of the code is similar to the original file.
 *
 *
 * File: WinAllocate.java			Author: N. Graham
 *
 */

import java.nio.*;
import mpi.*; 

public class WinAllocate {

	private static final int BASE_SIZE = 8192;

	public static void main(String[] args) throws MPIException
	{
		CharBuffer myPtr = MPI.newCharBuffer(0);
		Win win, sharedWin;
		int rank, size, shmRank, shmNproc, peer;
		int mySize, peer_size;
		int peer_disp;
		CharBuffer peerPtr = MPI.newCharBuffer(0);
		Comm shmComm;

		MPI.Init(args);
		MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);
		rank = MPI.COMM_WORLD.getRank();
		size = MPI.COMM_WORLD.getSize();
		
		shmComm = MPI.COMM_WORLD.splitType(Comm.TYPE_SHARED, rank, MPI.INFO_NULL);
		
		shmRank = MPI.COMM_WORLD.getRank();
		shmNproc = MPI.COMM_WORLD.getSize();

		mySize = BASE_SIZE + (shmRank + 1);

		win = new Win(mySize, 1, MPI.INFO_NULL, shmComm, myPtr, Win.FLAVOR_PRIVATE);
		sharedWin = new Win(mySize, 1, MPI.INFO_NULL, shmComm, myPtr, Win.FLAVOR_SHARED);

		win.free();
		sharedWin.free();
		shmComm.free();

		MPI.Finalize();
		if(rank == 0)
			System.out.println("Test Completed");
	}
}