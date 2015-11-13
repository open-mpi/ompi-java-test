import java.nio.*;
import mpi.*;

public class Ialltoallv
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    int myself = MPI.COMM_WORLD.getRank(),
        tasks  = MPI.COMM_WORLD.getSize();

    int[] sdispls = new int[tasks],
          scounts = new int[tasks],
          rdispls = new int[tasks],
          rcounts = new int[tasks];

    IntBuffer in  = MPI.newIntBuffer(MAXLEN * tasks),
              out = MPI.newIntBuffer(MAXLEN * tasks);

    for(int i = 0; i < MAXLEN * tasks; ++i) {
      out.put(i, myself);
    }

    for(int j = 1; j <= MAXLEN; j *= 10) {
      for(int i = 0; i < tasks; i++) {
        scounts[i] = rcounts[i] = j;
        sdispls[i] = rdispls[i] = i * j;
      }

      Request request = MPI.COMM_WORLD.iAllToAllv(
                        out, scounts, sdispls, MPI.INT,
                        in, rcounts, rdispls, MPI.INT);

      request.waitFor();
      request.free();

      for(int i = 0; i < tasks; ++i)  {
	for(int k = 0; k < j; ++k) {
	  if(in.get(k + i * j) != i) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(k + i * j) +
					") at index " + (k + i * j) +
					" of " + (j * tasks) +
					" (should be " + i + ")\n"); 
	    break; 
	  }
	}
      }
    }

    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
