reset
set terminal x11
#set terminal windows
set key left top Left reverse font ",7"  samplen 2 spacing 0.8
set nolabel
set border 3
set lmargin 12
set rmargin 12
set xtics 2
#set ytics 10
set logscale y
set title "CPU time to send 1000 times a buffer of \"int\" values"
set xlabel "Number of processes in MPI_COMM_WORLD"
set ylabel "CPU time in seconds"
set style data linespoints
set pointsize 0.8
set xrange [0:24]
plot "c_Fedora_shm_10000_intValues.dat",\
"c_Fedora_shm_50000_intValues.dat",\
"c_Fedora_shm_100000_intValues.dat",\
"java_Fedora_shm_10000_intValues.dat",\
"java_Fedora_shm_50000_intValues.dat",\
"java_Fedora_shm_100000_intValues.dat", \
"c_Fedora_net_10000_intValues.dat",\
"c_Fedora_net_50000_intValues.dat",\
"c_Fedora_net_100000_intValues.dat",\
"java_Fedora_net_10000_intValues.dat",\
"java_Fedora_net_50000_intValues.dat",\
"java_Fedora_net_100000_intValues.dat"
replot
set terminal pdf
set output "fedora_numProc_c_java_cputime.pdf"
replot
pause 5
