reset
set terminal x11
#set terminal windows
set key left top Left reverse font ",7"  samplen 2 spacing 0.8
set nolabel
set border 3
set lmargin 12
set rmargin 12
#set xtics 10000
#set ytics 10
set logscale y
set title "Elapsed time to send 1000 times a buffer of \"int\" values"
set xlabel "Number of \"int\" values in buffer"
set ylabel "Elapsed time in seconds"
set style data linespoints
set pointsize 0.8
set xrange [0:100000]
plot "c_Fedora_shm_4_tasks.dat",\
"c_Fedora_shm_8_tasks.dat",\
"java_Fedora_shm_4_tasks.dat",\
"java_Fedora_shm_8_tasks.dat", \
"c_Fedora_net_4_tasks.dat",\
"c_Fedora_net_8_tasks.dat",\
"java_Fedora_net_4_tasks.dat",\
"java_Fedora_net_8_tasks.dat"
replot
set terminal pdf
set output "fedora_msgSize_c_java_elapsedtime.pdf"
replot
pause 5
