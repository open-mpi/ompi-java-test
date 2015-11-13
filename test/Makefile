#
# Copyright (c) 2004-2005 The Trustees of Indiana University and Indiana
#                         University Research and Technology
#                         Corporation.  All rights reserved.
# Copyright (c) 2004-2005 The University of Tennessee and The University
#                         of Tennessee Research Foundation.  All rights
#                         reserved.
# Copyright (c) 2004-2005 High Performance Computing Center Stuttgart, 
#                         University of Stuttgart.  All rights reserved.
# Copyright (c) 2004-2005 The Regents of the University of California.
#                         All rights reserved.
# Copyright (c) 2006-2007 Sun Microsystems, Inc.  All rights reserved.
# Copyright (c) 2011-2012 Cisco Systems, Inc.  All rights reserved.
# Copyright (c) 2012      Los Alamos National Security, Inc.  All rights reserved.
# Copyright (c) 2013      Mellanox Technologies, Inc.  All rights reserved.
# Copyright (c) 2014      Intel, Inc.  All rights reserved.
# $COPYRIGHT$
# 
# Additional copyrights may follow
# 
# $HEADER$
#

# Use the Open MPI-provided wrapper compilers

JAVAC = mpijavac -cp .:../reporting

# Programs to build

EXAMPLES = \
	TestCheckSize1.class \
	TestCheckSize2.class \
	TestCheckSize3.class \
	TestCheckSize4.class \
	TestError1.class \
	TestError2.class \
	TestError3.class \
	TestError4.class \
	TestNeedEven1.class \
	TestNeedEven2.class \
	TestNeedEven3.class \
	TestProgress.class \
	TestWarning1.class \
	TestWarning2.class \
	TestWarning3.class \
	TestWarning4.class

all:
	@ if ompi_info --parsable | grep bindings:java:yes >/dev/null; then \
	    $(MAKE) TestCheckSize1.class \
		    TestCheckSize2.class \
		    TestCheckSize3.class \
		    TestCheckSize4.class \
		    TestError1.class \
		    TestError2.class \
		    TestError3.class \
		    TestError4.class \
		    TestNeedEven1.class \
		    TestNeedEven2.class \
		    TestNeedEven3.class \
		    TestProgress.class \
		    TestWarning1.class \
		    TestWarning2.class \
		    TestWarning3.class \
		    TestWarning4.class; \
	fi

# The usual "clean" target

clean:
	rm -f $(EXAMPLES) *~ *.o

# Don't rely on default rules

TestCheckSize1.class: TestCheckSize1.java
	$(JAVAC) TestCheckSize1.java
TestCheckSize2.class: TestCheckSize2.java
	$(JAVAC) TestCheckSize2.java
TestCheckSize3.class: TestCheckSize3.java
	$(JAVAC) TestCheckSize3.java
TestCheckSize4.class: TestCheckSize4.java
	$(JAVAC) TestCheckSize4.java

TestError1.class: TestError1.java
	$(JAVAC) TestError1.java
TestError2.class: TestError2.java
	$(JAVAC) TestError2.java
TestError3.class: TestError3.java
	$(JAVAC) TestError3.java
TestError4.class: TestError4.java
	$(JAVAC) TestError4.java

TestNeedEven1.class: TestNeedEven1.java
	$(JAVAC) TestNeedEven1.java
TestNeedEven2.class: TestNeedEven2.java
	$(JAVAC) TestNeedEven2.java
TestNeedEven3.class: TestNeedEven3.java
	$(JAVAC) TestNeedEven3.java
TestNeedEven4.class: TestNeedEven4.java
	$(JAVAC) TestNeedEven4.java

TestProgress.class: TestProgress.java
	$(JAVAC) TestProgress.java

TestWarning1.class: TestWarning1.java
	$(JAVAC) TestWarning1.java
TestWarning2.class: TestWarning2.java
	$(JAVAC) TestWarning2.java
TestWarning3.class: TestWarning3.java
	$(JAVAC) TestWarning3.java
TestWarning4.class: TestWarning4.java
	$(JAVAC) TestWarning4.java
