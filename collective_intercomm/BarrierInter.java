/* 
 *
 * This file is a port from "barrier_inter.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: BarrierInter.java		Author: S. Gross
 *
 */

import mpi.*;

public class BarrierInter
{
  public static void main (String args[]) throws MPIException
  {
    int size, rank, color;
    int  portLength[] = new int[1];
    char portBuffer[];
    Intracomm comm;
    Intercomm intercomm;

    MPI.Init(args);

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    /* We need an even number to run */
    OmpitestError.ompitestNeedEven(OmpitestError.getFileName(),
				   OmpitestError.getLineNumber());

    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();
    /* Even rank gets 1 while odd ranks get 0 value for color */
    color = ((rank % 2) != 0) ? 0 : 1;
    comm = MPI.COMM_WORLD.split(color, rank);
    
    if (color != 0) {
      /* even ranks */
      String portName = Intracomm.openPort();
      portLength[0] = portName.length();
      portBuffer = new char[MPI.MAX_PORT_NAME];
      portName.getChars(0, portName.length(), portBuffer, 0);
      MPI.COMM_WORLD.send(portLength, 1, MPI.INT, rank + 1, 1);
      MPI.COMM_WORLD.send(portBuffer, portLength[0], MPI.CHAR, rank + 1, 1);

      intercomm = comm.accept(portName, 0);
      intercomm.barrier();
      intercomm.disconnect();
      Intracomm.closePort(portName);
    }
    else {
      /* odd ranks */
      MPI.COMM_WORLD.recv(portLength, 1, MPI.INT, rank - 1, 1);

      /* allocate memory for the the port name */
      portBuffer = new char[portLength[0]];
      MPI.COMM_WORLD.recv(portBuffer, portLength[0], MPI.CHAR, rank - 1, 1);
      String portName = new String(portBuffer);

      /* connect to the server */
      intercomm = comm.connect(portName, 0);
      intercomm.barrier();
      intercomm.disconnect();
    }
    comm.free();
    MPI.Finalize();
  }
}
