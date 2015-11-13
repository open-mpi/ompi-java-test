# -*- shell-script -*- 
# 
# Copyright (c) 2013 Cisco Systems, Inc.  All rights reserved.
# $COPYRIGHT$
# 
# Additional copyrights may follow
# 
# $HEADER$
#  

#
# Init autoconf
#

AC_PREREQ([2.68])
AC_INIT([ompi-java], [1.0], [http://www.open-mpi.org], [ompi-java])
AC_CONFIG_AUX_DIR([config])
AC_CONFIG_MACRO_DIR([config])

echo "Configuring Open Java test suite"

AM_INIT_AUTOMAKE(1.12 foreign)

# If Automake supports silent rules, enable them.
m4_ifdef([AM_SILENT_RULES], [AM_SILENT_RULES([yes])])

#
# Make automake clean emacs ~ files for "make clean"
#

CLEANFILES="*~"
AC_SUBST(CLEANFILES)

#
# Get various programs
# Bias towards mpijava
#

if test "$JAVAC" != ""; then
    BASE="`basename $JAVAC`"
else
    BASE=
fi
if test "$BASE" = "" -o "$BASE" = "." -o "$BASE" = "javac"; then
    AC_CHECK_PROG(HAVE_MPIJAVAC, mpijavac, yes, no)
    if test "$HAVE_MPIJAVAC" = "yes"; then
        JAVAC=mpijavac
        export JAVAC
    fi
fi

#
# Is OMPI checking MPI API params?
#

AC_MSG_CHECKING([if checking MPI API params])
AH_TEMPLATE(OMPITEST_CHECKING_MPI_API_PARAMS, [Checking MPI API params or not])
which ompi_info > /dev/null 2>&1
if test "$?" = "0"; then
    CHECKING_MPI_API_PARAM=`ompi_info --parseable | grep option:mpi-param-check: |cut -d: -f3`
    if test "$CHECKING_MPI_API_PARAM" = " yes" -o "$CHECKING_MPI_API_PARAM" = "runtime"; then
         OMPITEST_CHECKING_MPI_API_PARAMS=1
         AC_MSG_RESULT([yes])
    else
         OMPITEST_CHECKING_MPI_API_PARAMS=0
         AC_MSG_RESULT([no])
    fi
else
    # If this isn't OMPI, then assume we're checking API params
    OMPITEST_CHECKING_MPI_API_PARAMS=1
    AC_MSG_RESULT([yes (not Open MPI; cannot check)])
fi
OMPI_PARAM_CHECK=1
OMPITEST_HAVE_MPI_THREADS=0
HAVE_DIST_GRAPH=0
OMPI_HAVE_MPI_EXT_AFFINITY=0
OMPITEST_HAVE_MPI_THREADS=0
AC_SUBST(OMPITEST_CHECKING_MPI_API_PARAMS)
AC_SUBST(OMPI_PARAM_CHECK)
AC_SUBST(HAVE_DIST_GRAPH)
AC_SUBST(OMPI_HAVE_MPI_EXT_AFFINITY)
AC_SUBST(OMPITEST_HAVE_MPI_THREADS)
AC_CONFIG_FILES([reporting/OmpitestConfig.java])

#
# Setup the mpirun args and the like
#

START_TEST=""
MPIRUN="mpirun -np %np %hostfile %mode java %cmd"
MODES=""
HOSTFILE=""
AC_SUBST(START_TEST)
AC_SUBST(MPIRUN)
AC_SUBST(POST_MPIRUN)
AC_SUBST(MODES)
AC_SUBST(HOSTFILE)

#
# Party on
#

AC_CONFIG_FILES([
    Makefile
])
AC_OUTPUT
