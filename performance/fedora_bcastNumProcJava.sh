#!/usr/bin/sh
#
# Measures the time to broadcast 1000 packets of the specified size
# (10000, 50000, 100000) to the specified number of processes
# (2, 4, 6, ...). This shell script varies the number of processes
# for a few message sizes.
#
# ${DIRPREFIX_LOCAL} depends on the machine, e.g.,
# SunOS sparc:  ${HOME}/SunOS/sparc
# SunOS x86_64: ${HOME}/SunOS/x86_64
# Linux x86_64: ${HOME}/Linux/x86_64
# Cygwin x86:   ${HOME}/Cygwin/x86
#
# The results will be plotted with Gnuplot.
# x-axis: number of processes, y-axis: time,
# one curve for each message size or each machine architecture
# in one graphic
#

# sunpc0/1: Sun Ultra 40, 2 dual-core AMD Opteron 280 processors,
#	    Solaris 10 x86_64, 2.4 GHz, 8 GB RAM,
#	    network datarate about 25 MB/s
# linpc0/1: Sun Ultra 40, 2 dual-core AMD Opteron 280 processors,
#	    openSuSE Linux 12.1 x86_64, 2.4 GHz, 8 GB RAM,
#	    network datarate about 50 MB/s
# rs0/1:    Sun SPARC Enterprise M4000 Server, 2 quad-core
#	    SPARC64-VII processors, Solaris 10 sparc, 2.4 GHz,
#	    32 GB RAM, network datarate about 20 MB/s
# cae25, cae29: Dell Precision T5600, 2 six-core Intel Xeon E5-2630
#		processors, Fedora Linux 18, 2.3 GHz, 64 GB RAM,
#		graphics: 2 GB Nvidia Quadro 4000,
#		network datarate about 95 MB/s
#

CLASSFILE_DIR=${DIRPREFIX_LOCAL}/mpi_classfiles

#HOST_NAMES=""
#HOST_NAMES="-host cae25"
HOST_NAMES="-host cae25,cae29"
#HOST_NAMES="-host linpc1"
#HOST_NAMES="-host linpc0,linpc1"
#HOST_NAMES="-host linpc0,linpc1,sunpc0,sunpc1"
#HOST_NAMES="-host sunpc1"
#HOST_NAMES="-host sunpc0,sunpc1"
#HOST_NAMES="-host rs1"
#HOST_NAMES="-host rs0,rs1"

MPI_OPTIONS="-bind-to core -bynode --mca btl_tcp_eager_limit 8388608"
#MPI_OPTIONS="-report-bindings -bind-to core -bynode"

# "Fedora_x86_64_tcp" isn't possible, because the filename causes a
# segmentation fault using Java. Why?

#OUTPUT_FILENAME="Fedora_x86_64_shm"
OUTPUT_FILENAME="Fedora_x86_64_net"
#OUTPUT_FILENAME="Linux_x86_64_shm"
#OUTPUT_FILENAME="Linux_x86_64_tcp"
#OUTPUT_FILENAME="Linux_SunOS_x86_64_tcp"
#OUTPUT_FILENAME="SunOS_x86_64_shm"
#OUTPUT_FILENAME="SunOS_x86_64_tcp"
#OUTPUT_FILENAME="SunOS_sparc_shm"
#OUTPUT_FILENAME="SunOS_sparc_tcp"

MESSAGE_SIZES="10000 \
50000 \
100000"

NUM_PROCS="2 \
4 \
6 \
8 \
10 \
12 \
14 \
16 \
18 \
20 \
22 \
24"

# compile the program
mpijavac -d ${CLASSFILE_DIR} BcastPerformanceNumProc.java

# create files for Gnuplot
for np in ${NUM_PROCS}; do
  for ms in ${MESSAGE_SIZES}; do
    echo =========== np: ${np}, message size: ${ms} ===========
    mpiexec -np ${np} ${HOST_NAMES} ${MPI_OPTIONS} \
      java BcastPerformanceNumProc ${ms} ${OUTPUT_FILENAME}
  done
done

# clean up
rm ${CLASSFILE_DIR}/BcastPerformanceNumProc.class
