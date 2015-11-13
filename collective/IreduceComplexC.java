/* 
 *
 * This file is a port from "ireduce-complex-c.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: IreduceComplexC.java			Author: S. Gross
 *
 */

import java.nio.*;
import mpi.*;

public class IreduceComplexC
{
  public static void main (String args[]) throws MPIException
  {
    MPI.Init(args);
    int rank = MPI.COMM_WORLD.getRank();
    int size = MPI.COMM_WORLD.getSize();
    FloatComplex fc;

    FloatBuffer fBufIn  = MPI.newFloatBuffer(2),
	        fBufOut = MPI.newFloatBuffer(2);

    FloatComplex fcIn  = FloatComplex.get(fBufIn),
                 fcOut = FloatComplex.get(fBufOut);

    DoubleBuffer dBufIn  = MPI.newDoubleBuffer(2),
	         dBufOut = MPI.newDoubleBuffer(2);
    
    DoubleComplex dcIn  = DoubleComplex.get(dBufIn),
                  dcOut = DoubleComplex.get(dBufOut);

    fcIn.putReal(1.0F);
    fcIn.putImag(1.0F);

    fcOut.putReal(0.0F);
    fcOut.putImag(0.0F);
    
    Request request = MPI.COMM_WORLD.iReduce(fBufIn, fBufOut, 1,
				             MPI.FLOAT_COMPLEX, MPI.SUM, 0);
    request.waitFor();
    request.free();
    
    if (0 == rank) {
      if ((int)fcOut.getReal() != size ||
	  (int)fcOut.getImag() != size) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Bad result for SUM of complex: " +
				    "got (" + fcOut.getReal() +
				    " / " + fcOut.getImag() +
				    "), expected (" +
				    size + " / " + size + ")\n");
      }
    }

    fcOut.putReal(0.0F);
    fcOut.putImag(0.0F);
    
    request = MPI.COMM_WORLD.iReduce(fBufIn, fBufOut, 1,
				     MPI.FLOAT_COMPLEX, MPI.SUM, 0);
    request.waitFor();
    request.free();
    
    if (0 == rank) {
      if ((int)fcOut.getReal() != size ||
	  (int)fcOut.getImag() != size) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Bad result for SUM of complex: " +
				    "got (" + fcOut.getReal() +
				    " / " + fcOut.getImag() +
				    "), expected (" +
				    size + " / " + size + ")\n");
      }
    }
    
    dcIn.putReal(1.0);
    dcIn.putImag(1.0);

    dcOut.putReal(0.0);
    dcOut.putImag(0.0);
    
    request = MPI.COMM_WORLD.iReduce(dBufIn, dBufOut, 1,
				     MPI.DOUBLE_COMPLEX, MPI.SUM, 0);
    request.waitFor();
    request.free();
    
    if (0 == rank) {
      if ((int)dcOut.getReal() != size ||
	  (int)dcOut.getImag() != size) {
	OmpitestError.ompitestError(OmpitestError.getFileName(),
				    OmpitestError.getLineNumber(),
				    "Bad result for SUM of complex: " +
				    "got (" + dcOut.getReal() +
				    " / " + dcOut.getImag() +
				    "), expected (" +
				    size + " / " + size + ")\n");
      }
    }

    /* "long double" isn't available in JAVA
     *
     *    ldc_in.real = 1;
     *    ldc_in.imag = 1;
     *    MPI_Reduce(&ldc_in, &ldc_out, 1, MPI_C_LONG_DOUBLE_COMPLEX, 
     *	        MPI_SUM, 0, MPI_COMM_WORLD);
     *    if (0 == rank) {
     *      if (ldc_out.real != size || ldc_out.imag != size) {
     *	OmpitestError.ompitestError(OmpitestError.getFileName(),
     *				    OmpitestError.getLineNumber(),
     *				    "Bad result for SUM of complex: " +
     *				    "got " + ldc_out.real + " / " +
     *				    ldc_out.imag + ", expected " +
     *				    size + " / " + size + "\n");
     *      }
     *    }
     */
    
    MPI.Finalize();
  }
}
