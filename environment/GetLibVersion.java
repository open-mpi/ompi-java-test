/*
 *
 * This file is a test for the getLibVersion method in MPI.java.
 *
 *
 * File: GetLibVersion.java			Author: N. Graham
 *
 */

import mpi.*;

public class GetLibVersion {

	public static void main(String[] args) {
		String libVer = MPI.getLibVersion();
		
		if(libVer != null) {
			System.out.println("LIBRARY VERSION: " + libVer);
		} else {
			System.out.println("Test Failed");
		}
	}

}
