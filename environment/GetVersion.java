/*
 *
 * This file is a test for the getVersion method in MPI.java.
 *
 *
 * File: GetVersion.java			Author: N. Graham
 *
 */

import mpi.*;

public class GetVersion {

	public static void main(String[] args) {
		Version vers = MPI.getVersion();

		if(vers != null) {
			System.out.println("VERSION: " + vers.getVersion());
			System.out.println("SUBVERSION: " + vers.getSubVersion());
		} else {
			System.out.println("Test Failed");
		}
	}

}
