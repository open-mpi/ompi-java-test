reset
#set terminal x11
set terminal windows
set key left top Left reverse
set nolabel
set border 3
set lmargin 12
set rmargin 12
set xtics 10000
set ytics 10
set title "Time to send 1000 times a buffer of \"int\" values"
set xlabel "Number of \"int\" values in buffer"
set ylabel "CPU time in seconds"
set xrange [0:100000]
plot "c_Linux_2_tasks.dat" with lines,\
"c_Linux_4_tasks.dat" with lines,\
"c_Linux_8_tasks.dat" with lines
replot
set terminal latex
set output "linpc1_msgSize_c.tex"
replot
set terminal png
set output "linpc1_msgSize_c.png"
replot
pause 5
