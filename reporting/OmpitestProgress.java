/* Simple routines to print and store errors for the OMPI test suite.
 *
 * This file is a port from "ompitest_progress.c" from the
 * "ompi-ibm-10.0" regression test package. The formatting of the
 * code is mainly the same as in the original file.
 *
 *
 * File: OmpitestProgress.java		Author: S. Gross
 *
 */

import mpi.*;

public class OmpitestProgress
{
  /*
   * Local constants and variables
   */
  final static String prefix = "Progress: [";
  final static String suffix = "]";
  final static char   fill_char = '=';
  final static int width = 76 - prefix.length() - suffix.length();
  static int rank = -1;
  static int max = -1;
  
  
  /*
   * Local methods
   */
  public static void ompitestProgressStart(int argMax)
     throws MPIException
  {
    rank = MPI.COMM_WORLD.getRank();
    if (rank == 0) {
      max = argMax;
      System.out.printf("%s", prefix);
      fill(' ', width);
      System.out.printf("%s", suffix);
    }
  }
  
  
  public static void ompitestProgress(int current)
  {
    float percent;
    int numDone;
    
    if (rank == 0) {
      if (current >= max) {
	numDone = width;
      }  else if (current <= 0) {
	numDone = 0;
      } else {
	percent = ((float) current / max);
	numDone = (int) (percent * width);
      }

      System.out.printf("\r%s", prefix);
      fill(fill_char, numDone);
      fill(' ', width - numDone);
      System.out.printf("%s", suffix);
    }
  }
  
  public static void ompitestProgressEnd()
  {
    if (rank == 0) {
      System.out.printf("\r%s", prefix);
      fill(fill_char, width);
      System.out.printf("%s\n", suffix);
    }
  }  
  
  private static void fill(char c, int len)
  {
    for (int i = 0; i < len; ++i)
      System.out.print(c);
  }
}
