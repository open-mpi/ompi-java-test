import java.nio.*;
import mpi.*;

public class IexscanInPlace
{
  private final static int MAXLEN = 10000;

  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);

    int myself = MPI.COMM_WORLD.getRank(),
        tasks  = MPI.COMM_WORLD.getSize();

    IntBuffer out = MPI.newIntBuffer(MAXLEN),
              in  = MPI.newIntBuffer(MAXLEN);

    for(int j=1;j<=MAXLEN;j*=10)  {
      for(int i=0;i<j;i++) {
	in.put(i, i);
      }
      
      Request request = MPI.COMM_WORLD.iExScan(in, j, MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();
      
      if (myself != 0)
	for(int k=0;k<j;k++) {
	  if(in.get(k) != k*(myself)) {  
	    OmpitestError.ompitestError(OmpitestError.getFileName(),
					OmpitestError.getLineNumber(),
					" bad answer (" + in.get(k) +
					") at index " + k + " of " + j +
					" (should be " + (k * myself) +
					")\n"); 
	    break; 
	  }
	}
      
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
