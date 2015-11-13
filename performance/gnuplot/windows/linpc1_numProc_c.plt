reset
#set terminal x11
set terminal windows
set key left top Left reverse
set nolabel
set border 3
set lmargin 12
set rmargin 12
set xtics 2
set ytics 10
set title "Time to send 1000 times a buffer of \"int\" values"
set xlabel "Number of processes in MPI_COMM_WORLD"
set ylabel "CPU time in seconds"
set xrange [0:16]
plot "c_Linux_10000_intValues.dat" with lines,\
"c_Linux_50000_intValues.dat" with lines,\
"c_Linux_100000_intValues.dat" with lines
replot
set terminal latex
set output "linpc1_numProc_c.tex"
replot
set terminal png
set output "linpc1_numProc_c.png"
replot
pause 5
