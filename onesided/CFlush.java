/*
 *
 * This file is a port from "c_flush.c" from the ibm
 * regression test package found in the ompi-tests repository. 
 * The formatting of the code is similar to the original file.
 *
 *
 * File: CFlush.java			Author: N. Graham
 *
 */

import java.nio.*;
import mpi.*;

public class CFlush {
	private static int ITER = 100;

public static void main(String[] args) throws MPIException {
    int nproc;
    IntBuffer buf = MPI.newIntBuffer(1);
    IntBuffer rank = MPI.newIntBuffer(1);
    int[] errors = new int[1];
    int[] allErrors = new int[1];
    
    errors[0] = 0;
    allErrors[0] = 0;
    
    Win window;

    MPI.Init(args);
    rank.put(0, MPI.COMM_WORLD.getRank());
    nproc = MPI.COMM_WORLD.getSize();

    if (nproc < 2) {
        if (rank.get(0) == 0) 
        	System.out.printf("Error: must be run with two or more processes\n");
        MPI.COMM_WORLD.abort(1);
    }

    /** Create using MPI_Win_create() **/

    if (rank.get(0) == 0) {
      buf.put(0, (nproc-1));
    } else {
      //buf = NULL; null value for creating window not supported
    }
    
    if(rank.get(0) == 0) {
    	window = new Win(buf, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    } else {
    	window = new Win(buf, 0, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
    }

    /* Test flush of an empty epoch */
    window.lock(MPI.LOCK_SHARED, 0, 0);
    window.flushAll();
    window.unlock(0);

    MPI.COMM_WORLD.barrier();

    /* Test third-party communication, through rank 0. */
    window.lock(MPI.LOCK_SHARED, 0, 0);

    for (int i = 0; i < ITER; i++) {
        IntBuffer val = MPI.newIntBuffer(1);
        val.put(0, -1);
        int exp = -1;

        /* Processes form a ring.  Process 0 starts first, then passes a token
         * to the right.  Each process, in turn, performs third-party
         * communication via process 0's window. */
        if (rank.get(0) > 0) {
        	MPI.COMM_WORLD.recv(null, 0, MPI.BYTE, (rank.get(0)-1), 0);
        }

        window.getAccumulate(rank, 1, MPI.INT, val, 1, MPI.INT, 0, 0, 1, MPI.INT, MPI.REPLACE);
        window.flush(0);

        exp = (rank.get(0) + nproc-1) % nproc;

        if (val.get(0) != exp) {
            System.out.printf("%d - Got %d, expected %d\n", rank, val, exp);
            errors[0]++;
        }

        if (rank.get(0) < nproc-1) {
        	MPI.COMM_WORLD.send(null, 0, MPI.BYTE, (rank.get(0)+1), 0);
        }

        MPI.COMM_WORLD.barrier();
    }

    window.unlock(0);

    window.free();

    MPI.COMM_WORLD.reduce(errors, allErrors, 1, MPI.INT, MPI.SUM, 0);

    if (rank.get(0) == 0 && allErrors[0] == 0)
        System.out.printf(" No Errors\n");

    MPI.Finalize();
}
}