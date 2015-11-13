/* 
 *
 * This file is a port from "mprobe-mpich.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: MprobeMpich.java		Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class MprobeMpich
{
  private static int errs[] = new int[1];

  public static void main (String args[]) throws MPIException
  {
    int rank, size;
    int sendbuf[] = new int[8];
    IntBuffer recvbuf = MPI.newIntBuffer(8);
    int count;
    Request rreq;
    Status s1, s2;

    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    errs[0] = 0;

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);

    /* all processes besides ranks 0 & 1 aren't used by this test */
    if (rank >= 2) {
      /* Nevertheless, the processes must report there number of
       * errors, before they terminate
       */
      MPI.COMM_WORLD.reduce(errs, errs, 1, MPI.INT, MPI.SUM, 0);
      MPI.Finalize();
      System.exit(0);
    }

    /* test 0: simple send & mprobe+mrecv */
    if (rank == 0) {
        sendbuf[0] = 0xdeadbeef;
        sendbuf[1] = 0xfeedface;
	MPI.COMM_WORLD.send (sendbuf, 2, MPI.INT, 1, 5);
    }
    else {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.mProbe(0, 5, MPI.COMM_WORLD);
        check(s1.getSource() == 0);
        check(s1.getTag() == 5);
        check(!msg.isNull());

	count = s1.getCount(MPI.INT);
        check(count == 2);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        s2 = msg.mRecv(recvbuf, count, MPI.INT);
        check(recvbuf.get(0) == 0xdeadbeef);
        check(recvbuf.get(1) == 0xfeedface);
        check(s2.getSource() == 0);
        check(s2.getTag() == 5);
        check(msg.isNull());
    }

    /* test 1: simple send & mprobe+imrecv */
    if (rank == 0) {
        sendbuf[0] = 0xdeadbeef;
        sendbuf[1] = 0xfeedface;
	MPI.COMM_WORLD.send (sendbuf, 2, MPI.INT, 1, 5);
    }
    else {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.mProbe(0, 5, MPI.COMM_WORLD);
        check(s1.getSource() == 0);
        check(s1.getTag() == 5);
        check(!msg.isNull());

	count = s1.getCount(MPI.INT);
        check(count == 2);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        rreq = msg.imRecv(recvbuf, count, MPI.INT);
        check(!rreq.isNull());
	s2 = rreq.waitStatus();
        rreq.free();
        check(recvbuf.get(0) == 0xdeadbeef);
        check(recvbuf.get(1) == 0xfeedface);
        check(s2.getSource() == 0);
        check(s2.getTag() == 5);
        check(msg.isNull());
    }

    /* test 2: simple send & improbe+mrecv */
    if (rank == 0) {
        sendbuf[0] = 0xdeadbeef;
        sendbuf[1] = 0xfeedface;
	MPI.COMM_WORLD.send (sendbuf, 2, MPI.INT, 1, 5);
    }
    else {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        do {
            check(msg.isNull());
            s1 = msg.imProbe(0, 5, MPI.COMM_WORLD);
        } while (s1 == null);
        check(!msg.isNull());
        check(s1.getSource() == 0);
        check(s1.getTag() == 5);

	count = s1.getCount(MPI.INT);
        check(count == 2);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        s2 = msg.mRecv(recvbuf, count, MPI.INT);
        check(recvbuf.get(0) == 0xdeadbeef);
        check(recvbuf.get(1) == 0xfeedface);
        check(s2.getSource() == 0);
        check(s2.getTag() == 5);
        check(msg.isNull());
    }

    /* test 3: simple send & improbe+imrecv */
    if (rank == 0) {
        sendbuf[0] = 0xdeadbeef;
        sendbuf[1] = 0xfeedface;
	MPI.COMM_WORLD.send (sendbuf, 2, MPI.INT, 1, 5);
    }
    else {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        do {
            check(msg.isNull());
            s1 = msg.imProbe(0, 5, MPI.COMM_WORLD);
        } while (s1 == null);
        check(!msg.isNull());
        check(s1.getSource() == 0);
        check(s1.getTag() == 5);

	count = s1.getCount(MPI.INT);
        check(count == 2);

        rreq = msg.imRecv(recvbuf, count, MPI.INT);
        check(!rreq.isNull());
	s2 = rreq.waitStatus();
        rreq.free();
        check(recvbuf.get(0) == 0xdeadbeef);
        check(recvbuf.get(1) == 0xfeedface);
        check(s2.getSource() == 0);
        check(s2.getTag() == 5);
        check(msg.isNull());
    }

    /* test 4: mprobe+mrecv with MPI.PROC_NULL */
    {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.mProbe(MPI.PROC_NULL, 5, MPI.COMM_WORLD);
        check(s1.getSource() == MPI.PROC_NULL);
        check(s1.getTag() == MPI.ANY_TAG);
        check(msg.isNoProc());

	count = s1.getCount(MPI.INT);
        check(count == 0);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        s2 = msg.mRecv(recvbuf, count, MPI.INT);
        /* recvbuf should remain unmodified */
        check(recvbuf.get(0) == 0x01234567);
        check(recvbuf.get(1) == 0x89abcdef);
        /* should get back "proc null status" */
        check(s2.getSource() == MPI.PROC_NULL);
        check(s2.getTag() == MPI.ANY_TAG);
        check(msg.isNull());
	count = s2.getCount(MPI.INT);
        check(count == 0);
    }

    /* test 5: mprobe+imrecv with MPI.PROC_NULL */
    {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.mProbe(MPI.PROC_NULL, 5, MPI.COMM_WORLD);
        check(s1.getSource() == MPI.PROC_NULL);
        check(s1.getTag() == MPI.ANY_TAG);
        check(msg.isNoProc());
	count = s1.getCount(MPI.INT);
        check(count == 0);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        rreq = msg.imRecv(recvbuf, count, MPI.INT);
        check(!rreq.isNull());
	s2 = rreq.waitStatus(); /* single test should always succeed */
        rreq.free();
        check(s2 != null);
        /* recvbuf should remain unmodified */
        check(recvbuf.get(0) == 0x01234567);
        check(recvbuf.get(1) == 0x89abcdef);
        /* should get back "proc null status" */
        check(s2.getSource() == MPI.PROC_NULL);
        check(s2.getTag() == MPI.ANY_TAG);
        check(msg.isNull());
	count = s2.getCount(MPI.INT);
        check(count == 0);
    }

    /* test 6: improbe+mrecv with MPI.PROC_NULL */
    {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.imProbe(MPI.PROC_NULL, 5, MPI.COMM_WORLD);
        check(s1 != null);
        check(msg.isNoProc());
        check(s1.getSource() == MPI.PROC_NULL);
        check(s1.getTag() == MPI.ANY_TAG);
	count = s1.getCount(MPI.INT);
        check(count == 0);

        recvbuf.put(0, 0x01234567);
        recvbuf.put(1, 0x89abcdef);
        s2 = msg.mRecv(recvbuf, count, MPI.INT);
        /* recvbuf should remain unmodified */
        check(recvbuf.get(0) == 0x01234567);
        check(recvbuf.get(1) == 0x89abcdef);
        /* should get back "proc null status" */
        check(s2.getSource() == MPI.PROC_NULL);
        check(s2.getTag() == MPI.ANY_TAG);
        check(msg.isNull());
	count = s2.getCount(MPI.INT);
        check(count == 0);
    }

    /* test 7: improbe+imrecv */
    {
        /* I don't port the next two statements to Java	*/
        // memset(&s1, 0xab, sizeof(MPI_Status));
        // memset(&s2, 0xab, sizeof(MPI_Status));

        Message msg = new Message();
        s1 = msg.imProbe(MPI.PROC_NULL, 5, MPI.COMM_WORLD);
        check(s1 != null);
        check(msg.isNoProc());
        check(s1.getSource() == MPI.PROC_NULL);
        check(s1.getTag() == MPI.ANY_TAG);
	count = s1.getCount(MPI.INT);
        check(count == 0);

        rreq = msg.imRecv(recvbuf, count, MPI.INT);
        check(!rreq.isNull());
        s2 = rreq.testStatus(); /* single test should always succeed */
        rreq.free();
        check(s2 != null);
        /* recvbuf should remain unmodified */
        check(recvbuf.get(0) == 0x01234567);
        check(recvbuf.get(1) == 0x89abcdef);
        /* should get back "proc null status" */
        check(s2.getSource() == MPI.PROC_NULL);
        check(s2.getTag() == MPI.ANY_TAG);
        check(msg.isNull());
	count = s2.getCount(MPI.INT);
        check(count == 0);
    }

    /* TODO MPI.ANY_SOURCE and MPI.ANY_TAG should be tested as well */
    /* TODO a full range of message sizes should be tested too */
    /* TODO threaded tests are also needed, but they should go in a separate
     * program */

    MPI.COMM_WORLD.reduce(errs, 1, MPI.INT, MPI.SUM, 0);

    if (rank == 0) {
        if (errs[0] != 0) {
            System.out.printf("Found %d errors\n", errs[0]);
        }
        else {
            System.out.printf("No errors\n");
        }
    }

    MPI.Finalize();
    System.exit((0 == errs[0]) ? 0 : 77);
  }


  /* assert-like macro that bumps the err count and emits a message */
  private static void check(boolean b) {
    if (!b) {
      ++errs[0];
      if (errs[0] < 10) {
	System.err.printf("check failed in line %d\n",
			  OmpitestError.getLineNumber());
      }
    }
  }
}
