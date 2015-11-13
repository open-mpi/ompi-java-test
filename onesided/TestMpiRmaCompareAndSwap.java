/*
 *
 * This file is a port from "test_mpi_rma_compare_and_swap.c" from the mpich
 * test package found at http://git.mpich.org/mpich.git/blob/c77631474f072e86c9fe761c1328c3d4cb8cc4a5:/test/mpi/rma/compare_and_swap.c#l1
 * The formatting of the code is similar to the original file.
 *
 *
 * File: TestMpiRmaCompareAndSwap.java			Author: N. Graham
 *
 */

import java.nio.*;
import mpi.*; 

public class TestMpiRmaCompareAndSwap {
	private static int ITER = 100;

	public static void main(String[] args) throws MPIException {
		int rank, nproc;
		int[] errors = new int[1];
		int[] allErrors = new int[1];
		IntBuffer valPtr = MPI.newIntBuffer(1);
		Win win;

		errors[0] = 0;
		allErrors[0] = 0;

		MPI.Init(args);

		rank = MPI.COMM_WORLD.getRank();
		nproc = MPI.COMM_WORLD.getSize();

		valPtr.put(0, 0);

		win = new Win(valPtr, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);

		/* Test self communication */

		for (int i = 0; i < ITER; i++) {
			IntBuffer next = MPI.newIntBuffer(1);
			IntBuffer iBuffer = MPI.newIntBuffer(1);
			IntBuffer result = MPI.newIntBuffer(1);

			next.put(0, (i + 1));
			iBuffer.put(0, i);
			result.put(0, -1);

			win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
			win.compareAndSwap(next, iBuffer, result, MPI.INT, rank, 0);
			win.unlock(rank);

			if (result.get(0) != i) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + rank + "->" + rank + " -- Error: next=" + next.get(0) + " compare=" + iBuffer.get(0) + 
						" result=" + result.get(0) + " val=" + valPtr.get(0) + "\n");
				errors[0]++;
			}
		}

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		valPtr.put(0, 0);
		win.unlock(rank);

		MPI.COMM_WORLD.barrier();

		/* Test neighbor communication */

		for (int i = 0; i < ITER; i++) {
			IntBuffer next = MPI.newIntBuffer(1);
			IntBuffer iBuffer = MPI.newIntBuffer(1);
			IntBuffer result = MPI.newIntBuffer(1);

			next.put(0, (i + 1));
			iBuffer.put(0, i);
			result.put(0, -1);

			win.lock(MPI.LOCK_EXCLUSIVE, (rank+1)%nproc, 0);
			win.compareAndSwap(next, iBuffer, result, MPI.INT, (rank+1)%nproc, 0);
			win.unlock((rank+1)%nproc);
			if (result.get(0) != i) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + rank + "->" + (rank+1)%nproc + " -- Error: next=" + next.get(0) + " compare=" + iBuffer.get(0) + 
						" result=" + result.get(0) + " val=" + valPtr.get(0) + "\n");
				errors[0]++;
			}
		}

		MPI.COMM_WORLD.barrier();
		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		valPtr.put(0, 0);
		win.unlock(rank);
		MPI.COMM_WORLD.barrier();

		/* Test contention */

		if (rank != 0) {
			for (int i = 0; i < ITER; i++) {
				IntBuffer next = MPI.newIntBuffer(1);
				IntBuffer iBuffer = MPI.newIntBuffer(1);
				IntBuffer result = MPI.newIntBuffer(1);

				next.put(0, (i + 1));
				iBuffer.put(0, i);
				result.put(0, -1);

				win.lock(MPI.LOCK_EXCLUSIVE, 0, 0);
				win.compareAndSwap(next, iBuffer, result, MPI.INT, 0, 0);
				win.unlock(0);
			}
		}

		MPI.COMM_WORLD.barrier();

		if (rank == 0 && nproc > 1) {
			if (valPtr.get(0) != ITER) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + rank + " - Error: expected=" + ITER + " val=" + valPtr.get(0) + "\n");
				errors[0]++;
			}
		}

		win.free();

		MPI.COMM_WORLD.reduce(errors, allErrors, 1, MPI.INT, MPI.SUM, 0);

		if (rank == 0 && allErrors[0] == 0)
			System.out.printf(" No Errors\n");

		MPI.Finalize();
	}

}