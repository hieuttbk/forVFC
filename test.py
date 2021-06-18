import psutil
import os
from os import path 

if path.exists("monitor.txt"):
	os.system("rm -f /home/pi/test2/monitor.txt") 
monitor = open("monitor.txt", "a")
while True:
	cpu_percent = psutil.cpu_percent(interval=1)
	print('RAM memory % used:', psutil.virtual_memory()[2])
	print(cpu_percent)
	monitor.write(str(cpu_percent) + " " + str(psutil.virtual_memory()[2]) +"\n")
