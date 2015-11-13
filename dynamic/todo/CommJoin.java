/* 
 *
 * This file is a port from "comm_join.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: CommJoin.java			Author: S. Gross
 *
 */

import java.io.*;
import java.net.*;
import mpi.*;

public class CommJoin
{
  private final static int SERVER_PORT = 3100;

  public static void main (String args[]) throws MPIException
  {
    int rank;
    int send_data[] = new int[1],
	recv_data[] = new int[1];
    Intercomm intercomm;
    //int server_socket, client_socket, newsd;
    //int host_len, on;
    //char host_name[];
    //struct sockaddr_in client_addr, server_addr;
    //struct hostent* h;
    //socklen_t client_length;
    ServerSocket server_socket;
    Socket client_socket, newsd;
    String host_name = "";
    int host_name_len[] = new int[1];
    char host_name_buffer[];
    
    MPI.Init(args);
    
    /* We need at least 2 to run */
    OmpitestError.ompitestCheckSize(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    2, true);
    
    rank = MPI.COMM_WORLD.getRank();
    send_data[0] = 17;
    
    if (rank == 0) {
      /* server code */
      //      on = 1;
      //      server_socket = socket(AF_INET, SOCK_STREAM, 0);
      //      /* reuse the port address */
      //      setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on));
      //      if (server_socket < 0)
      //	OmpitestError.ompitestError(OmpitestError.getFileName(),
      //				    OmpitestError.getLineNumber(),
      //				    "Error: server failed to create a " +
      //				    "socket\n");
      //      /* bind to a server port */
      //      server_addr.sin_family = AF_INET;
      //      server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
      //      server_addr.sin_port = htons(SERVER_PORT);
      //      if (bind(server_socket, (struct sockaddr*) &server_addr,
      //	       sizeof(server_addr)) < 0) {
      //	close(server_socket);
      //	OmpitestError.ompitestError(OmpitestError.getFileName(),
      //				    OmpitestError.getLineNumber(),
      //				    "Error: server failed to bind a " +
      //				    "socket\n");
      //      }
      //      listen(server_socket, 3);

      try {
	server_socket = new ServerSocket(SERVER_PORT, 3);
	server_socket.setReuseAddress(true);
      }
      catch (IOException ex)
      {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: server failed to create a " +
				    "socket\n");
      }

      try {
	host_name = InetAddress.getLocalHost().getHostName();
      }
      catch (UnknownHostException ex)
      {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: server failed to get " +
      				    "hostname\n");
      }
      host_name_buffer = host_name.toCharArray();
      host_name_len[0] = host_name_buffer.length;

      /* send length of host name to rank 1 */
      MPI.COMM_WORLD.sSend (host_name_len, 1, MPI.INT, 1, 100);
      
      /* send host name to rank 1 */
      MPI.COMM_WORLD.sSend (host_name_buffer, host_name_len[0],
			    MPI.CHAR, 1, 100);
      
      //      client_length = sizeof(client_addr);
      //      newsd = accept(server_socket, (struct sockaddr*) &client_addr, 
      //		     &client_length);
      //      if (newsd < 0) {
      //	close(server_socket);
      //	free(host_name);
      //	OmpitestError.ompitestError(OmpitestError.getFileName(),
      //				    OmpitestError.getLineNumber(),
      //				    "Error: server failed to accept\n");
      //      }

      try {
	newsd = server_socket.accept();
      }
      catch (IOException ex1)
      {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: server failed to accept\n");
	try {
	  server_socket.close();
	}
	catch (IOException ex2)
	{
	}
      }

      /* join with client */
      MPI_Comm_join(newsd, &intercomm);

      try {
	newsd.close();
	server_socket.close();
      }
      catch (IOException ex)
      {
      }

      /* test the newly generated comm */
      rank = intercomm.getRank();
      intercomm.sSend (send_data, 1, MPI.INT, 1, 123);
      intercomm.recv (recv_data, 1, MPI.INT, 0, 321);
      if (send_data[0] != recv_data[0])
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: server check data mismatch\n");
      intercomm.free();
    } else if (rank == 1) {
      /* client code */
      /* get the host name length from rank 0 */
      MPI.COMM_WORLD.recv (host_name_len, 1, MPI.INT, 1, 100);

      /* get the host name from rank 0 */
      host_name_buffer = new char[host_name_len[0]];
      MPI.COMM_WORLD.recv (host_name_buffer, host_name_len[0],
			   MPI.CHAR, 1, 100);
      host_name = new String(host_name_buffer);
      
      //      h = gethostbyname(host_name);
      //      if (h == NULL) {
      //	close(client_socket);
      //	free(host_name);
      //	OmpitestError.ompitestError(OmpitestError.getFileName(),
      //				    OmpitestError.getLineNumber(),
      //				    "ERROR: Client could not " +
      //				    "gethostbyname properly\n");
      //      }
      //      client_socket = socket(AF_INET, SOCK_STREAM, 0);
      //      server_addr.sin_family = h->h_addrtype;
      //      memcpy((char *) &server_addr.sin_addr.s_addr, h->h_addr_list[0],
      //	     h->h_length);
      //      server_addr.sin_port = htons(SERVER_PORT);
      //      newsd = connect(client_socket, (struct sockaddr*) &server_addr, 
      //		      sizeof(server_addr));
      //      if (newsd < 0) {
      //	close(client_socket);
      //	free(host_name);
      //	OmpitestError.ompitestError(OmpitestError.getFileName(),
      //				    OmpitestError.getLineNumber(),
      //				    "ERROR: Client could not " +
      //				    "connect() properly\n");

      try {
	client_socket = new Socket(host_name, SERVER_PORT);
      }
      catch (IOException ex)
      {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: client failed to create a " +
				    "socket\n");
      }

      /* join with server */
      MPI_Comm_join(client_socket, &intercomm);

      try {
	client_socket.close();
      }
      catch (IOException ex)
      {
      }
      
      /* test newly generated comm */
      rank = intercomm.getRank();
      intercomm.recv (recv_data, 1, MPI.INT, 0, 123);
      intercomm.sSend (send_data, 1, MPI.INT, 1, 321);

      if (send_data[0] != recv_data[0])
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Error: client check data mismatch\n");
      intercomm.free();
    }
    
    /* All done */
    MPI.Finalize();
  }
}
