/* 
 *
 * This file is a port from "60_info_env.c" from the "ompi-ibm-10.0"
 * regression test package. The formatting of the code is
 * mainly the same as in the original file.
 *
 *
 * File: InfoEnv60.java			Author: S. Gross
 *
 */

import java.net.*;
import mpi.*;

public class InfoEnv60
{
  public static void main (String args[]) throws MPIException,
						 UnknownHostException
  {
    int size;
    String value, str, ptr;

    MPI.Init(args);
    size = MPI.COMM_WORLD.getSize();

    /* Check that the "command" key value is the same as our argv[0].
     * Java doesn't have the name of the command in args[0], so that
     * "MPI_Info_get()" cannot return it. The Java API could return
     * the classname for method "main()", which I will assume.
     * Otherwise we must remove the following code.
     *
     * The C program tests, if "command" returns the "basename" for
     * the command, while argv[0] may have an absolute filename. This
     * cannot happen in Java, so that I removed this part of the code.
     */
    value = get("command");
    if (!value.equals(OmpitestError.getClassName())) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Expected class \"" +
				  OmpitestError.getClassName() +
				  "\", but got \"" + value + "\"\n");
    }

    /* maxprocs */
    value = get("maxprocs");
    if (Integer.parseInt(value) != size) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Expected maxprocs \"" + size +
				  "\", but got \"" + value + "\"\n");
    }

    /* np */
    value = get("ompi_np");
    if (Integer.parseInt(value) != size) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Expected np \"" + size +
				  "\", but got \"" + value + "\"\n");
    }

    /* host */
    value = get("host");
    str = InetAddress.getLocalHost().getHostName();
    if (!value.equals(str)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Expected host \"" + str +
				  "\", but got \"" + value + "\"\n");
    }

    /* arch, num_app_ctx, first_rank (just ensure it's there; don't
     * care what it is because we don't have anything easily to
     * compare it to)
     */
    value = get("arch");
    value = get("ompi_num_apps");
    value = get("ompi_first_rank");

    /* wdir.  OMPI uses $PWD if it is set, or getcwd() if not. */
    value = get("wdir");
    ptr = System.getenv("PWD");
    if (null == ptr) {
      /* getcwd(str, sizeof(str)); */
      ptr = System.getProperty("user.dir");
    }
    if (!value.equals(ptr)) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Expected wdir \"" + ptr +
				  "\", but got \"" + value + "\"\n");
    }

    MPI.Finalize();
  }


  private static String get(String key) throws MPIException
  {
    String value = MPI.INFO_ENV.get(key);

    if (value == null) {
      OmpitestError.ompitestError(OmpitestError.getFileName(),
				  OmpitestError.getLineNumber(),
				  "Should have gotten flag==1 for key " +
				  key + "\n");
      /* Does not return */
    }
    return value;
  }
}
