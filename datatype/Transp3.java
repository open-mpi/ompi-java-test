/* 
 *
 * This file is a port from "transp3.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: Transp3.java			Author: S. Gross
 *
 */

import mpi.*;

public class Transp3
{
  public static void main (String args[]) throws MPIException
  {
    MyMatrix3D imessage = new MyMatrix3D(),
               omessage = new MyMatrix3D();

    omessage.initialize();
    byte[] packbuf = new byte[4000];
    Datatype type_ox, type_oy, type_oz;
    Datatype type_ia, type_ib, type_ic;

    int[]      aob = new int[10],
               aod = new int[10];
    Datatype[] aot = new Datatype[10];

    MPI.Init(args);
    int me = MPI.COMM_WORLD.getRank();
    int numtasks = MPI.COMM_WORLD.getSize();
    
    if ((numtasks != 1) && (me != 0)) { 
      /* Java doesn't have the name of the command in args[0], so that
       * I use the classname for method "main()".
       */
      System.out.printf("Testcase %s uses one task, extraneous task " +
			"#%d exited.\n",
			OmpitestError.getClassName(), me);
      MPI.Finalize();
      System.exit(0);
    }

    int addr1 = 4 * MyMatrix3D.address(1,0,0), // 100
        addr2 = 4 * MyMatrix3D.address(0,1,0), // 10
        addr3 = 4 * MyMatrix3D.address(0,0,1); // 1
    
    for(int i = 0; i < 10; i++) { 
      aot[i] = MPI.INT; 
      aob[i] = 1;
      aod[i] = i * addr3;
    }

    type_oz = Datatype.createStruct(aob, aod, aot);

    for(int i = 0; i < 10; i++) { 
      aot[i] = type_oz; 
      aob[i] = 1;
      aod[i] = i * addr2;
    }

    type_oy = Datatype.createStruct(aob, aod, aot);
    
    for(int i = 0; i < 10; i++) { 
      aot[i] = type_oy; 
      aob[i] = 1;
      aod[i] = i * addr1;
    }

    type_ox = Datatype.createStruct(aob, aod, aot);
    type_ox.commit();

    for(int i = 0; i < 10; i++) { 
      aot[i] = MPI.INT; 
      aob[i] = 1;
      aod[i] = i * addr1;
    }

    type_ic = Datatype.createStruct(aob, aod, aot);
    
    for(int i = 0; i < 10; i++) {
      aot[i] = type_ic; 
      aob[i] = 1;
      aod[i] = i * addr2;
    }

    type_ib = Datatype.createStruct(aob, aod, aot);
    
    for(int i = 0; i < 10; i++) { 
      aot[i] = type_ib; 
      aob[i] = 1;
      aod[i] = i * addr3;
    }

    type_ia = Datatype.createStruct(aob, aod, aot);
    type_ia.commit();

    MPI.COMM_WORLD.pack(omessage.buf(), 1, type_ox, packbuf, 0);
    MPI.COMM_WORLD.unpack(packbuf, 0, imessage.buf(), 1, type_ia);
    omessage.testTransp(imessage);

    type_oz.free();
    type_oy.free();
    type_ox.free();
    type_ic.free();
    type_ib.free();
    type_ia.free();

    MPI.Finalize();
  }


  private static class MyMatrix3D
  {
    private int buf[];
    
    private MyMatrix3D()
    {
      /* "int imessage[10][10][10];", ...	*/
      buf = new int[10 * 10 * 10];
    }
    
    private static int address(int i, int j, int k)
    {
      return 100*i + 10*j + k;
    }
    
    private int get(int i, int j, int k)
    {
      return buf[address(i, j, k)];
    }
    
    private void put(int i, int j, int k, int v)
    {
      buf[address(i, j, k)] = v;
    }
    
    private int[] buf()
    {
      return buf;
    }
    
    private void initialize()
    {
      for(int i = 0; i < 10; i++)
      {
	for(int j = 0; j < 10; j++)
	{
	  for(int k = 0; k < 10; k++)
	  {
	    /* "omessage[i][j][k] = i*100 + j*10 + k;", ...	*/
	    put(i,j,k, i*100 + j*10 + k);
	  }
	}
      }
    }
    
    private void testTransp(MyMatrix3D m) throws MPIException
    {
      for(int i = 0; i < 10; i++)
      {
	for(int j = 0; j < 10; j++)
	{
	  for(int k = 0; k < 10; k++)
	  { 
	    if(get(i,j,k) != m.get(k,j,i))
	    {
	      OmpitestError.ompitestError(OmpitestError.getFileName(),
					  OmpitestError.getLineNumber(),
					  "OOPS i=" + i + ", j="+ j +
					  ", k=" + k +
					  ", omessage[...] = " +
					  get(i,j,k) +
					  ", imessage[...] = " +
					  m.get(k,j,i) + ". \n");
	    }
	  }
	}
      }
    }
  } // MyMatrix3D
}
