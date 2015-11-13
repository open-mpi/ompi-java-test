/* 
 *
 * This file is a port from "clientserver.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: ClientServer.java		Author: S. Gross
 *
 */

import mpi.*;

public class ClientServer
{
  public static void main (String args[]) throws MPIException
  {
    int size, rank;
    int buf[] = new int[1];
    String serviceName = "MyService";

    MPI.Init(args);

    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);

    buf[0] = 1;
    rank = MPI.COMM_WORLD.getRank();
    size = MPI.COMM_WORLD.getSize();

    if (rank == 0) {
      // Server code
      String portName = Intracomm.openPort();

      Intracomm.publishName(serviceName, portName);
      MPI.COMM_WORLD.barrier();

      /*
      // send port length and port name to client (rank 1)
      int  portLength[]  = new int[1];
      char portNameBuf[] = new char[MPI.MAX_PORT_NAME];
      portName.getChars(0, portName.length(), portNameBuf, 0);
      portLength[0] = portName.length();
      MPI.COMM_WORLD.send(portLength, 0, 1, MPI.INT, 1, 1);
      MPI.COMM_WORLD.send(portNameBuf, 0, portLength[0], MPI.CHAR, 1, 1);
      */

      Comm client = MPI.COMM_SELF.accept(portName, 0);
      client.recv(buf, 1, MPI.INT, 0, 1);
      client.disconnect();

      Intracomm.closePort(portName);
      Intracomm.unpublishName(serviceName, portName);
    }
    else if (rank == 1) {
      // Client code
      MPI.COMM_WORLD.barrier();
      String portName = Intracomm.lookupName(serviceName);

      /*
      // receive port length and port name from server (rank 0)
      int  portLength[]  = new int[1];
      char portNameBuf[] = new char[MPI.MAX_PORT_NAME];
      MPI.COMM_WORLD.recv(portLength, 0, 1, MPI.INT, 0, 1);
      MPI.COMM_WORLD.recv(portNameBuf, 0, portLength[0], MPI.CHAR, 0, 1);
      String portName = new String(portNameBuf, 0, portLength[0]);
      */

      // connect to the server
      Comm server = MPI.COMM_SELF.connect(portName, 0);
      server.send(buf, 1, MPI.INT, 0, 1);
      server.disconnect();
    }

    MPI.Finalize();
  }
}
