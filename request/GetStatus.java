/* 
 *
 * This file is a modified version of Test1.java which has been
 * repurposed to test the java binding of MPI_REQUEST_GET_STATUS.
 *
 *
 * File: GetStatus.java			Author: N. Graham
 *
 */

import java.nio.*;
import mpi.*;

public class GetStatus
{
	public static void main (String args[]) throws MPIException
	{
		int me;
		int outmsg[] = new int[1];
		IntBuffer inmsg = MPI.newIntBuffer(1);
		boolean flag = false;
		Request msgid;
		MPI.Init(args);

		me = MPI.COMM_WORLD.getRank();
		inmsg.put(0, -1);

		/* We need at least 2 to run */
		OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				OmpitestError.getLineNumber(),
				2, true);

		if (me == 1) {
			outmsg[0] = 5;
			MPI.COMM_WORLD.send (outmsg, 1, MPI.INT, 0, 1);
		}
		if (me == 0) {
			msgid = MPI.COMM_WORLD.iRecv(inmsg, 1, MPI.INT,
					MPI.ANY_SOURCE, MPI.ANY_TAG);
			Status status = null;

			while(status == null)
				status = msgid.getStatus();
			
			msgid.free();
			
			if(inmsg.get(0) != 5 || status.getSource() != 1 || status.getTag() != 1)
				OmpitestError.ompitestError(OmpitestError.getFileName(),
						OmpitestError.getLineNumber(),
						"flag, inmsg, src, tag = \"" +
								flag + ", " + inmsg.get(0) + ", " +
								status.getSource() + ", " +
								status.getTag() + 
						"\", should be \"true, 5, 1, 1\"\n");
		}
		MPI.COMM_WORLD.barrier();
		
		if(me == 0) {
			System.out.println("Test Passed");
		}
		
		MPI.Finalize();
	}
}
