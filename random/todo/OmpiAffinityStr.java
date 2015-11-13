/* 
 *
 * This file is a port from "ompi-affinity-str.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: OmpiAffinityStr.java		Author: S. Gross
 *
 */

import mpi.*;

public class OmpiAffinityStr
{
  public static void main (String args[]) throws MPIException
  {
    if (OmpitestConfig.OMPI_HAVE_MPI_EXT_AFFINITY == 1) {
      int rank;
      char ompi_bound[OMPI_AFFINITY_STRING_MAX];
      char current_binding[OMPI_AFFINITY_STRING_MAX];
      char exists[OMPI_AFFINITY_STRING_MAX];
      
      MPI.Init(args);
      rank = MPI.COMM_WORLD.getRank();
      
      OMPI_Affinity_str(OMPI_AFFINITY_RSRC_STRING_FMT,
			ompi_bound, current_binding, exists);
      System.out.printf("rank %d (resource string): \n" +
			"       ompi_bound: %s\n" +
			"  current_binding: %s\n" +
			"           exists: %s\n",
			rank, ompi_bound, current_binding, exists);
      
      OMPI_Affinity_str(OMPI_AFFINITY_LAYOUT_FMT,
			ompi_bound, current_binding, exists);
      System.out.printf("rank %d (layout): \n" +
			"       ompi_bound: %s\n" +
			"  current_binding: %s\n" +
			"           exists: %s\n",
			rank, ompi_bound, current_binding, exists);
      MPI.Finalize();
      System.exit(0);
    } else {
      /* The "affinity" extension is not available */
      System.exit(77);
    }
  }
}
