/* 
 *
 * This file is a port from "self_atexit.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: SelfAtexit.java		Author: S. Gross
 *
 */

import mpi.*;

public class SelfAtexit
{
  private static int ret = 1;

  public static void main (String args[]) throws MPIException
  {
    int me, tasks;
    int key[] = new int[1],
        intval[] = new int[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.getRank();
    tasks = MPI.COMM_WORLD.getSize();


    /* Create a keyval with an associated delete function */
    MPI_Keyval_create(MPI.NULL_COPY_FN,
    		      DeleteCallBackFunction delFunction, key, 0);

    /* Now attach an arbitrary attribute on that keyval on
     * MPI_COMM_SELF
     */
    intval[0] = 17;

    MPI.COMM_SELF.setAttr(key[0],intval);

    /* The keyval delete function should be triggered during
     * MPI_Finalize()
     */
    MPI.Finalize();
  }


  public interface DeleteCallBackFunction {
    int deleteFunction(Comm comm, int keyval,
		       Object attribute_val, Object extra_state);
  }


  public abstract class AbstractDeleteCallBackFunctionClass {
    public abstract int delFunction(Comm comm, int keyval,
				    Object attribute_val,
				    Object extra_state);
  }


  class DeleteFunction extends AbstractDeleteCallBackFunctionClass {
    public int delFunction(Comm comm, int keyval,
			   Object attribute_val, 
			   Object extra_state)
    {
      System.out.printf("delete_function() has been called.\n");
      ret = 0;
      return 0;
    }
  }
}
