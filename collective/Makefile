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
	Allgather.class \
	AllgatherInPlace.class \
	Allgatherv.class \
	AllgathervInPlace.class \
	Allreduce.class \
	AllreduceInPlace.class \
	Alltoall.class \
	Alltoallv.class \
	Barrier.class \
	Bcast.class \
	Exscan.class \
	ExscanInPlace.class \
	Gather.class \
	GatherInPlace.class \
	Gatherv.class \
	GathervInPlace.class \
	Iallgather.class \
	IallgatherInPlace.class \
	Iallgatherv.class \
	IallgathervInPlace.class \
	Iallreduce.class \
	IallreduceInPlace.class \
	Ialltoall.class \
	Ialltoallv.class \
	Ibarrier.class \
	Ibcast.class \
	Iexscan.class \
	IexscanInPlace.class \
	Igather.class \
	IgatherInPlace.class \
	Igatherv.class \
	IgathervInPlace.class \
	Ireduce.class \
	IreduceBig.class \
	IreduceComplexC.class \
	IreduceInPlace.class \
	IreduceLoc.class \
	IreduceScatter.class \
	IreduceScatterBlock.class \
	IreduceScatterBlockInPlace.class \
	Iscan.class \
	IscanInPlace.class \
	Iscatter.class \
	IscatterInPlace.class \
	Iscatterv.class \
	IscattervInPlace.class \
	OpTest.class \
	Reduce.class \
	ReduceBig.class \
	ReduceComplexC.class \
	ReduceInPlace.class \
	ReduceLoc.class \
	ReduceScatter.class \
	ReduceScatterBlock.class \
	ReduceScatterBlockInPlace.class \
	ReduceScatterInPlace.class \
	Scan.class \
	ScanInPlace.class \
	Scatter.class \
	ScatterInPlace.class \
	Scatterv.class \
	ScattervInPlace.class


all:
	$(MAKE) $(EXAMPLES);

# The usual "clean" target

clean:
	rm -f $(EXAMPLES) *~ *.o

# Don't rely on default rules

Allgather.class: Allgather.java
	$(JAVAC) Allgather.java

AllgatherInPlace.class: AllgatherInPlace.java
	$(JAVAC) AllgatherInPlace.java

Allgatherv.class: Allgatherv.java
	$(JAVAC) Allgatherv.java

AllgathervInPlace.class: AllgathervInPlace.java
	$(JAVAC) AllgathervInPlace.java

Allreduce.class: Allreduce.java
	$(JAVAC) Allreduce.java

AllreduceInPlace.class: AllreduceInPlace.java
	$(JAVAC) AllreduceInPlace.java


Alltoall.class: Alltoall.java
	$(JAVAC) Alltoall.java

Alltoallv.class: Alltoallv.java
	$(JAVAC) Alltoallv.java

Barrier.class: Barrier.java
	$(JAVAC) Barrier.java

Bcast.class: Bcast.java
	$(JAVAC) Bcast.java

Exscan.class: Exscan.java
	$(JAVAC) Exscan.java

ExscanInPlace.class: ExscanInPlace.java
	$(JAVAC) ExscanInPlace.java

Gather.class: Gather.java
	$(JAVAC) Gather.java

GatherInPlace.class: GatherInPlace.java
	$(JAVAC) GatherInPlace.java

Gatherv.class: Gatherv.java
	$(JAVAC) Gatherv.java

GathervInPlace.class: GathervInPlace.java
	$(JAVAC) GathervInPlace.java

Iallgather.class: Iallgather.java
	$(JAVAC) Iallgather.java

IallgatherInPlace.class: IallgatherInPlace.java
	$(JAVAC) IallgatherInPlace.java

Iallgatherv.class: Iallgatherv.java
	$(JAVAC) Iallgatherv.java

IallgathervInPlace.class: IallgathervInPlace.java
	$(JAVAC) IallgathervInPlace.java

Iallreduce.class: Iallreduce.java
	$(JAVAC) Iallreduce.java

IallreduceInPlace.class: IallreduceInPlace.java
	$(JAVAC) IallreduceInPlace.java

Ialltoall.class: Ialltoall.java
	$(JAVAC) Ialltoall.java

Ialltoallv.class: Ialltoallv.java
	$(JAVAC) Ialltoallv.java

Ibarrier.class: Ibarrier.java
	$(JAVAC) Ibarrier.java

Ibcast.class: Ibcast.java
	$(JAVAC) Ibcast.java

Iexscan.class: Iexscan.java
	$(JAVAC) Iexscan.java

IexscanInPlace.class: IexscanInPlace.java
	$(JAVAC) IexscanInPlace.java

Igather.class: Igather.java
	$(JAVAC) Igather.java

IgatherInPlace.class: IgatherInPlace.java
	$(JAVAC) IgatherInPlace.java

Igatherv.class: Igatherv.java
	$(JAVAC) Igatherv.java

IgathervInPlace.class: IgathervInPlace.java
	$(JAVAC) IgathervInPlace.java

Ireduce.class: Ireduce.java
	$(JAVAC) Ireduce.java

IreduceBig.class: IreduceBig.java
	$(JAVAC) IreduceBig.java

IreduceComplexC.class: IreduceComplexC.java
	$(JAVAC) IreduceComplexC.java

IreduceInPlace.class: IreduceInPlace.java
	$(JAVAC) IreduceInPlace.java

IreduceLoc.class: IreduceLoc.java
	$(JAVAC) IreduceLoc.java

IreduceScatter.class: IreduceScatter.java
	$(JAVAC) IreduceScatter.java

IreduceScatterBlock.class: IreduceScatterBlock.java
	$(JAVAC) IreduceScatterBlock.java

IreduceScatterBlockInPlace.class: IreduceScatterBlockInPlace.java
	$(JAVAC) IreduceScatterBlockInPlace.java

Iscan.class: Iscan.java
	$(JAVAC) Iscan.java

IscanInPlace.class: IscanInPlace.java
	$(JAVAC) IscanInPlace.java

Iscatter.class: Iscatter.java
	$(JAVAC) Iscatter.java

IscatterInPlace.class: IscatterInPlace.java
	$(JAVAC) IscatterInPlace.java

Iscatterv.class: Iscatterv.java
	$(JAVAC) Iscatterv.java

IscattervInPlace.class: IscattervInPlace.java
	$(JAVAC) IscattervInPlace.java

OpTest.class: OpTest.java
	$(JAVAC) OpTest.java

Reduce.class: Reduce.java
	$(JAVAC) Reduce.java

ReduceBig.class: ReduceBig.java
	$(JAVAC) ReduceBig.java

ReduceComplexC.class: ReduceComplexC.java
	$(JAVAC) ReduceComplexC.java

ReduceInPlace.class: ReduceInPlace.java
	$(JAVAC) ReduceInPlace.java

ReduceLoc.class: ReduceLoc.java
	$(JAVAC) ReduceLoc.java

ReduceScatter.class: ReduceScatter.java
	$(JAVAC) ReduceScatter.java

ReduceScatterBlock.class: ReduceScatterBlock.java
	$(JAVAC) ReduceScatterBlock.java

ReduceScatterBlockInPlace.class: ReduceScatterBlockInPlace.java
	$(JAVAC) ReduceScatterBlockInPlace.java

ReduceScatterInPlace.class: ReduceScatterInPlace.java
	$(JAVAC) ReduceScatterInPlace.java

Scan.class: Scan.java
	$(JAVAC) Scan.java

ScanInPlace.class: ScanInPlace.java
	$(JAVAC) ScanInPlace.java

Scatter.class: Scatter.java
	$(JAVAC) Scatter.java

ScatterInPlace.class: ScatterInPlace.java
	$(JAVAC) ScatterInPlace.java

Scatterv.class: Scatterv.java
	$(JAVAC) Scatterv.java

ScattervInPlace.class: ScattervInPlace.java
	$(JAVAC) ScattervInPlace.java

