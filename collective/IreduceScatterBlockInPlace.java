import java.nio.*;
import mpi.*;

public class IreduceScatterBlockInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks, recvcount;
    Request request;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    IntBuffer inout = MPI.newIntBuffer(MAXLEN * tasks);

    for (int j = 1; j <= MAXLEN;  j *= 10)  {
      recvcount = j;
      
      for (int i = 0; i < j * tasks; i++) {
	inout.put(i, i);
      }
      
      request = MPI.COMM_WORLD.iReduceScatterBlock(inout, recvcount,
                                                   MPI.INT, MPI.SUM);
      request.waitFor();
      request.free();
      
      for (int k = 0; k < j; k++) {
	if (inout.get(k) != tasks * (myself * j + k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + inout.get(k) +
				      ") at index " + k + " of " +
				      j + " (should be " +
				      ((myself * j + k) * tasks) + ")\n");
	  break; 
	}
      }
    }
    MPI.COMM_WORLD.barrier();
    MPI.Finalize();
  }
}
