/*
 *
 * This file is a test for the getName and setName methods in Win.java.
 *
 *
 * File: WinName.java			Author: N. Graham
 *
 */

import java.nio.IntBuffer;
import mpi.*;

public class WinName {

	public static void main(String[] args) throws MPIException {
		String testName = "testName";
		MPI.Init(args);
		
		int rank = MPI.COMM_WORLD.getRank();
	    IntBuffer winArea = MPI.newIntBuffer(1);

	    Win win = new Win(winArea, 1, 1, MPI.INFO_NULL, MPI.COMM_WORLD);
	    
	    win.setName(testName);
	    
	    if(win.getName().equals(testName)) {
	    	if(rank == 0) {
	    		System.out.println("Test Passed");
	    	}
	    } else {
	    	if(rank == 0) {
	    		System.out.println("Test Failed");
	    	}
	    }
	}

}
