/* 
 *
 * This file is a port from "graph.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Graph.java			Author: S. Gross
 *
 */

import java.util.*;
import mpi.*;

public class Graph
{
  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    GraphComm comm;
    Comm only4;
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();

    /* We need at least 4 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    4, true);
    
    /* For these two tests, make a subcomm of size 4 and only have the
     * procs in that subcomm run the tests
     */
    only4 = MPI.COMM_WORLD.split((me < 4 ? 1 : 0), 0);
    if (me < 4) {
      graph_test1(only4);
      if (((MPI.VERSION == 2) && (MPI.SUBVERSION >= 1)) ||
	  (MPI.VERSION > 2)) {
	/* At least MPI-2.1 is necessary for the following test */
	graph_test2(only4);
      }
    }
    MPI.COMM_WORLD.barrier();
    only4.free();
    
    if (((MPI.VERSION == 2) && (MPI.SUBVERSION >= 1)) ||
	(MPI.VERSION > 2)) {
      /* At least MPI-2.1 is necessary for the following test */
      comm = MPI.COMM_WORLD.createGraph(new int[0], new int[0], false);
      if (!comm.isNull()) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Graph_create: should " +
				    "get MPI_COMM_NULL back when " +
				    "nnodes==0\n");
      }
    }
    
    MPI.Finalize();
  }
  
  
  
  static void graph_test1(Comm input_comm) throws MPIException
  {
    int index[] = new int[]{ 2, 3, 4, 6 }, 
	edges[] = new int[]{ 1, 3, 0, 3, 0, 2},
        index_out[] = new int[index.length],
	edges_out[] = new int[edges.length],
	neighbors[];
    int me, num, start, type, nnodes, nedges;
    boolean reorder;
    GraphParms graphTopo;
    GraphComm comm;

    reorder = false;
    me = input_comm.getRank();
    comm = ((Intracomm) input_comm).createGraph(index, edges, reorder);
    
    type = comm.getTopology();
    if (type != MPI.GRAPH) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Topo_test, type = " +
				  type + ", should be " + MPI.GRAPH +
				  " (GRAPH)\n");
    }

    /* Java uses Graphcomm.getDims() for MPI_Graphdims_get() and
     * MPI_Graph_get()
     *
     * First call MPI_Graphdims_get(comm, &nnodes, &nedges);
     */
    graphTopo = comm.getDims();
    nnodes = graphTopo.getIndexCount();
    nedges = graphTopo.getEdgeCount();
    if (nnodes != index.length || nedges != edges.length) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graphdims_get, " +
				  "(nnodes,nedges) = (" + nnodes + "," +
				  nedges + "), should be (4,6)\n");
    }

    /* Second call to 
     * MPI_Graph_get(comm, max_index, max_edges, index_out, edges_out);
     * which isn't necessary, because "getDims()" determined all
     * values.
     */
    for (int i = 0; i < index_out.length; ++i) {
      index_out[i] = graphTopo.getIndex(i);
    }
    for (int i = 0; i < edges_out.length; ++i) {
      edges_out[i] = graphTopo.getEdge(i);
    }
    if (!Arrays.equals(index, index_out)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_get: got wrong " +
				  "values for index array\n");
    }
    if (!Arrays.equals(edges, edges_out)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_get: got wrong " +
				  "values for edges array\n");
    }

    /* Java uses Graphcomm.getNeighbors() for MPI_Graph_neighbors_count()
     * and MPI_Graph_neighbors()
     *
     * First call MPI_Graph_neighbors_count(comm, me, &nnodes);
     */
    neighbors = comm.getNeighbors(me);
    nnodes = neighbors.length;
    if (nnodes != 1 + (me == 0 || me == 3 ? 1 : 0)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_neighbors_count: " +
				  "count = " + nnodes + ", should be " +
				  (1 + ((me == 0) || (me == 3) ? 1 : 0)) +
				  "\n");
    }
    
    /* Second call to MPI_Graph_neighbors(comm, me, 4, neighbors);
     * which isn't necessary, because "getNeighbors()" determined all
     * values.
     */
    start = (me == 0) ? 0 : index[me - 1];
    num = (me == 0) ? index[0] : index[me] - start;
    for (int i = 0; i < num; i++) {
      if (neighbors[i] != edges[start + i])
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Graph_neighbors: " +
				    "wrong neighbor on task " + me +
				    " index " + i + ", neighbor = " +
				    neighbors[i] + ", should be " +
				    edges[start + i] + "\n");
    }
    
    comm.barrier();
    comm.free();
  }
  
  
  static void graph_test2(Comm input_comm) throws MPIException
  {
    /* Asymmetric graph with some loopback connections.  Graph looks
     * like this:
     * 
     * |---|
     * 0 --------->  1 <-|
     * |   |      __/^  |
     * |   |   __/   |  |
     * \|/ \|/</      | \|/
     * |-> 2 <---------- 3
     * |---|
     */
    int index[] = new int[]{ 3, 6, 7, 9 },
	edges[] = new int[]{ 1, 2, 2,
			     1, 2, 3, 
			     2,
			     1, 2 },
        index_out[] = new int[index.length],
	edges_out[] = new int[edges.length],
	neighbors[];
    int me, expected, num, start, type, nnodes, nedges;
    boolean reorder;
    GraphParms graphTopo;
    GraphComm comm;

    reorder = false;
    me = input_comm.getRank();
    /* Make a whacky graph comm (see above graph) */
    comm = ((Intracomm) input_comm).createGraph(index, edges, reorder);
    
    /* Should have a graph topo */
    type = comm.getTopology();
    if (type != MPI.GRAPH) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Topo_test, type = " +
				  type + ", should be " + MPI.GRAPH +
				  " (GRAPH)\n");
    }
    
    /* Compare output from MPI_Graphdims_get with the size we used in
     * MPI_Graph_create
     *
     * Java uses Graphcomm.getDims() for MPI_Graphdims_get() and
     * MPI_Graph_get()
     *
     * First call MPI_Graphdims_get(comm, &nnodes, &nedges);
     */
    graphTopo = comm.getDims();
    nnodes = graphTopo.getIndexCount();
    nedges = graphTopo.getEdgeCount();
    if (nnodes != index.length || nedges != edges.length) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graphdims_get, " +
				  "(nnodes,nedges) = (" + nnodes + "," +
				  nedges + "), should be (4,9)\n");
    }
    
    /* Second call to 
     * MPI_Graph_get(comm, max_index, max_edges, index_out, edges_out);
     * which isn't necessary, because "getDims()" determined all
     * values.
     *
     * Compare output from MPI_Graph_get with the args we used in
     * MPI_Graph_create
     */
    for (int i = 0; i < index_out.length; ++i) {
      index_out[i] = graphTopo.getIndex(i);
    }
    for (int i = 0; i < edges_out.length; ++i) {
      edges_out[i] = graphTopo.getEdge(i);
    }
    if (!Arrays.equals(index, index_out)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_get: got wrong " +
				  "values for index array\n");
    }
    if (!Arrays.equals(edges, edges_out)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_get: got wrong " +
				  "values for edges array\n");
    }
    
    /* Each node should have a specific number of neighbors (the
     * outdegree of that node in the graph above):
     * 
     * 0: 3 neighbors
     * 1: 3 neighbors
     * 2: 1 neighbor
     * 3: 2 neightbors
     *
     * Java uses Graphcomm.getNeighbors() for MPI_Graph_neighbors_count()
     * and MPI_Graph_neighbors()
     *
     * First call MPI_Graph_neighbors_count(comm, me, &nnodes);
     */
    neighbors = comm.getNeighbors(me);
    nnodes = neighbors.length;
    switch(me) {
    case 0: expected = 3; break;
    case 1: expected = 3; break;
    case 2: expected = 1; break;
    case 3: expected = 2; break;
    default: expected = -1; break;
    }
    if (nnodes != expected) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "ERROR in MPI_Graph_neighbors_count: " +
				  "count = " + nnodes + ", should be " +
				  expected + "\n");
    }
    
    /* Second call to MPI_Graph_neighbors(comm, me, 4, neighbors);
     * which isn't necessary, because "getNeighbors()" determined all
     * values.
     *
     * Check that the neighbors listed in MPI_Graph_neighbors match
     * that what we gave in MPI_Graph_create
     */
    start = (me == 0) ? 0 : index[me - 1];
    num = (me == 0) ? index[0] : index[me] - start;
    for (int i = 0; i < num; i++) {
      if (neighbors[i] != edges[start + i])
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "ERROR in MPI_Graph_neighbors: " +
				    "wrong neighbor on task " + me +
				    " index " + i + ", neighbor = " +
				    neighbors[i] + ", should be " +
				    edges[start + i] + "\n");
    }
    
    comm.barrier();
    comm.free();
  }
}
