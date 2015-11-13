/* Needs at least MPI 2.2.
 *
 * This file is a port from "distgraph1.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Distgraph1.java		Author: S. Gross
 *
 */

import java.util.*;
import mpi.*;

public class Distgraph1
{
  private final static int NUM_GRAPHS = 10;
  private final static int MAX_WEIGHT = 100;
  private final static int MAX_LAYOUT_NAME_LENGTH = 256;
  private final static boolean DEBUG = true;

  private static int size, rank;
  /* Maybe use a bit vector instead? */
  private static int layout[][];
  private static String graph_layout_name = "";


  public static void main (String args[]) throws MPIException
  {
    int errs = 0;
    int k, p;
    int indegree, outdegree;
    int sources[], sweights[], destinations[], dweights[], degrees[];
    GraphComm comm;
    
    MPI.Init(args);
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();

    if (OmpitestConfig.HAVE_DIST_GRAPH != 0) {
      layout = new int[size][size];
      /* alloc size*size ints to handle the all-on-one-process case */
      sources = new int[size * size];
      sweights = new int[size * size];
      destinations = new int[size * size];
      dweights = new int[size * size];
      degrees = new int[size * size];

      for (int i = 0; i < NUM_GRAPHS; i++) {
	create_graph_layout(i);
	if (rank == 0 && DEBUG) {
	  System.out.printf("using graph layout '%s'\n",
			    graph_layout_name);
	}
	
	/* MPI_Dist_graph_create_adjacent */
	if (rank == 0 && DEBUG) {
	  System.out.printf("testing MPI_Dist_graph_create_adjacent\n");
	}
	indegree = 0;
	k = 0;
	for (int j = 0; j < size; j++) {
	  if (layout[j][rank] != 0) {
	    indegree++;
	    sources[k] = j;
	    sweights[k++] = layout[j][rank];
	  }
	}
	
	outdegree = 0;
	k = 0;
	for (int j = 0; j < size; j++) {
	  if (layout[rank][j] != 0) {
	    outdegree++;
	    destinations[k] = j;
	    dweights[k++] = layout[rank][j];
	  }
	}

	for (int reorder = 0; reorder <= 1; reorder++) {
	  /* Don't know how to do "getpid()" in Java and don't see,
	   * what you can do with the following lines of code.
	   *
	   *	  {
	   *	    static int pause = 3;
	   *	    if (pause == 0) {
	   *	      System.out.printf("pid %d ready to attach\n",
	   *				getpid());
	   *	      while (pause == 0) {
	   *		Thread.sleep(5000);
	   *	      }
	   *	    }
	   *	  }
	   */

          comm = MPI.COMM_WORLD.createDistGraphAdjacent(
                  Arrays.copyOf(sources, indegree), sweights,
                  Arrays.copyOf(destinations, outdegree), dweights,
                  MPI.INFO_NULL, reorder != 0);

          comm.barrier();
	  verify_comm(comm);
	  comm.free();
	}
	
	
	/* MPI_Dist_graph_create() where each process specifies its
	 * outgoing edges
	 */
	if (rank == 0 && DEBUG) {
	  System.out.printf("testing MPI_Dist_graph_create w/ " +
			    "outgoing only\n");
	}
	sources[0] = rank;
	k = 0;
	for (int j = 0; j < size; j++) {
	  if (layout[rank][j] != 0) {
	    destinations[k] = j;
	    dweights[k++] = layout[rank][j];
	  }
	}
	degrees[0] = k;
	for (int reorder = 0; reorder <= 1; reorder++) {
          comm = MPI.COMM_WORLD.createDistGraph(
                  Arrays.copyOf(sources, 1), degrees,
                  destinations, dweights, MPI.INFO_NULL, reorder != 0);

          comm.barrier();
	  verify_comm(comm);
	  comm.free();
	}
	
	
	/* MPI_Dist_graph_create() where each process specifies its
	 * incoming edges
	 */
	if (rank == 0 && DEBUG) {
	  System.out.printf("testing MPI_Dist_graph_create w/ " +
			    "incoming only\n");
	}
	k = 0;
	for (int j = 0; j < size; j++) {
	  if (layout[j][rank] != 0) {
	    sources[k] = j;
	    sweights[k] = layout[j][rank];
	    degrees[k] = 1;
	    destinations[k++] = rank;
	  }
	}
	for (int reorder = 0; reorder <= 1; reorder++) {
          comm = MPI.COMM_WORLD.createDistGraph(
                  Arrays.copyOf(sources, k), degrees,
                  destinations, sweights, MPI.INFO_NULL, reorder != 0);

          comm.barrier();
	  verify_comm(comm);
	  comm.free();
	}
	
	
	/* MPI_Dist_graph_create() where rank 0 specifies the entire
	 * graph
	 */
	if (rank == 0 && DEBUG) {
	  System.out.printf("testing MPI_Dist_graph_create w/ " +
			    "rank 0 specifies only\n");
	}
	p = 0;
	for (int j = 0; j < size; j++) {
	  for (k = 0; k < size; k++) {
	    if (layout[j][k] != 0) {
	      sources[p] = j;
	      sweights[p] = layout[j][k];
	      degrees[p] = 1;
	      destinations[p++] = k;
	    }
	  }
	}
	for (int reorder = 0; reorder <= 1; reorder++) {
          comm = MPI.COMM_WORLD.createDistGraph(
                  Arrays.copyOf(sources, (rank == 0) ? p : 0),
                  degrees, destinations, sweights, MPI.INFO_NULL, reorder != 0);

          comm.barrier();
	  verify_comm(comm);
	  comm.free();
	}
	
	
	/* MPI_Dist_graph_create() with no graph */
	if (rank == 0 && DEBUG) {
	  System.out.printf("testing MPI_Dist_graph_create w/ " +
			    "no graph\n");
	}
	for (int reorder = 0; reorder <= 1; reorder++) {
          comm = MPI.COMM_WORLD.createDistGraph(
                  Arrays.copyOf(sources, 0), degrees,
                  destinations, sweights, MPI.INFO_NULL, reorder != 0);
	  /* intentionally no verification */
	  comm.free();
	}
      }
    }
    
    MPI.Finalize();
    
    if (OmpitestConfig.HAVE_DIST_GRAPH != 0) {
      System.exit(0);
    } else {
      /* Indicate "skip" if we don't have MPI_DIST_GRAPH */
      System.exit(77);
    }
  }
  
  
  
  private static void create_graph_layout(int graph_num) throws MPIException
  {
    /* We need MPI 2.2 to be able to compile the following routines. */
    if (OmpitestConfig.HAVE_DIST_GRAPH != 0) {
      byte graphLayoutName[] = new byte[MAX_LAYOUT_NAME_LENGTH];

      if (rank == 0) {
	switch (graph_num) {
	case 0:
	  graph_layout_name = "deterministic complete graph";
	  for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
	      layout[i][j] = (i + 2) * (j + 1);
	  break;
	case 1:
	  graph_layout_name = "every other edge deleted";
	  for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
	      layout[i][j] = ((j % 2) != 0 ? (i + 2) * (j + 1) : 0);
	  break;
	case 2:
	  graph_layout_name = "only self-edges";
	  for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
	      if (i == rank && j == rank)
		layout[i][j] = 10 * (i + 1);
	      else
		layout[i][j] = 0;
	    }
	  }
	  break;
	case 3:
	  graph_layout_name = "no edges";
	  for (int i = 0; i < size; i++)
	    for (int j = 0; j < size; j++)
	      layout[i][j] = 0;
	  break;
	default:
	  /* srand(graph_num); */
	  Random rand = new Random(graph_num);
	  graph_layout_name = "a random incomplete graph";
	  
	  /* Create a connectivity graph; layout[i,j]==w represents an
	   * outward connectivity from i to j with weight w, w==0 is
	   * no edge.
	   */
	  for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
	      /* disable about a third of the edges */
	      /* if (((rand() * 1.0) / RAND_MAX) < 0.33) */
	      if (rand.nextFloat() < 0.33)
		layout[i][j] = 0;
	      else
		/* layout[i][j] = rand() % MAX_WEIGHT; */
		layout[i][j] = rand.nextInt(MAX_WEIGHT);
	    }
	  }
	  break;
	}
	graphLayoutName = graph_layout_name.getBytes();
      }

      /* because of the randomization we must determine the graph on
       * rank 0 and send the layout to all other processes
       */
      assert graphLayoutName.length <= MAX_LAYOUT_NAME_LENGTH;
      MPI.COMM_WORLD.bcast(graphLayoutName, graphLayoutName.length,
			   MPI.BYTE, 0);
      graph_layout_name = new String (graphLayoutName, 0,
				      graphLayoutName.length);
      for (int i = 0; i < size; ++i) {
        MPI.COMM_WORLD.bcast(layout[i], size, MPI.INT, 0);
      }
    }  /* At least MPI 2.2 */
  }



  private static void verify_comm(GraphComm comm) throws MPIException
  {
    /* We need MPI 2.2 to be able to compile the following routines. */
    if (OmpitestConfig.HAVE_DIST_GRAPH != 0) {
      int j;
      GraphComm dupcomm = null;
      
      for (int use_dup = 0; use_dup <= 1; ++use_dup) {
        /*
	if (use_dup == 0) {
	  MPI_Dist_graph_neighbors_count(comm, &indegree,
					 &outdegree, &weighted);
	}
	else {
	  dupcomm = comm.clone();
	  comm = dupcomm; // caller retains original comm value
	}
        */
        if(use_dup > 0)
        {
	  dupcomm = comm.clone();
	  comm = dupcomm; // caller retains original comm value
        }

	int topo_type = comm.getTopology();
	if (topo_type != MPI.DIST_GRAPH) {
	  System.err.printf("topo_type != MPI_DIST_GRAPH\n");
	}
	
        DistGraphNeighbors n = comm.getDistGraphNeighbors();

        j = 0;
	for (int i = 0; i < size; i++) {
	  if (layout[i][rank] != 0) {
	    j++;
	  }
	}
	if (j != n.getInDegree()) {
	  System.err.printf("indegree does not match, expected =" +
			    n.getInDegree() + " got = " + j +
			    ", layout = '" + graph_layout_name + "'\n");
	}
	
	j = 0;
	for (int i = 0; i < size; i++) {
	  if (layout[rank][i] != 0) {
	    j++;
	  }
	}
	if (j != n.getOutDegree()) {
	  System.err.printf("outdegree does not match, expected = " +
			    n.getOutDegree() + " got = " + j +
			    ", layout = '" + graph_layout_name + "'\n");
	}
	
	if (((n.getInDegree() != 0) ||
            (n.getOutDegree() != 0)) && !n.isWeighted()) {
	  System.err.printf("MPI_Dist_graph_neighbors_count thinks " +
			    "the graph is not weighted\n");
	}
	
	/* For each incoming and outgoing edge in the matrix, search
	 * if the query function listed it in the sources.
	 */
	for (int i = 0; i < size; i++) {
	  if (layout[i][rank] != 0) {
	    for (j = 0; j < n.getInDegree(); j++) {
	      assert n.getSource(j) >= 0;
	      assert n.getSource(j) < size;
	      if (n.getSource(j) == i) {
		break;
	      }
	    }
	    if (j == n.getInDegree()) {
	      System.err.printf("no edge from " + i + " to " +
				rank + " specified\n");
	    }
	    else {
	      if (n.getSourceWeight(j) != layout[i][rank]) {
		System.err.printf("incorrect weight for edge (" +
				  i + ", " + rank + "): " +
				  n.getSourceWeight(j) + " instead of " +
                                  layout[i][rank] + "\n");
	      }
	    }
	  }
	  if (layout[rank][i] != 0) {
	    for (j = 0; j < n.getOutDegree(); j++) {
	      assert n.getDestination(j) >= 0;
	      assert n.getDestination(j) < size;
	      if (n.getDestination(j) == i) {
		break;
	      }
	    }
	    if (j == n.getOutDegree()) {
	      System.err.printf("no edge from " + rank + " to " +
				i + " specified\n");
	    }
	    else {
	      if (n.getDestinationWeight(j) != layout[rank][i]) {
		System.err.printf("incorrect weight for edge (" +
				  rank + ", " + i + "): " +
				  n.getDestinationWeight(j) + " instead of " +
                                  layout[rank][i] + "\n");
	      }
	    }
	  }
	}
	
	/* For each incoming and outgoing edge in the sources, we
	 * should have an entry in the matrix
	 */
	for (int i = 0; i < n.getInDegree(); i++) {
	  if (layout[n.getSource(i)][rank] != n.getSourceWeight(i)) {
	    System.err.printf("edge (" + i + ", " + rank +
			      ") has a weight " + n.getSourceWeight(i) +
			      " instead of " +
			      layout[n.getSource(i)][rank] + "\n");
	  }
	}
	for (int i = 0; i < n.getOutDegree(); i++) {
	  if (layout[rank][n.getDestination(i)] != n.getDestinationWeight(i)) {
	    System.err.printf("edge (" + rank + ", " + i +
			      ") has a weight " + n.getDestinationWeight(i) +
			      " instead of " +
			      layout[rank][n.getDestination(i)] + "\n");
	  }
	}
      }
      
      if (dupcomm != null && !dupcomm.isNull()) {
	dupcomm.free();
      }
    }
  } /* At least MPI 2.2 */
}
