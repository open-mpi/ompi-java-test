import mpi.*;

public class ReduceScatterBlockInPlace
{
  private final static int MAXLEN = 1000;

  public static void main (String args[]) throws MPIException
  {
    int myself,tasks;
    int inout[], recvcount;
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();
    
    inout = new int[MAXLEN * tasks];
    for (int j = 1; j <= MAXLEN;  j *= 10)  {
      recvcount = j;
      
      for (int i = 0; i < j * tasks; i++) {
	inout[i] = i;
      }
      
      MPI.COMM_WORLD.reduceScatterBlock(inout, recvcount, MPI.INT, MPI.SUM);
      
      for (int k = 0; k < j; k++) {
	if (inout[k] != tasks * (myself * j + k)) {  
	  OmpitestError.ompitestError(OmpitestError.getFileName(),
				      OmpitestError.getLineNumber(),
				      " bad answer (" + inout[k] +
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
