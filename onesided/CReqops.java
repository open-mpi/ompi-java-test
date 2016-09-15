/*
 *
 * This file is a port from "c_reqops.c" from the ibm
 * regression test package found in the ompi-tests repository. 
 * The formatting of the code is mainly the same as in the 
 * original file.
 *
 *
 * File: CReqops.java			Author: N. Graham
 *
 */

//package onesided;

import static mpi.MPI.slice;

import java.nio.*;

import mpi.*;

public class CReqops
{
	private static final int ITER = 100;
	private static int nproc;
	private static int[] errors = new int[1];
	private static int[] all_errors = new int[1];

	public static void main(String args[]) throws MPIException
	{
		errors[0] = 0;
		all_errors[0] = 0;
		Win window;
		IntBuffer buf = MPI.newIntBuffer(0);
		IntBuffer rank = MPI.newIntBuffer(1);
		IntBuffer val = MPI.newIntBuffer(4);

		MPI.Init(args);
		rank.put(0, MPI.COMM_WORLD.getRank());
		nproc = MPI.COMM_WORLD.getSize();

		if (nproc < 2) {
			if (rank.get(0) == 0)
				System.out.print("Error: must be run with two or more processes\n");
			MPI.COMM_WORLD.abort(1);
		}

		/** Create using MPI_Win_create() **/

		if (rank.get(0) == 0) {
			buf = MPI.newIntBuffer(4);
			buf.put(0, (nproc-1));
		} else {
			//buf = null; //cant initialize a window with a null buffer
		}

                if(nprocs > 4) {
                        window = new Win(buf, nproc, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
                } else {
                        window = new Win(buf, 4, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
                }

		procNullComm(window, val);
		getACC(window, rank, buf);
		getAndPut(window, rank, buf);
		getAndACC(window, rank);
		waitInEpoch(window);
		waitOutEpoch(window);
		waitDiffEpoch(window);
		waitFenceEpoch(window);

		window.free();
		MPI.COMM_WORLD.reduce(errors, all_errors, 1, MPI.INT, MPI.SUM, 0);
		MPI.Finalize();
		
		if (rank.get(0) == 0 && all_errors[0] == 0)
	        System.out.printf(" No Errors\n");
	}

	//PROC_NULL Communication
	private static void procNullComm(Win window, IntBuffer val) throws MPIException 
	{
		Request pn_req[] = new Request[4];
		IntBuffer res = MPI.newIntBuffer(1);

		window.lockAll(0);
		
		pn_req[0] = window.rGetAccumulate(slice(val, 0), 1, MPI.INT, res, 1, MPI.INT, MPI.PROC_NULL, 0, 1, MPI.INT, MPI.REPLACE);
		pn_req[1] = window.rGet(slice(val, 1), 1, MPI.INT, MPI.PROC_NULL, 1, 1, MPI.INT);
		pn_req[2] = window.rPut(slice(val, 2), 1, MPI.INT, MPI.PROC_NULL, 1, 1, MPI.INT);
		pn_req[3] = window.rAccumulate(slice(val, 3), 1, MPI.INT, MPI.PROC_NULL, 0, 1, MPI.INT, MPI.REPLACE);

		assert(pn_req[0] != MPI.REQUEST_NULL);
		assert(pn_req[1] != MPI.REQUEST_NULL);
		assert(pn_req[2] != MPI.REQUEST_NULL);
		assert(pn_req[3] != MPI.REQUEST_NULL);

		window.unlockAll();

		Request.waitAll(pn_req);


		MPI.COMM_WORLD.barrier();

		window.lock(MPI.LOCK_SHARED, 0, 0);
	}

	/* GET-ACC: Test third-party communication, through rank 0. */
	private static void getACC(Win window, IntBuffer rank, IntBuffer buf) throws MPIException 
	{
		for (int i = 0; i < ITER; i++) {
			Request gacc_req;
			IntBuffer res = MPI.newIntBuffer(1);
			IntBuffer val = MPI.newIntBuffer(1);
			val.put(0, -1);
			int exp = -1;

			/* Processes form a ring.  Process 0 starts first, then passes a token
			 * to the right.  Each process, in turn, performs third-party
			 * communication via process 0's window. */
			if (rank.get(0) > 0) {
				MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, (rank.get(0) - 1), 0);
			}
			
			gacc_req = window.rGetAccumulate(rank, 1, MPI.INT, val, 1, MPI.INT, 0, 0, 1, MPI.INT, MPI.REPLACE);
			assert(gacc_req != MPI.REQUEST_NULL);
			gacc_req.waitFor();
			
			exp = (rank.get(0) + nproc - 1) % nproc;
			
			if (val.get(0) != exp) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(), 
						"" + rank + " - Got " + val.get(0) + ", expected " + exp + "\n");
				errors[0]++;
			}

			if (rank.get(0) < nproc-1) {
				MPI.COMM_WORLD.send(null, 0, MPI.BYTE, (rank.get(0) + 1), 0);
			}
			MPI.COMM_WORLD.barrier();
		}

		MPI.COMM_WORLD.barrier();

		if (rank.get(0) == 0)
			buf.put(0, (nproc-1));

		window.sync();	
	}

	/* GET+PUT: Test third-party communication, through rank 0. */
	private static void getAndPut(Win window, IntBuffer rank, IntBuffer buf) throws MPIException 
	{
		for (int i = 0; i < ITER; i++) {
			Request req;
			IntBuffer val = MPI.newIntBuffer(1);
			val.put(0, -1);
			int exp = -1;

			/* Processes form a ring.  Process 0 starts first, then passes a token
			 * to the right.  Each process, in turn, performs third-party
			 * communication via process 0's window. */
			if (rank.get(0) > 0) {
				MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, (rank.get(0) - 1), 0);
			}

			req = window.rGet(val, 1, MPI.INT, 0, 0, 1, MPI.INT);
			assert(req != MPI.REQUEST_NULL);
			req.waitFor();

			req = window.rPut(rank, 1, MPI.INT, 0, 0, 1, MPI.INT);
			assert(req != MPI.REQUEST_NULL);
			req.waitFor();

			exp = (rank.get(0) + nproc-1) % nproc;

			if (val.get(0) != exp) {
				System.out.printf("GET+PUT: %d - Got %d, expected %d\n", rank.get(0), val.get(0), exp);
				errors[0]++;
			}

			/* must wait for remote completion for the result to be correct for the next proc */

			window.flush(0);

			if (rank.get(0) < nproc-1) {
				MPI.COMM_WORLD.send(null, 0, MPI.BYTE, (rank.get(0) + 1), 0);
			}
			MPI.COMM_WORLD.barrier();
		}
		MPI.COMM_WORLD.barrier();

		if (rank.get(0) == 0)
			buf.put(0, (nproc-1));

		window.sync();
	}

	/* GET+ACC: Test third-party communication, through rank 0. */
	private static void getAndACC(Win window, IntBuffer rank) throws MPIException
	{
		for (int i = 0; i < ITER; i++) {
			Request req;
			IntBuffer val = MPI.newIntBuffer(1);
			val.put(0, -1);
			int exp = -1;

			/* Processes form a ring.  Process 0 starts first, then passes a token
			 * to the right.  Each process, in turn, performs third-party
			 * communication via process 0's window. */
			if (rank.get(0) > 0) {
				MPI.COMM_WORLD.recv(null,  0, MPI.BYTE, (rank.get(0) - 1), 0);
			}

			req = window.rGet(val, 1, MPI.INT, 0, 0, 1, MPI.INT);
			assert(req != MPI.REQUEST_NULL);
			req.waitFor();

			req = window.rAccumulate(rank,  1, MPI.INT, 0, 0, 1, MPI.INT, MPI.REPLACE);
			assert(req != MPI.REQUEST_NULL);
			req.waitFor();

			exp = (rank.get(0) + nproc-1) % nproc;

			if (val.get(0) != exp) {
				OmpitestError.ompitestError(OmpitestError.getFileName(), OmpitestError.getLineNumber(), 
						"" + rank + " - Got " + val.get(0) + ", expected " + exp + "\n");
				errors[0]++;
			}

			/* must wait for remote completion for the result to be correct for the next proc */
			window.flush(0);
			if (rank.get(0) < nproc-1) {
				MPI.COMM_WORLD.send(null, 0, MPI.BYTE, (rank.get(0) + 1), 0);
			}
			MPI.COMM_WORLD.barrier();
		}
		window.unlock(0);

		MPI.COMM_WORLD.barrier();
	}

	/* Wait inside of an epoch */
	private static void waitInEpoch(Win window) throws MPIException 
	{
		Request[] pn_req = new Request[4];
		IntBuffer val = MPI.newIntBuffer(4);
		IntBuffer res = MPI.newIntBuffer(1);
		int target = 0;

		window.lockAll(0);

		pn_req[0] = window.rGetAccumulate(slice(val, 0), 1, MPI.INT, res, 1, MPI.INT, target, 0, 1, MPI.INT, MPI.REPLACE);
		pn_req[1] = window.rGet(slice(val, 1), 1, MPI.INT, target, 1, 1, MPI.INT);
		pn_req[2] = window.rPut(slice(val, 2), 1, MPI.INT, target, 2, 1, MPI.INT);
		pn_req[3] = window.rAccumulate(slice(val, 3), 1, MPI.INT, target, 3, 1, MPI.INT, MPI.REPLACE);

		assert(pn_req[0] != MPI.REQUEST_NULL);
		assert(pn_req[1] != MPI.REQUEST_NULL);
		assert(pn_req[2] != MPI.REQUEST_NULL);
		assert(pn_req[3] != MPI.REQUEST_NULL);

		Request.waitAll(pn_req);

		window.unlockAll();

		MPI.COMM_WORLD.barrier();
	}

	/* Wait outside of an epoch */
	private static void waitOutEpoch(Win window) throws MPIException 
	{
		Request[] pn_req = new Request[4];
		IntBuffer val = MPI.newIntBuffer(4);
		IntBuffer res = MPI.newIntBuffer(1);
		int target = 0;

		window.lockAll(0);

		pn_req[0] = window.rGetAccumulate(slice(val, 0), 1, MPI.INT, res, 1, MPI.INT, target, 0, 1, MPI.INT, MPI.REPLACE);
		pn_req[1] = window.rGet(slice(val, 1), 1, MPI.INT, target, 1, 1, MPI.INT);
		pn_req[2] = window.rPut(slice(val, 2), 1, MPI.INT, target, 2, 1, MPI.INT);
		pn_req[3] = window.rAccumulate(slice(val, 3), 1, MPI.INT, target, 3, 1, MPI.INT, MPI.REPLACE);

		assert(pn_req[0] != MPI.REQUEST_NULL);
		assert(pn_req[1] != MPI.REQUEST_NULL);
		assert(pn_req[2] != MPI.REQUEST_NULL);
		assert(pn_req[3] != MPI.REQUEST_NULL);

		window.unlockAll();

		Request.waitAll(pn_req);
	}

	/* Wait in a different epoch */
	private static void waitDiffEpoch(Win window) throws MPIException 
	{
		Request[] pn_req = new Request[4];
		IntBuffer val = MPI.newIntBuffer(4);
		IntBuffer res = MPI.newIntBuffer(1);
		int target = 0;

		window.lockAll(0);

		pn_req[0] = window.rGetAccumulate(slice(val, 0), 1, MPI.INT, res, 1, MPI.INT, target, 0, 1, MPI.INT, MPI.REPLACE);
		pn_req[1] = window.rGet(slice(val, 1), 1, MPI.INT, target, 1, 1, MPI.INT);
		pn_req[2] = window.rPut(slice(val, 2), 1, MPI.INT, target, 2, 1, MPI.INT);
		pn_req[3] = window.rAccumulate(slice(val, 3), 1, MPI.INT, target, 3, 1, MPI.INT, MPI.REPLACE);

		assert(pn_req[0] != MPI.REQUEST_NULL);
		assert(pn_req[1] != MPI.REQUEST_NULL);
		assert(pn_req[2] != MPI.REQUEST_NULL);
		assert(pn_req[3] != MPI.REQUEST_NULL);

		window.unlockAll();

		window.lockAll(0);
		Request.waitAll(pn_req);
		window.unlockAll();
	}

	/* Wait in a fence epoch */
	private static void waitFenceEpoch(Win window) throws MPIException
	{
		Request[] pn_req = new Request[4];
		IntBuffer val = MPI.newIntBuffer(4);
		IntBuffer res = MPI.newIntBuffer(1);
		int target = 0;

		window.lockAll(0);

		pn_req[0] = window.rGetAccumulate(slice(val, 0), 1, MPI.INT, res, 1, MPI.INT, target, 0, 1, MPI.INT, MPI.REPLACE);
		pn_req[1] = window.rGet(slice(val, 1), 1, MPI.INT, target, 1, 1, MPI.INT);
		pn_req[2] = window.rPut(slice(val, 2), 1, MPI.INT, target, 2, 1, MPI.INT);
		pn_req[3] = window.rAccumulate(slice(val, 3), 1, MPI.INT, target, 3, 1, MPI.INT, MPI.REPLACE);

		assert(pn_req[0] != MPI.REQUEST_NULL);
		assert(pn_req[1] != MPI.REQUEST_NULL);
		assert(pn_req[2] != MPI.REQUEST_NULL);
		assert(pn_req[3] != MPI.REQUEST_NULL);

		window.unlockAll();

		window.fence(0);
		Request.waitAll(pn_req);
		window.fence(0);
	}
}
