/*
 *
 * This file is a port from "c_fetch_and_op.c" from the ibm
 * regression test package found in the ompi-tests repository. 
 * The formatting of the code is similar to the original file.
 *
 *
 * File: CFetchAndOp.java			Author: N. Graham
 *
 */

import static mpi.MPI.slice;

import java.nio.*;

import mpi.*; 

public class CFetchAndOp {
	private static int ITER = 100;
	private static int[] errors = new int[1];
	private static int[] allErrors = new int[1];

	public static void main(String[] args) throws MPIException {
		int rank, nproc;
		IntBuffer valPtr, resPtr;
		Win win;
		errors[0] = 0;
		allErrors[0] = 0;

		MPI.Init(args);

		rank = MPI.COMM_WORLD.getRank();
		nproc = MPI.COMM_WORLD.getSize();

		valPtr = MPI.newIntBuffer(nproc);
		resPtr = MPI.newIntBuffer(nproc);

		win = new Win(valPtr, nproc, 1, MPI.INFO_NULL, MPI.COMM_WORLD);

		selfComm(valPtr, resPtr, win, rank);
		neighborComm(valPtr, resPtr, win, rank, nproc);
		contention(valPtr, resPtr, win, rank, nproc);
		allToAllCommFence(valPtr, resPtr, win, rank, nproc);
		allToAllCommLockAll(valPtr, resPtr, win, rank, nproc);
		allToAllCommLockAllFlush(valPtr, resPtr, win, rank, nproc);
		noOpNeighbor(valPtr, resPtr, win, rank, nproc);
		noOpSelf(valPtr, resPtr, win, rank, nproc);

		win.free();

		MPI.COMM_WORLD.reduce(errors, allErrors, 1, MPI.INT, MPI.SUM, 0);

		MPI.Finalize();
		if (rank == 0 && allErrors[0] == 0)
	        System.out.printf(" No Errors\n");
	}

	// Test self communication
	private static void selfComm(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank) throws MPIException {
		resetVars(valPtr, resPtr, win);

		for (int i = 0; i < ITER; i++) {
			IntBuffer one = MPI.newIntBuffer(1); 
			IntBuffer result = MPI.newIntBuffer(1);
			one.put(0, 1);
			result.put(0, -1);

			win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
			win.fetchAndOp(one, result, MPI.INT, rank, 0, MPI.SUM);
			win.unlock(rank);
		}

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		if ( CMP(valPtr.get(0), ITER) ) {
			OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
					""+ rank + "->" + rank + " -- SELF: expected " + ITER + ", got "+ valPtr.get(0) + "\n");
			errors[0]++;
		}
		win.unlock(rank);
	}

	// Test neighbor communication
	private static void neighborComm(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		resetVars(valPtr, resPtr, win);

		for (int i = 0; i < ITER; i++) {
			IntBuffer one = MPI.newIntBuffer(1); 
			IntBuffer result = MPI.newIntBuffer(1);
			one.put(0, 1);
			result.put(0, -1);

			win.lock(MPI.LOCK_EXCLUSIVE, (rank+1)%nproc, 0);
			win.fetchAndOp(one, result, MPI.INT, (rank+1)%nproc, 0, MPI.SUM);
			win.unlock((rank+1)%nproc);
			if ( CMP(result.get(0), i) ) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + ((rank+1)%nproc) + "->" + rank + " -- NEIGHBOR[" + i + "]: expected result "
								+ i + ", got "+ result.get(0) + "\n");
				errors[0]++;
			}
		}

		MPI.COMM_WORLD.barrier();

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		if ( CMP(valPtr.get(0), ITER) ) {
			OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
					"" + ((rank+1)%nproc) + "->" + rank + " -- NEIGHBOR: expected " + ITER + 
					", got "+ valPtr.get(0) + "\n");
			errors[0]++;
		}
		win.unlock(rank);
	}

	// Test contention
	private static void contention(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		resetVars(valPtr, resPtr, win);

		if (rank != 0) {
			for (int i = 0; i < ITER; i++) {
				IntBuffer one = MPI.newIntBuffer(1); 
				IntBuffer result = MPI.newIntBuffer(1);
				one.put(0, 1);

				win.lock(MPI.LOCK_EXCLUSIVE, 0, 0);
				win.fetchAndOp(one, result, MPI.INT, 0, 0, MPI.SUM);
				win.unlock(0);
			}
		}

		MPI.COMM_WORLD.barrier();

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		if (rank == 0 && nproc > 1) {
			if ( CMP(valPtr.get(0), ITER*(nproc-1)) ) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"*->" + rank + " - CONTENTION: expected=" + (ITER*(nproc-1)) + " val=" + valPtr.get(0) +"\n");
				errors[0]++;
			}
		}
		win.unlock(rank);
	}

	//Test all-to-all communication (fence)
	private static void allToAllCommFence(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		resetVars(valPtr, resPtr, win);

		for (int i = 0; i < ITER; i++) {
			win.fence(MPI.MODE_NOPRECEDE);

			for (int j = 0; j < nproc; j++) {
				IntBuffer rankCNV = MPI.newIntBuffer(1);
				rankCNV.put(0, rank);

				win.fetchAndOp(rankCNV, slice(resPtr, j), MPI.INT, j, rank, MPI.SUM);
				resPtr.put(j, (i*rank));
			}
			win.fence(MPI.MODE_NOSUCCEED);
			MPI.COMM_WORLD.barrier();

			for (int j = 0; j < nproc; j++) {
				if (CMP(resPtr.get(j), (i*rank))) {
					OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
							"" + rank + "->" + j + " -- ALL-TO-ALL (FENCE) [" + i + "]: expected result " + 
									(i*rank) + ", got " + resPtr.get(j) + "\n");
					errors[0]++;
				}
			}
		}

		MPI.COMM_WORLD.barrier();
		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		for(int i = 0; i < nproc; i++) {
			if (CMP(valPtr.get(i), (ITER*i))) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + i + "->" + rank + " -- ALL-TO-ALL (FENCE): expected " + (ITER*i) + 
						", got " + valPtr.get(i) + "\n");
				errors[0]++;
			}
		}
		win.unlock(rank);
	}

	//Test all-to-all communication (lock-all)
	private static void allToAllCommLockAll(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		resetVars(valPtr, resPtr, win);

		for (int i = 0; i < ITER; i++) {
			int j;

			win.lockAll(0);
			for (j = 0; j < nproc; j++) {
				IntBuffer rankCNV = MPI.newIntBuffer(1);
				rankCNV.put(0, rank);
				win.fetchAndOp(rankCNV, slice(resPtr, j), MPI.INT, j, rank, MPI.SUM);
				resPtr.put(j, (i*rank));
			}
			win.unlockAll();
			MPI.COMM_WORLD.barrier();

			for (j = 0; j < nproc; j++) {
				if (CMP(resPtr.get(j), (i*rank))) {
					OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
							"" + rank + "->" + j + " -- ALL-TO-ALL (LOCK-ALL) [" + i + "]: expected result " + 
									(i*rank) + ", got " + (resPtr.get(j)) + "\n");
					errors[0]++;
				}
			}
		}

		MPI.COMM_WORLD.barrier();
		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		for (int i = 0; i < nproc; i++) {
			if (CMP(valPtr.get(i), (ITER*i))) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + i + "->" + rank + " -- ALL-TO-ALL (LOCK-ALL): expected " + (ITER*i) + 
						", got " + valPtr.get(i) + "\n");
				errors[0]++;
			}
		}
		win.unlock(rank);
	}

	// Test all-to-all communication (lock-all+flush)
	private static void allToAllCommLockAllFlush(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		resetVars(valPtr, resPtr, win);

		for (int i = 0; i < ITER; i++) {
			win.lockAll(0);

			for (int j = 0; j < nproc; j++) {
				IntBuffer rankCNV = MPI.newIntBuffer(1);
				rankCNV.put(0, rank);
				win.fetchAndOp(rankCNV, slice(resPtr, j), MPI.INT, j, rank, MPI.SUM);
				resPtr.put(j, (i*rank));
				win.flush(j);
			}
			win.unlockAll();
			MPI.COMM_WORLD.barrier();

			for (int j = 0; j < nproc; j++) {
				if (CMP(resPtr.get(j), (i*rank))) {
					OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
							"" + rank + "->" + j + " -- ALL-TO-ALL (LOCK-ALL+FLUSH) [" + i + 
							                                                         "]: expected result " + (i*rank) +", got " + resPtr.get(j) + "\n");
					errors[0]++;
				}
			}
		}

		MPI.COMM_WORLD.barrier();
		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		for (int i = 0; i < nproc; i++) {
			if (CMP(valPtr.get(i), (ITER*i))) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + i + "-> " + rank + " -- ALL-TO-ALL (LOCK-ALL+FLUSH): expected " + (ITER*i) + 
						", got " + valPtr.get(i) + "\n");
				errors[0]++;
			}
		}
		win.unlock(rank);
	}

	//Test NO_OP (neighbor communication)
	private static void noOpNeighbor(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		IntBuffer nullPtr = MPI.newIntBuffer(0); //can not use null in fetchAndOp
		
		MPI.COMM_WORLD.barrier();
		resetVars(valPtr, resPtr, win);

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		for (int i = 0; i < nproc; i++)
			valPtr.put(i, rank);
		win.unlock(rank);
		MPI.COMM_WORLD.barrier();

		for (int i = 0; i < ITER; i++) {
			int target = (rank+1) % nproc;

			win.lock(MPI.LOCK_EXCLUSIVE, target, 0);
			win.fetchAndOp(nullPtr, resPtr, MPI.INT, target, 0, MPI.NO_OP);
			win.unlock(target);

			if (resPtr.get(0) != target) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + target + "->" + rank + " -- NOP[" + i + "]: expected " + target + 
						", got " + resPtr.get(0) + "\n");
				errors[0]++;
			}
		}
	}

	// Test NO_OP (self communication)
	private static void noOpSelf(IntBuffer valPtr, IntBuffer resPtr, Win win, int rank, int nproc) throws MPIException {
		IntBuffer nullPtr = MPI.newIntBuffer(0); //can not use null in fetchAndOp
		
		MPI.COMM_WORLD.barrier();
		resetVars(valPtr, resPtr, win);

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);
		for (int i = 0; i < nproc; i++)
			valPtr.put(i, rank);
		win.unlock(rank);
		MPI.COMM_WORLD.barrier();

		for (int i = 0; i < ITER; i++) {
			int target = rank;

			win.lock(MPI.LOCK_EXCLUSIVE, target, 0);
			win.fetchAndOp(nullPtr, resPtr, MPI.INT, target, 0, MPI.NO_OP);
			win.unlock(target);

			if (resPtr.get(0) != target) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(),
						"" + target + "->" + rank + " -- NOP_SELF[" + i + "]: expected " + target + 
						", got " + resPtr.get(0) + "\n");
				errors[0]++;
			}
		}
	}

	private static void resetVars(IntBuffer valPtr, IntBuffer resPtr, Win win) throws MPIException {
		int rank, nproc;

		rank = MPI.COMM_WORLD.getRank();
		nproc = MPI.COMM_WORLD.getSize();

		win.lock(MPI.LOCK_EXCLUSIVE, rank, 0);

		for (int i = 0; i < nproc; i++) {
			valPtr.put(i, 0);
			resPtr.put(i, -1);
		}
		win.unlock(rank);

		MPI.COMM_WORLD.barrier();
	}

	private static boolean CMP(int x, int y) {
		return (x - y) > 0.000000001;
	}
}
