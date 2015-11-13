/* 
 *
 * This file is a port from "ticket-1984-littlehang.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of
 * the code is mainly the same as in the original file.
 *
 *
 * File: Ticket_1984_Littlehang.java	Author: S. Gross
 *
 */

import java.util.*;
import mpi.*;

public class Ticket_1984_Littlehang
{
  public static void main (String args[]) throws MPIException
  {
    //MPI_Aint extent;
    int rank, np, peer, extent;
    int tag = 51;
    char irbuffer[] = new char[262144];
    char isbuffer[] = new char[262144];
    int packed = 0;
    Status status;
    Datatype newtype;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    np = MPI.COMM_WORLD.getSize();

    Arrays.fill(irbuffer, (char) 0);
    Arrays.fill(isbuffer, (char) 0);
    init_smsg(isbuffer, (73*1536));
    
    if (0 == rank) {
      for (int i = 0; i < 2; ++i) {
	peer = 1;

	newtype = Datatype.createVector(73, 1536, 1536, MPI.CHAR);
	newtype.commit();
	extent = newtype.getExtent();
	System.out.printf("R%d:%d : extent = %d\n",
			  rank, i, extent);
	
	MPI.COMM_WORLD.send (isbuffer, 1, newtype, peer, tag);
	
	System.out.printf("R%d:%d : Send Complete\n", rank, i);	
	newtype.free();
      }
    } else if (1 == rank) {
      for (int i = 0; i < 2; ++i) {
	peer = 0;
	int ridx;
	int sidx;	
	int fail = 0;
	
	if (1 == i) {
	  /* original which causes hang */
	  System.out.printf("R%d:%d : EXPEXT HANG\n", rank, i);
	  newtype = Datatype.createVector(73, 1536, 1552, MPI.CHAR);
	  
	} else {
	  /* modified fix */
	  newtype = Datatype.createVector(73, 1536, 1536, MPI.CHAR);
	}
	
	newtype.commit();
	extent = newtype.getExtent();
	System.out.printf("R%d:%d : extent = %d\n",
			  rank, i, extent);
	
	status = MPI.COMM_WORLD.recv(irbuffer, 1, newtype, peer, tag);
	
	for (int j = 0; j < 73; j++) {
	  sidx = j * 1536;
	  
	  if (1 == i) {
	    ridx = j * 1552;
	  } else {
	    ridx = j * 1536;		
	  }

	  for (int k = 0; k < 1536; ++k) {
	    if (irbuffer[ridx + k] != isbuffer[sidx + k]) {
	      fail = 1;
	    }
	  }
	}
	
	if (fail != 0) {
	  System.out.printf("R%d:%d : Recv Corrupt : FAIL\n", rank, i);
	} else {
	  System.out.printf("R%d:%d : Recv Complete : PASS\n", rank, i);
	}
	newtype.free();
      }
    }
    
    MPI.COMM_WORLD.barrier();
    System.out.printf("R%d : COMPLETE\n", rank);
    
    MPI.Finalize();
  }
  
  
  private static void init_smsg(char b[], int c)
  {
    char charString64[] = {'a','b','c','d','e','f','g',
			   'h','i','j','k','l','m','n',
			   'o','p','q','r','s','t','u',
			   'v','w','x','y','z','A','B',
			   'C','D','E','F','G','H','I',
			   'J','K','L','M','N','O','P',
			   'Q','R','S','T','U','V','W',
			   'X','Y','Z','-','f','i','l',
			   'l','h','e','r','u','p','!',
			   '-'};

    if (c <= charString64.length ) {
      System.arraycopy(b, 0, charString64, 0, c);
    } else {
      int fill_idx, b_idx;
      
      fill_idx = 0;
      for (b_idx = 0; b_idx < c; b_idx++) {
	b[b_idx] = charString64[fill_idx];
	fill_idx =
	  ((fill_idx == (charString64.length - 1)) ? 0 : ++fill_idx);
      }
    }
  }
}
