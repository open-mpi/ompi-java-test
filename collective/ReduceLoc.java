/* 
 *
 * This file is a port from "reduce_loc.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ReduceLoc.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class ReduceLoc
{
  private final static boolean DB_TALK = false;
  private final static int SIZEOF_SHORT = 2;
  private final static int SIZEOF_INT = 4;
  private final static int SIZEOF_LONG = 8;
  private final static int MAXLEN = 100000;

  public static void main (String args[]) throws MPIException
  {
    int rank, size;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    
    check_si(rank, size);
    check_li(rank, size);
    
    MPI.Finalize();
  }


  private static void check_si(int rank, int size) throws MPIException
  {
    //    struct si {
    //      short s;
    //      int i;
    //    } *in, *out;
    //    in = malloc(sizeof(struct si) * MAXLEN);
    //    out = malloc(sizeof(struct si) * MAXLEN);
    ByteBuffer in  = MPI.newByteBuffer(MPI.SHORT_INT.getExtent() * MAXLEN),
	       out = MPI.newByteBuffer(MPI.SHORT_INT.getExtent() * MAXLEN);

    if (DB_TALK) {
      System.out.printf("Sizeof struct_short_int: %d, " +
			"sizeof short: %d, sizeof int: %d\n",
			MPI.SHORT_INT.getExtent(), SIZEOF_SHORT, SIZEOF_INT);
    }

    for (int i = 0; i < MAXLEN; ++i) {
      //      memset(&(out[i]), 17, sizeof(out[i]));
      ShortInt.Data co = MPI.shortInt.getData(out, i);
      co.putValue((short) 17);
      co.putIndex(17);
    }
    for (int count = 2000; count <= MAXLEN; count *= 10) {
      for (int root = 0; root < size; ++root) {
	for (int i = 0; i < count; i++) {
	  ShortInt.Data co = MPI.shortInt.getData(out, i);
	  ShortInt.Data ci = MPI.shortInt.getData(in, i);
	  //	  out[i].s = rank;
	  //	  out[i].i = i + rank;
	  //	  in[i].s = 1;
	  //	  in[i].i = 3;
	  co.putValue((short) rank);
	  co.putIndex(i + rank);
	  ci.putValue((short) 1);
	  ci.putIndex(3);
	}

	MPI.COMM_WORLD.reduce(out, in, count, MPI.SHORT_INT, MPI.MINLOC, root);

	if (root == rank) {
	  for (int i = 0; i < count; i++) {
	    ShortInt.Data ci = MPI.shortInt.getData(in, i);
	    if (ci.getValue() != 0 || ci.getIndex() != i) {  
	      OmpitestError.ompitestError(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "root " + root +
					  ", bad answer (in[" + i +
					  " of " + count + "] = (" +
					  ci.getValue() + ", " + ci.getIndex() +
					  "). Should be (0, " + i +
					  ")\n");
	    }
	  }
	}
      }
    }
    
    MPI.COMM_WORLD.barrier();
  }

  
  private static void check_li(int rank, int size) throws MPIException
  {
    //    struct li {
    //      long l;
    //      int i;
    //    } *in, *out;
    //    in = malloc(sizeof(struct li) * MAXLEN);
    //    out = malloc(sizeof(struct li) * MAXLEN);
    ByteBuffer in  = MPI.newByteBuffer(MPI.LONG_INT.getExtent() * MAXLEN),
	       out = MPI.newByteBuffer(MPI.LONG_INT.getExtent() * MAXLEN);
    if (DB_TALK) {
      System.out.printf("Sizeof struct_long_int: %d, " +
			"sizeof long: %d, sizeof int: %d\n",
			MPI.LONG_INT.getExtent(), SIZEOF_LONG, SIZEOF_INT);
    }

    for (int i = 0; i < MAXLEN; ++i) {
      //      memset(&(out[i]), 17, sizeof(out[i]));
      LongInt.Data co = MPI.longInt.getData(out, i);
      co.putValue(17);
      co.putIndex(17);
    }

    MPI.COMM_WORLD.barrier();
    for (int count = 2000; count <= MAXLEN; count *= 10)  {
      for (int root = 0; root < size; ++root) {
	for (int i = 0; i < count; i++) {
	  LongInt.Data co = MPI.longInt.getData(out, i);
	  LongInt.Data ci = MPI.longInt.getData(in, i);
	  //	  out[i].l = rank;
	  //	  out[i].i = i + rank;
	  //	  in[i].l = 1111;
	  //	  in[i].i = 3333;
	  co.putValue(rank);
	  co.putIndex(i + rank);
	  ci.putValue(1111);
	  ci.putIndex(3333);

	}
	
	if (DB_TALK) {
	  if (rank == root) {
	    System.out.printf("Reducing root %d size %d\n",
			      root, count);
	  }
	}

	MPI.COMM_WORLD.reduce(out, in, count, MPI.LONG_INT, MPI.MINLOC, root);
	
	if (root == rank) {
	  for (int i = 0; i < count; i++) {
	    LongInt.Data ci = MPI.longInt.getData(in, i);
	    if (ci.getValue() != 0 || ci.getIndex() != i) {  
	      OmpitestError.ompitestError(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "root " + root +
					  ", bad answer (in[" + i +
					  " of " + count + "] = (" +
					  ci.getValue() + ", " + ci.getIndex() +
					  "). Should be (0, " + i +
					  ")\n");
	    }
	  }
	}
	MPI.COMM_WORLD.barrier();
      }
    }
    MPI.COMM_WORLD.barrier();
  }
}